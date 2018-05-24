package app;

import app.xml.MyPair;
import app.xml.XMLComparator;
import app.xml.XMLCompareResult;
import app.xml.XMLError;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Application {

    public static void main(String[] args) {
        FileInputStream isDev = null;
        FileInputStream isProm = null;
        if(args.length >= 2){
            try {
                isDev = new FileInputStream(new File(args[0]));
                isProm = new FileInputStream(new File(args[1]));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        else {
            try {
                isDev = new FileInputStream(new File("devTransformResult.xml"));
                isProm = new FileInputStream(new File("promTransformResult.xml"));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        XMLCompareResult result = null;
        try {
            result = XMLComparator.diff(isDev, isProm);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Ошибки:");
        for (XMLError error : result.getErrors() ) {
            System.out.println("XPath: " + error.getXpath());
            System.out.println("ParentXPath: " + error.getParentXPath());
            System.out.println("Type: " + error.getComparisonType());
            System.out.println("Message: " + error.getMessage());
            System.out.println();
        }
        System.out.println("Предупреждения:");
        for (XMLError error : result.getWarnings() ) {
            System.out.println(error.getXpath());
            System.out.println(error.getType());
            System.out.println(error.getMessage());
        }
        System.out.println("...end of diff");

    }
}
