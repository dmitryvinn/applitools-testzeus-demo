package dev.dvinnik.applitools.testzeus.utils;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Stream;

public class TestPropertiesUtil {

    private static final String PROPERTY_PATH
            = System.getProperty("user.dir") + "/src/test/java/config.properties";

    private final Map<PropertyKey, String> properties;
    private static TestPropertiesUtil instance;

    private TestPropertiesUtil() {
        properties = new HashMap<>();
        loadAllProperties();
    }

    public static TestPropertiesUtil getInstance() {
        if(instance == null) {
            instance = new TestPropertiesUtil();
        }
        return instance;
    }

    public String getProperty(final PropertyKey propertyKey) {
        return properties.get(propertyKey);
    }

    private void loadAllProperties() {
        try {
            final InputStream input = new FileInputStream(PROPERTY_PATH);
            final Properties propertyFile = new Properties();

            propertyFile.load(input);

            Stream.of(PropertyKey.values()).forEach(propertyKey -> {
                properties.put(propertyKey, propertyFile.getProperty(propertyKey.name()));
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}