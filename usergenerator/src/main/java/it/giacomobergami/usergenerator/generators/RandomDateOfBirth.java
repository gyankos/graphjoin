package it.giacomobergami.usergenerator.generators;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import it.giacomobergami.usergenerator.utils.Configurations;
import it.giacomobergami.usergenerator.utils.RandomWithJump;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Random;

public class RandomDateOfBirth implements Generator<Date> {

    private int minYear;
    private int maxYear;
    private RandomWithJump genYear, genDay;

    @Inject
    public RandomDateOfBirth(@Named("born.year.min") int minYear,
                             @Named("born.year.max") int maxYear,
                             @Named("born.year.seed") int seedYear,
                             @Named("born.day.seed") int seedDay) {
        this.maxYear = maxYear;
        this.minYear = minYear;
        this.genDay = new RandomWithJump(seedDay);
        this.genYear = new RandomWithJump(seedYear);
    }

    @Override
    public Date next() {
        GregorianCalendar gc = new GregorianCalendar();
        int year = minYear + genYear.nextInt(maxYear-minYear);
        int day = 1 + genDay.nextInt(gc.getActualMaximum(gc.DAY_OF_YEAR)-1);
        gc.set(gc.DAY_OF_YEAR, day);
        gc.set(gc.YEAR, year);
        return (gc.getTime());
    }

    @Override
    public Date next(long ustep) {
        GregorianCalendar gc = new GregorianCalendar();
        int year = minYear + genYear.nextInt(ustep,maxYear-minYear);
        int day = 1 + genDay.nextInt(ustep,gc.getActualMaximum(gc.DAY_OF_YEAR)-1);
        gc.set(gc.DAY_OF_YEAR, day);
        gc.set(gc.YEAR, year);
        return (gc.getTime());
    }
}

