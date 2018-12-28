package it.giacomobergami.usergenerator.utils;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.name.Names;

public class Configurations extends AbstractModule {
    @Override
    protected void configure() {
        Names.bindProperties(binder(), LoadProperty.fromFile("conf.properties"));
    }

    private static Injector self;
    public static Injector get() {
        if (self == null) {
            self = Guice.createInjector(new Configurations());
        }
        return self;
    }

    public static <T> T getInstance(Class<T> clazz) {
        return get().getInstance(clazz);
    }
}
