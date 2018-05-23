package app.xml;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.diff.*;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.xpath.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class XMLComparator {

    private static final Logger log = LoggerFactory.getLogger(XMLComparator.class);

    private static String lineNumAttribName = "lineNumber";
    private static String columnNumAttribName = "columnNumber";


    /**
     * @param is входной поток
     * @return Документ в котором в каждом ноде в UserData записан номер строки.
     * Аттрибут UserData указывается в конструкторе реализации интерфейса
     * @throws SAXException
     * @throws IOException
     */
    private static Document parseXML(InputStream is)
            throws SAXException, IOException, ParserConfigurationException {
        CompareHandler handler = new CompareHandler(lineNumAttribName, columnNumAttribName);

        SAXParser parser = SAXParserFactory.newInstance().newSAXParser();

        parser.parse(is, handler);

        return handler.getDocument();
    }

    /**
     * @param list список пар трансформаций
     * @return список результатов сравнения трансформаций
     * @throws IOException
     * @throws SAXException
     */
    public static List<XMLCompareResult> diff(List<MyPair<DocumentSource>> list)
            throws IOException, SAXException, ParserConfigurationException {

        List<XMLCompareResult> reports = Collections.EMPTY_LIST;

        for (MyPair<DocumentSource> pair: list ) {
            reports.add(diff(new ByteArrayInputStream(pair.getFirst().getStream()),
                    new ByteArrayInputStream(pair.getSecond().getStream())));
        }

        return reports;
    }

    /**
     * @param inputControl
     * @param inputTest
     * @return
     * @throws IOException
     * @throws SAXException
     */

    private static XMLCompareResult diff(InputStream inputControl, InputStream inputTest)
            throws IOException, SAXException, ParserConfigurationException {
        Document control = parseXML(inputControl);
        Document test = parseXML(inputTest);
        return diff(control, test);
    }


    /**
     * Сравнение двух трансформация
     *
     * @param first
     * @param second
     * @return результат сравнения
     */
    private static XMLCompareResult diff(Document first, Document second) {
        XMLCompareResult result = new XMLCompareResult();
        result.setErrors(new ArrayList<MyPair<XMLError>>());
        result.setWarnings(new ArrayList<MyPair<XMLError>>());

        Diff detDiff = DiffBuilder.
                compare(first).
                withTest(second).
                withNodeMatcher(new DefaultNodeMatcher(ElementSelectors.byNameAndText)).build();

        for (Difference difference : detDiff.getDifferences()) {
            if (difference.getResult() == ComparisonResult.SIMILAR) {
                addFault(first, second, result.getWarnings(), difference);
            } else if (difference.getResult() == ComparisonResult.DIFFERENT) {
                addFault(first, second, result.getErrors(), difference);
            }
        }
        clearExtraErrors(result.getErrors());

        return result;
    }

    /**
     * Удалем лишние ошибки об аттрибутах
     * @param error
     * @return
     */
    private static boolean hasAttrAndTextErrors(MyPair<XMLError> error) {
        String xpathFirst = error.getFirst().getXpath();
        String xpathSecond = error.getSecond().getXpath();
        if (xpathFirst != null && xpathSecond != null && xpathFirst.equals(xpathSecond) &&
                !xpathFirst.contains("@") && !xpathFirst.contains("()")) {
            return true;
        }
        return false;
    }

    /**
     * удаляем ошибки связанные с неправильным временем в заголовке
     * @param error
     * @return
     */
    private static boolean hasAppHdrErrors(MyPair<XMLError> error) {

        String xpathFirst = error.getFirst().getXpath();
        String xpathSecond = error.getSecond().getXpath();
        if (xpathFirst != null && xpathSecond != null) {
            Element firstNode = (Element) error.getFirst().getNode();
            Element secondNode = (Element) error.getSecond().getNode();
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH);

            try {
                Date date1 = format.parse(firstNode.getTextContent().replaceFirst("Z$", ""));
                Date date2 = format.parse(secondNode.getTextContent().replaceFirst("Z$", ""));
                if (date2.getTime() - date1.getTime() < 1000 * 60 * 30) { // 30 минут
                    return true;
                }
            } catch (ParseException e) {
                // ничего не делаем, т.к. этим исключением выполняется проверка на дату в теге.
                return false;
            }
        }
        return false;
    }

    /**
     * Удаление лишних сообщений об ошибках.
     * Когда xmlunit возвращает ошибку в элементе, хотя ошибка в его дочерних элементах.
     * В этом случаи сообщение об ошибке удаляется из списка.
     *
     * @param errors список ошибок
     */
    private static void clearExtraErrors(List<MyPair<XMLError>> errors) {
        Iterator<MyPair<XMLError>> iterator = errors.iterator();
        while (iterator.hasNext()) {
            MyPair<XMLError> error = iterator.next();

            if (hasAttrAndTextErrors(error)) {
                iterator.remove();
                continue;
            }

            if (hasAppHdrErrors(error)) {
                iterator.remove();
                continue;
            }
        }
    }

    /**
     * Добавление несоответствие в трансформациях (ошибка или предупреждение)
     *
     * @param docFirst
     * @param docSecond
     * @param faults     список несоответствий. Может быть списком ошибок или предупреждений.
     * @param difference результат сравнения двух документов, переданных в первых 2-х параметрах
     */
    private static void addFault(Document docFirst, Document docSecond,
                                 List<MyPair<XMLError>> faults,
                                 Difference difference) {
        XMLError faultControl = new XMLError();
        XMLError faultTest = new XMLError();

        if (difference.getComparison().getControlDetails() != null
                && difference.getComparison().getControlDetails().getXPath() != null) {
            String path = difference.getComparison().getControlDetails().getXPath();
            faultControl = fillFault(docFirst, path, ErrorType.NOT_XML);
        }
        if (difference.getComparison().getTestDetails() != null
                && difference.getComparison().getTestDetails().getXPath() != null) {
            String path = difference.getComparison().getTestDetails().getXPath();
            faultTest = fillFault(docSecond, path, ErrorType.NOT_XML);
        }

        faults.add(new MyPair<XMLError>(faultControl, faultTest));
    }

    /**
     * Заполняется структура несоответствия
     *
     * @param doc
     * @param path Путь в виде xpath до элемента.
     * @return заполненное несоответствие
     */
    private static XMLError fillFault(Document doc, String path, ErrorType type) {
        XMLError fault = new XMLError(type);
        fault.setXpath(path);

        Node node = findNodeByXPath(doc, removeAttrAndTextFromXPath(path));
        fault.setNode(node);
        fault.setLineNumber(getUserDataInt(node, lineNumAttribName));
        fault.setColumnNumber(getUserDataInt(node, columnNumAttribName));

        return fault;
    }

    /**
     * Получить элемент по его xpath
     *
     * @param doc  документ в котором есть элемент
     * @param path его путь
     * @return элемент
     */
    private static Node findNodeByXPath(Document doc, String path) {
        XPathFactory xPathfactory = XPathFactory.newInstance();
        XPath xpath = xPathfactory.newXPath();

        try {
            XPathExpression expr = xpath.compile(removeAttrAndTextFromXPath(path));
            NodeList nl = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
            if (nl.getLength() > 0) {
                return nl.item(0);
            }
        } catch (XPathExpressionException e) {
            log.error("Не удаётся найти путь: " + path);
        }
        return null;
    }

    /**
     * Удаление конструкций текста и аттрибутов(text() и @) из xpath
     *
     * @param path - xpath
     * @return xpath без аттрибутов и текста
     */
    private static String removeAttrAndTextFromXPath(String path) {
        String result = path;
        if (path.contains("@") || path.contains("()")) {
            int index = path.lastIndexOf("/");
            result = path.substring(0, index);
        }
        return result;
    }

    /**
     * Получить целочисленные данные из UserData
     *
     * @param node     элемент из которого нужно получить данные
     * @param attrName имя данных в UserDate
     * @return данные из UserDate
     */
    private static int getUserDataInt(Node node, String attrName) {
        int result = -1;
        if (node != null) {
            Object obj = node.getUserData(attrName);
            if (obj != null) {
                result = Integer.parseInt(obj.toString());
            }
        }
        return result;
    }
}