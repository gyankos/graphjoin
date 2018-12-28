
package it.giacomobergami.usergenerator.classes;

import java.util.Date;
import java.util.Objects;

public class User {
    public enum Sex {
        M, F
    }

    public long id;
    public Sex sex;
    public String name;
    public String surname;
    public long dob;
    public String email;
    public String company;
    public String residence;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return sex == user.sex &&
                Objects.equals(name, user.name) &&
                Objects.equals(surname, user.surname) &&
                Objects.equals(dob, user.dob) &&
                Objects.equals(email, user.email) &&
                Objects.equals(company, user.company) &&
                Objects.equals(residence, user.residence);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sex, name, surname, dob, email, company, residence);
    }
}
