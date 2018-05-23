package app.xml;

import java.util.ArrayList;
import java.util.List;

public class XMLCompareResult {

    /**
     * Список ошибок.
     */
    private List<MyPair<XMLError>> errors;

    /**
     * Список предупреждений.
     * Например: XML-файлы могут быть равны, но иметь разный порядок нодов.
     */
    private List<MyPair<XMLError>> warnings;

    public XMLCompareResult() {
        errors = new ArrayList<>();
        warnings = new ArrayList<>();
    }

    public boolean isSimilar() {
        return errors.isEmpty();
    }

    public boolean isIdentical() {
        return errors.isEmpty() && warnings.isEmpty();
    }

    public List<MyPair<XMLError>> getErrors() {
        return errors;
    }

    public void setErrors(List<MyPair<XMLError>> errors) {
        this.errors = errors;
    }

    public List<MyPair<XMLError>> getWarnings() {
        return warnings;
    }

    public void setWarnings(List<MyPair<XMLError>> warnings) {
        this.warnings = warnings;
    }
}
