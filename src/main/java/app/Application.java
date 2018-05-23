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
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        System.out.println("Ошибки:");
        for (MyPair<XMLError> error : result.getErrors() ) {
            if(error.getFirst() != null){
                System.out.println(error.getFirst().getXpath());
            }
            if(error.getSecond() != null){
                System.out.println(error.getSecond().getXpath());
            }
        }
        System.out.println("Предупреждения:");
        for (MyPair<XMLError> error : result.getWarnings() ) {
            if(error.getFirst() != null){
                System.out.println(error.getFirst().getXpath());
            }
            if(error.getSecond() != null){
                System.out.println(error.getSecond().getXpath());
            }
        }
        System.out.println("...end of diff");

    }
}
