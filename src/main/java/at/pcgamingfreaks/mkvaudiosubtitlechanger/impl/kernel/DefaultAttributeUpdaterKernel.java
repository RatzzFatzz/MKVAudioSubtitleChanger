package at.pcgamingfreaks.mkvaudiosubtitlechanger.impl.kernel;

import at.pcgamingfreaks.mkvaudiosubtitlechanger.model.InputConfig;
import at.pcgamingfreaks.mkvaudiosubtitlechanger.impl.processors.FileProcessor;
import lombok.extern.slf4j.Slf4j;
import me.tongfei.progressbar.ProgressBarBuilder;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class DefaultAttributeUpdaterKernel extends AttributeUpdaterKernel {

    public DefaultAttributeUpdaterKernel(FileProcessor processor) {
        super(processor);
    }

    @Override
    protected ProgressBarBuilder pbBuilder() {
        return super.pbBuilder()
                .setUnit(" files", 1);
    }
}
