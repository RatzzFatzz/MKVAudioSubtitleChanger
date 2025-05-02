package at.pcgamingfreaks.mkvaudiosubtitlechanger;

import at.pcgamingfreaks.mkvaudiosubtitlechanger.config.Config;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.impl.CachedMkvFileProcessor;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.impl.kernel.AttributeUpdaterKernel;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.impl.kernel.CoherentAttributeUpdaterKernel;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.impl.kernel.DefaultAttributeUpdaterKernel;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.impl.MkvFileCollector;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.util.ProjectUtil;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import picocli.CommandLine;

import java.util.Set;

@Slf4j
@CommandLine.Command(
        name = "mkvaudiosubtitlechanger",
        usageHelpWidth = 120,
        customSynopsis = {
                "mkvaudiosubtitlechanger -a <attributeConfig>... -l <libraryPath> [-s]",
                "Example: mkvaudiosubtitlechanger -a eng:eng eng:ger -l /mnt/media/ -s",
                ""
        },
        mixinStandardHelpOptions = true,
        versionProvider = ProjectUtil.class
)
public class Main implements Runnable {

    @Getter
    @CommandLine.ArgGroup(exclusive = false)
    private Config config;

    @CommandLine.Spec
    CommandLine.Model.CommandSpec spec;

    @Override
    public void run() {
        if (config.isDebug()) {
            Configurator.setRootLevel(Level.DEBUG);
        }
        validate();
        Config.setInstance(config);
        AttributeUpdaterKernel kernel = Config.getInstance().getCoherent() != null
                ? new CoherentAttributeUpdaterKernel(new MkvFileCollector(), new CachedMkvFileProcessor())
                : new DefaultAttributeUpdaterKernel(new MkvFileCollector(), new CachedMkvFileProcessor());
        kernel.execute();
    }

    private void validate() {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<Config>> violations = validator.validate(config);

        if (!violations.isEmpty()) {
            StringBuilder errorMsg = new StringBuilder();
            for (ConstraintViolation<Config> violation : violations) {
                errorMsg.append("ERROR: ").append(violation.getPropertyPath()).append(" ").append(violation.getMessage()).append("\n");
            }
            throw new CommandLine.ParameterException(spec.commandLine(), errorMsg.toString());
        }
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            args = new String[] { "--help" };
        }
        new CommandLine(Main.class).execute(args);
    }
}
