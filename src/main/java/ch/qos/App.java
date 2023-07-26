package ch.qos;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Hello world!
 */
public class App {

    public static void main(String[] args) {
        System.setProperty("logback_scmo_target", "config-scmo/logback.scmo");
        System.setProperty("logback.serializedModelFilew", "config-scmo/logback.scmo");
        Logger LOGGER = LoggerFactory.getLogger(App.class);
        LOGGER.info("test");
        System.out.println("Hello World!");
    }

}
