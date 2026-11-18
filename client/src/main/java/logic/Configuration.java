package logic;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Properties;

public class Configuration {
    public static Properties addresses;
    public static Properties graphics;

    public static void load() {
        addresses = loadResource("addresses");
        graphics = loadResource("graphics");
    }

    /** A setting from a system property, then the environment, then the default. */
    public static String setting(String property, String environment, String defaultValue) {
        String value = System.getProperty(property);
        if (value == null) {
            value = System.getenv(environment);
        }
        return value == null || value.isBlank() ? defaultValue : value;
    }

    private static Properties loadResource(String name) {
        Properties properties = new Properties();
        try (InputStream in = Configuration.class.getResourceAsStream("/Configurations/" + name + ".xml")) {
            properties.loadFromXML(in);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return properties;
    }
}
