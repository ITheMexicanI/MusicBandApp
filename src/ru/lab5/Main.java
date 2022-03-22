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

// execute C:\Users\TheMexican\IdeaProjects\Lab\src\ru.lab5.commands.txt
// save C:\Users\TheMexican\IdeaProjects\Lab\src\comma.txt