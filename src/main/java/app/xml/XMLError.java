package app.xml;

import org.w3c.dom.Node;
import org.xmlunit.diff.*;

public class XMLError{
    /**
     * XPath до нода в xml-файле
     */
    private String xpath;

    /**
     * Номер строки в xml
     */
    private int lineNumber;

    /**
     * Нод в файле
     */
    private Node node;

    /**
     *
     * Тип ошибки
     */
    private String type;

    public String getXpath() {
        return xpath;
    }

    public void setXpath(String xpath) {
        this.xpath = xpath;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public Node getNode() {
        return node;
    }

    public void setNode(Node node) {
        this.node = node;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
