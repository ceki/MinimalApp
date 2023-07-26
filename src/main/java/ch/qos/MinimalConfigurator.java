package ch.qos;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.filter.ThresholdFilter;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;
import ch.qos.logback.core.spi.Configurator;
import ch.qos.logback.classic.spi.ConfiguratorRank;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.encoder.LayoutWrappingEncoder;
import ch.qos.logback.core.spi.ContextAwareBase;
import ch.qos.logback.core.status.OnConsoleStatusListener;
import ch.qos.logback.core.util.FileSize;
import org.slf4j.Logger;

@ConfiguratorRank(value = ConfiguratorRank.Value.FIRST)
public class MinimalConfigurator extends ContextAwareBase implements Configurator {
    @Override
    public ExecutionStatus configure(Context context) {
        System.out.println("in "+this.getClass()+".configure");

        OnConsoleStatusListener ocst = new OnConsoleStatusListener();
        ocst.setContext(context);
        ocst.start();
        context.getStatusManager().add(ocst);

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


        Appender<ILoggingEvent> rollingFileAppender = configRollingFileAppender();

        LoggerContext loggerContext = (LoggerContext) context;
        ch.qos.logback.classic.Logger rootLogger = loggerContext.getLogger(Logger.ROOT_LOGGER_NAME);
        rootLogger.addAppender(ca);
        rootLogger.addAppender(rollingFileAppender);

        String[] loggerNames = {"com.sun.mail.handlers.TextXmlTest",
                "org.apache.maven.model.management.DefaultDependencyManagementInjector",
                "org.apache.maven.repository.metadata.MetadataResolutionResult" ,
                "org.apache.felix.cm.impl.RequiredConfigurationPluginTracker" ,
                "org.apache.felix.webconsole.plugins.event.internal.OptionalFeaturesHandler",
                "ch.qos.logback.access.jetty.JettyServerAdapter"};

        for(String loggerName: loggerNames) {
            ((ch.qos.logback.classic.Logger) loggerContext.getLogger(loggerName)).setLevel(Level.INFO);
        }
        // this is it
        return ExecutionStatus.DO_NOT_INVOKE_NEXT_IF_ANY;
    }

    private Appender<ILoggingEvent> configRollingFileAppender() {
        RollingFileAppender<ILoggingEvent> rfa = new RollingFileAppender<>();
        rfa.setContext(context);
        rfa.setFile("target/infoLog.log");

        TimeBasedRollingPolicy<ILoggingEvent> tbrp = new TimeBasedRollingPolicy<>();
        tbrp.setContext(context);
        tbrp.setFileNamePattern("target/infoLogs.%d.log");
        tbrp.setMaxHistory(30);
        tbrp.setTotalSizeCap(FileSize.valueOf("3GB"));
        tbrp.setParent(rfa);
        tbrp.start();

        ThresholdFilter filter = new ThresholdFilter();
        filter.setContext(context);
        filter.setLevel("ERROR");
        filter.start();

        rfa.setRollingPolicy(tbrp);

        PatternLayoutEncoder layoutWrappingEncoder = new PatternLayoutEncoder();
        layoutWrappingEncoder.setParent(rfa);
        layoutWrappingEncoder.start();
        rfa.setEncoder(layoutWrappingEncoder);
        rfa.start();

        return rfa;
    }
}
