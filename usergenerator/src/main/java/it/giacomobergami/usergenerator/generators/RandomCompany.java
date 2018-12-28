package it.giacomobergami.usergenerator.generators;

import com.google.inject.Inject;
import com.google.inject.name.Named;

public class RandomCompany extends FileGenerator {

    @Inject
    public RandomCompany(@Named("company.seed") int seed) {
        super("business-names.txt", seed);
    }
}
