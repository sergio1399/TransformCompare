package app.xml;

import org.w3c.dom.Node;
import org.xmlunit.diff.*;

public class XMLError{
    /**
     * XPath до нода в xml-файле
     */
    private String xpath;

    /**
     * XPath до нода в xml-файле не включая сам нод
     */
    private String parentXPath;

    /**
     * Номер строки в xml
     */
    private int lineNumber;

    /**
     * Номер колонки в которой была ошибка
     */
    private int columnNumber;

    /**
     * Нод в файле
     */
    private Node node;

    /**
     *
     * Сообщение ошибки
     */
    private String message;

    /**
     *
     * Тип ошибки
     */
    private ErrorType type;

    /**
     *
     * XML источник
     */
    private String source;

    /**
     *
     * Тип сравнения
     */
    private ComparisonType comparisonType;

    public String getParentXPath() {
        return parentXPath;
    }

    public void setParentXPath(String parentXPath) {
        this.parentXPath = parentXPath;
    }

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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getColumnNumber() {
        return columnNumber;
    }

    public void setColumnNumber(int columnNumber) {
        this.columnNumber = columnNumber;
    }

    public ErrorType getType() {
        return type;
    }

    public void setType(ErrorType type) {
        this.type = type;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public XMLError() {
    }

    public XMLError(ErrorType type) {
        this.type = type;
    }

    public ComparisonType getComparisonType() {
        return comparisonType;
    }

    public void setComparisonType(ComparisonType comparisonType) {
        this.comparisonType = comparisonType;
    }
}
