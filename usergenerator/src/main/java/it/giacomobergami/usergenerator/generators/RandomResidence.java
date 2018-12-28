package it.giacomobergami.usergenerator.generators;

import com.google.inject.Inject;
import com.google.inject.name.Named;

public class RandomResidence extends FileGenerator {
    @Inject
    public RandomResidence(@Named("residence.seed") int seed) {
        super("residence.txt", seed);
    }
}
