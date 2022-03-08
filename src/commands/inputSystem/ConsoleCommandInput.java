package commands.inputSystem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * ЧИТАЕТ КОМАНДЫ ИЗ КОНСОЛИ
 */
public class ConsoleCommandInput implements CommandInput {
    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    /**
     * ЧИТАЕТ ИЗ КОНСОЛИ
     * @return ВОВРАЩАЕТ ПРОЧТЕННУЮ СТРОКУ
     */
    @Override
    public String readLine() {
        try {
            return reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
