package it.giacomobergami.usergenerator.generators;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import it.giacomobergami.usergenerator.utils.RandomWithJump;

import java.util.Random;

public class RandomSex implements Generator<Boolean> {

    private final RandomWithJump sex;
    @Inject
    public RandomSex(@Named("sex.seed")int seed) {
        sex = new RandomWithJump(seed);
    }

    @Override
    public Boolean next() {
        return sex.nextBoolean();
    }

    @Override
    public Boolean next(long ustep) {
        return sex.nextBoolean(ustep);
    }
}
