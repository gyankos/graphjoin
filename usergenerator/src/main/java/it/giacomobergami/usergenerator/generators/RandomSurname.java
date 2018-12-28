package it.giacomobergami.usergenerator.generators;

import com.google.inject.Inject;
import com.google.inject.name.Named;

public class RandomSurname extends FileGenerator {
    @Inject
    public RandomSurname(@Named("surname.seed") int seed) {
        super("all_surnames.txt", seed);
    }
}
