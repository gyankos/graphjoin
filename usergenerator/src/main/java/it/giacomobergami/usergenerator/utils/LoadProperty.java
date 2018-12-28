package it.giacomobergami.usergenerator.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class LoadProperty {

    public static Properties fromFile(File f) {
        Properties prop = new Properties();
        InputStream input = null;
        try {
            input = new FileInputStream(f);
            prop.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return prop;
    }

    public static Properties fromFile(String path) {
        return fromFile(new File(path));
    }

}
