package it.giacomobergami.usergenerator.utils;

import com.google.common.collect.ImmutableList;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Resources {

    private Resources() { }
    private static Resources self;
    private static List<String> empty = ImmutableList.of();

    public static List<String> readTextLines(String fileName) {
        if (self == null) {
            self = new Resources();
        }
        ClassLoader classLoader = self.getClass().getClassLoader();
        try {
            Path p = Paths.get(classLoader.getResource(fileName).toURI());
            return Files.readAllLines(p);
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }
        return empty;
    }
}
