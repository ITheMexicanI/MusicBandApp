package parser.excetions;

/**
 * ВОЗНИКАЕТ ПРИ НЕПРАВИЛЬНОМ ПОЛЕ В CSV ФАЙЛЕ
 */
public class InvalidFieldValue extends RuntimeException {
    public InvalidFieldValue(String message) {
        super(message);
    }
}
