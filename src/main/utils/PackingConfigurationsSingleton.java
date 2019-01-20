package main.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class PackingConfigurationsSingleton {
    private static final PackingConfigurationsSingleton config = new PackingConfigurationsSingleton();
    private static Properties prop;
    private PackingConfigurationsSingleton(){
        try {
            FileInputStream is = new FileInputStream("src/main.Constraints.resources/packing.properties");
            prop = new Properties();
            prop.load(is);
            is.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getProperty(String key) {
        return prop.getProperty(key);
    }
}
