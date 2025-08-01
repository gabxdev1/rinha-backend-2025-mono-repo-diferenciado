package br.com.gabxdev.properties;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ApplicationProperties {
    private static final ApplicationProperties INSTANCE = new ApplicationProperties();

    private Properties properties;

    private ApplicationProperties() {
        loadProperties();
    }

    private void loadProperties() {
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("application.properties")) {
            if (in == null) {
                throw new RuntimeException("application.properties not found in classpath");
            }

            String raw = new String(in.readAllBytes(), StandardCharsets.UTF_8);

            String resolved = resolvePlaceholders(raw);

            properties = new Properties();

            properties.load(new ByteArrayInputStream(resolved.getBytes(StandardCharsets.UTF_8)));
        } catch (IOException e) {
            throw new RuntimeException("Error loading application.properties", e);
        }
    }

    private String resolvePlaceholders(String text) {
        Pattern pattern = Pattern.compile("\\$\\{([^:}]+)(?::([^}]*))?}");
        Matcher matcher = pattern.matcher(text);
        StringBuffer sb = new StringBuffer();

        while (matcher.find()) {
            String var = matcher.group(1);
            String defaultVal = matcher.group(2);

            String value = System.getProperty(var);

            if (value == null) {
                value = System.getenv(var);
            }
            if (value == null) {
                value = defaultVal;
            }

            matcher.appendReplacement(sb, Matcher.quoteReplacement(value != null ? value : ""));
        }

        matcher.appendTail(sb);
        return sb.toString();
    }

    public static ApplicationProperties getInstance() {
        return INSTANCE;
    }

    public String getProperty(PropertiesKey key) {
        return properties.getProperty(key.getKey());
    }
}
