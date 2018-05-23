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

    public XMLError() {
    }

    public XMLError(ErrorType type) {
        this.type = type;
    }

}
