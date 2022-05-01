package ru.lab.server.parser;

import ru.lab.server.parser.excetions.InvalidFieldValue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * БАЗОВЫЙ CSV ОБЪЕКТ ПРОСТО ХРАНИТ ПРОЧТЕННЫЙ CSV ФАЙЛ
 */
public class CSVObject {
    private final Map<String, String> fields = new HashMap<>();

    /**
     * @param headers   ЗАГАЛОВКИ CSV ФАЙЛА
     * @param arguments ПОЛЯ CSV ФАЙЛА
     */
    protected CSVObject(List<String> headers, List<String> arguments) {
        if (arguments.size() < headers.size()) {
            throw new InvalidFieldValue("Not enough fields in the file");
        }

        for (int i = 0; i < headers.size(); i++) {
            fields.put(headers.get(i), arguments.get(i));
        }
    }

    /**
     * @return ВОЗВРАЩАЕТ ПОЛЯ
     */
    protected Map<String, String> getFields() {
        return fields;
    }

}
