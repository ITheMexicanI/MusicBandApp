package ru.lab5;

import ru.lab5.commands.CommandExecutor;
import ru.lab5.commands.CommandReader;
import ru.lab5.commands.inputSystem.CommandInput;
import ru.lab5.commands.inputSystem.ConsoleCommandInput;
import ru.lab5.mainObjects.MusicBandCollection;
import ru.lab5.parser.CSVReader;

public class Main {
    public static void main(String[] args) {
        MusicBandCollection collection = new MusicBandCollection();

        // read file and serialize objects
        CSVReader fileReader = new CSVReader(collection);
        fileReader.readCSVFile(args[0]);

        CommandInput commandInput = new ConsoleCommandInput();
        CommandReader commandReader = new CommandReader(new CommandExecutor(collection, commandInput));
        commandReader.read(commandInput);
    }
}

// ON PC:
// csv-file: C:\Users\TheMexican\IdeaProjects\Lab\src\csv-example.csv
// execute C:\Users\TheMexican\IdeaProjects\Lab\src\commands.txt
// save C:\Users\TheMexican\IdeaProjects\Lab\src\new-csv.txt

// ON LAPTOP:
// csv-file: C:\Users\Dmitry\Documents\Programming\Programming\src\csv-example.csv
// execute C:\Users\Dmitry\Documents\Programming\Programming\src\commands.txt
// save C:\Users\Dmitry\Documents\Programming\Programming\src\new-csv.txt