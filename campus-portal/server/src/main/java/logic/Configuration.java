package logic;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Properties;

public final class Configuration {
    private static Properties labels;
    private static Properties logics;

    private Configuration() {
    }

    public static void load() {
        logics = loadResource("logics");
        labels = loadResource("labels");
    }

    /** A setting from a system property, then the environment, then the default. */
    public static String setting(String property, String environment, String defaultValue) {
        String value = System.getProperty(property);
        if (value == null) {
            value = System.getenv(environment);
        }
        return value == null || value.isBlank() ? defaultValue : value;
    }

    public static int number(String key) {
        return Integer.parseInt(logics.getProperty(key));
    }

    public static String text(String key) {
        return logics.getProperty(key);
    }

    /** A sentence from the labels file with @1, @2 and so on filled in. */
    public static String label(String key, Object... values) {
        String text = labels.getProperty(key);
        for (int i = 0; i < values.length; i++) {
            text = text.replace("@" + (i + 1), String.valueOf(values[i]));
        }
        return text;
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
