package it.giacomobergami.usergenerator.generators;

import it.giacomobergami.usergenerator.utils.RandomWithJump;
import it.giacomobergami.usergenerator.utils.Resources;

import java.util.List;
import java.util.Random;

public class FileGenerator implements Generator<String> {

    private final List<String> lines;
    private final RandomWithJump gen;
    private final int len;

    public FileGenerator(String resourcePath, int seed) {
        lines = Resources.readTextLines(resourcePath);
        len   = lines.size();
        gen   = new RandomWithJump(seed);
    }

    @Override
    public String next() {
        return lines.get(gen.nextInt(len));
    }

    @Override
    public String next(long ustep) {
        return lines.get(gen.nextInt(ustep, len));
    }
}
