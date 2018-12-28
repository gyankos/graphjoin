package it.giacomobergami.usergenerator.generators;

import com.google.inject.Inject;
import com.google.inject.name.Named;

public class RandomEMail extends FileGenerator {
    @Inject
    public RandomEMail(@Named("email.seed") int seed) {
        super("email.txt", seed);
    }
}
