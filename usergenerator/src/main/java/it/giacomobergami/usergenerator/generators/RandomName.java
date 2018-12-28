package it.giacomobergami.usergenerator.generators;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import it.giacomobergami.usergenerator.classes.User;
import it.giacomobergami.usergenerator.utils.Configurations;
import it.giacomobergami.usergenerator.utils.RandomWithJump;
import it.giacomobergami.usergenerator.utils.Resources;
import javafx.util.Pair;

import java.util.List;
import java.util.Random;

public class RandomName implements Generator<Pair<User.Sex, String>> {

    RandomSex rs;
    RandomWithJump r;
    List<String> females, males;
    int nf, nm;

    @Inject
    public RandomName(@Named("name.seed") int seed) {
        rs = Configurations.getInstance(RandomSex.class);
        r = new RandomWithJump(seed);
        females = Resources.readTextLines("all_names_female.txt");
        nf = females.size();
        males = Resources.readTextLines("all_names_male.txt");
        nm = males.size();
    }

    @Override
    public Pair<User.Sex, String> next() {
        if (rs.next()) {
            return new Pair<>(User.Sex.M, males.get(r.nextInt(nm)));
        } else {
            return new Pair<>(User.Sex.F, females.get(r.nextInt(nf)));
        }
    }

    @Override
    public Pair<User.Sex, String> next(long ustep) {
        if (rs.next()) {
            return new Pair<>(User.Sex.M, males.get(r.nextInt(ustep, nm)));
        } else {
            return new Pair<>(User.Sex.F, females.get(r.nextInt(ustep, nf)));
        }
    }
}
