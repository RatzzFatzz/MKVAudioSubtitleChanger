package at.pcgamingfreaks.mkvaudiosubtitlechanger;

import at.pcgamingfreaks.mkvaudiosubtitlechanger.impl.CommandRunner;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.impl.validation.ValidationExecutionStrategy;
import picocli.CommandLine;

public class Main {
    public static void main(String[] args) {
        if (args.length == 0) {
            CommandLine.usage(CommandRunner.class, System.out);
            return;
        }

        new CommandLine(CommandRunner.class)
                .setExecutionStrategy(new ValidationExecutionStrategy())
                .execute(args);
    }
}
