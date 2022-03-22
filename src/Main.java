import commands.CommandExecutor;
import commands.CommandReader;
import commands.inputSystem.CommandInput;
import commands.inputSystem.ConsoleCommandInput;
import mainObjects.MusicBandCollection;
import parser.CSVReader;

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

// execute C:\Users\TheMexican\IdeaProjects\Lab\src\commands.txt
// save C:\Users\TheMexican\IdeaProjects\Lab\src\comma.txt