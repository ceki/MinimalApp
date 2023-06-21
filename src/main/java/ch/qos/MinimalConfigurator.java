package ch.qos;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.spi.Configurator;
import ch.qos.logback.classic.spi.ConfiguratorRank;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.encoder.LayoutWrappingEncoder;
import ch.qos.logback.core.spi.ContextAwareBase;
import org.slf4j.Logger;

@ConfiguratorRank(value = ConfiguratorRank.Value.FIRST)
public class MinimalConfigurator extends ContextAwareBase implements Configurator {
    @Override
    public ExecutionStatus configure(Context context) {
        addInfo("Setting up default configuration.");

        ConsoleAppender<ILoggingEvent> ca = new ConsoleAppender<ILoggingEvent>();
        ca.setContext(context);
        ca.setName("console");
        LayoutWrappingEncoder<ILoggingEvent> encoder = new LayoutWrappingEncoder<ILoggingEvent>();
        encoder.setContext(context);

        // same as
        PatternLayout layout = new PatternLayout();
        layout.setPattern("MINIMAL [%thread] %-5level %logger{36} - %msg%n");

        layout.setContext(context);
        layout.start();
        encoder.setLayout(layout);

        ca.setEncoder(encoder);
        ca.start();

        LoggerContext loggerContext = (LoggerContext) context;
        ch.qos.logback.classic.Logger rootLogger = loggerContext.getLogger(Logger.ROOT_LOGGER_NAME);
        rootLogger.addAppender(ca);

        // this is it
        return ExecutionStatus.DO_NOT_INVOKE_NEXT_IF_ANY;
    }
}
