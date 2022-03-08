package parser.excetions;

/**
 * ВОЗНИКАЕТ ПРИ НЕПРАВИЛЬНО ЗАПОЛНЕНОМ ХЕАДЕРЕ В CSV ФАЙЛЕ
 */
public class InvalidHeaderException extends RuntimeException {
    @Override
    public String getMessage() {
        return "Invalid header on file";
    }
}
