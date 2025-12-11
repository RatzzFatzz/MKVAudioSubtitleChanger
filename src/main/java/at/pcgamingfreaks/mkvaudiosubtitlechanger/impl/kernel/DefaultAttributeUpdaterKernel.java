package at.pcgamingfreaks.mkvaudiosubtitlechanger.impl.kernel;

import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.InputConfig;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.impl.processors.FileProcessor;
import lombok.extern.slf4j.Slf4j;
import me.tongfei.progressbar.ProgressBarBuilder;

@Slf4j
public class DefaultAttributeUpdaterKernel extends AttributeUpdaterKernel {

    public DefaultAttributeUpdaterKernel(InputConfig config, FileProcessor processor) {
        super(config, processor);
    }

    @Override
    protected ProgressBarBuilder pbBuilder() {
        return super.pbBuilder()
                .setUnit(" files", 1);
    }
}
