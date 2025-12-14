package at.pcgamingfreaks.mkvaudiosubtitlechanger.impl.validation;

import at.pcgamingfreaks.mkvaudiosubtitlechanger.impl.CommandRunner;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.InputConfig;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.util.ValidationUtil;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import picocli.CommandLine;

import java.util.Set;

public class ValidationExecutionStrategy implements CommandLine.IExecutionStrategy {

    public int execute(CommandLine.ParseResult parseResult) {
        if (!parseResult.isVersionHelpRequested() && !parseResult.isUsageHelpRequested()) validate(parseResult.commandSpec());
        return new CommandLine.RunLast().execute(parseResult);
    }

    private static void validate(CommandLine.Model.CommandSpec spec) {
        Validator validator = ValidationUtil.getValidator();
        Set<ConstraintViolation<InputConfig>> violations = validator.validate(((CommandRunner)spec.userObject()).getConfig());

        if (!violations.isEmpty()) {
            StringBuilder errors = new StringBuilder();
            for (ConstraintViolation<InputConfig> violation : violations) {
                errors.append(violation.getPropertyPath()).append(" ").append(violation.getMessage()).append("\n");
            }
            throw new CommandLine.ParameterException(spec.commandLine(), errors.toString());
        }
    }
}
