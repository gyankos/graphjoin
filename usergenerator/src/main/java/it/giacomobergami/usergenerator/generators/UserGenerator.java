package it.giacomobergami.usergenerator.generators;

import com.google.inject.Injector;
import it.giacomobergami.usergenerator.classes.User;
import it.giacomobergami.usergenerator.utils.Configurations;
import javafx.util.Pair;

public class UserGenerator implements Generator<User> {
    RandomCompany rc;
    RandomDateOfBirth rdob;
    RandomEMail re;
    RandomResidence rr;
    RandomName rs;
    RandomSurname rn;

    public UserGenerator() {
        Injector inj = Configurations.get();
        rc = inj.getInstance(RandomCompany.class);
        rdob = inj.getInstance(RandomDateOfBirth.class);
        re = inj.getInstance(RandomEMail.class);
        rr = inj.getInstance(RandomResidence.class);
        rs = inj.getInstance(RandomName.class);
        rn = inj.getInstance(RandomSurname.class);
    }

    @Override
    public User next() {
        User user = new User();
        Pair<User.Sex, String> cp = rs.next();
        user.name = cp.getValue().replace(",", "").replaceAll("[^A-Za-z0-9]","");
        user.sex = cp.getKey();
        user.surname = rn.next().replace(",", "").replaceAll("[^A-Za-z0-9]","");
        user.company = rc.next().replace(",", "").replaceAll("[^A-Za-z0-9]","");
        user.dob = rdob.next().getYear();
        user.email = user.name+"."+user.surname+"@"+re.next().replace(",", "").replaceAll("[^A-Za-z0-9]","");
        user.residence = rr.next().replace(",", "").replaceAll("[^A-Za-z0-9]","");
        return user;
    }

    @Override
    public User next(long ustep) {
        User user = new User();
        Pair<User.Sex, String> cp = rs.next(ustep);
        user.name = cp.getValue().replace(",", "").replaceAll("[^A-Za-z0-9]","");
        user.sex = cp.getKey();
        user.surname = rn.next(ustep).replace(",", "").replaceAll("[^A-Za-z0-9]","");
        user.company = rc.next(ustep).replace(",", "").replaceAll("[^A-Za-z0-9]","");
        user.dob = rdob.next(ustep).getYear();
        user.email = user.name+"."+user.surname+"@"+re.next(ustep).replace(",", "").replaceAll("[^A-Za-z0-9]","");
        user.residence = rr.next(ustep).replace(",", "").replaceAll("[^A-Za-z0-9]","");
        return user;
    }


    public static void main(String args[]) {

        UserGenerator ug = new UserGenerator();
        System.out.println(ug.next(20).email);;
        System.out.println(ug.next(20).email);;
        System.out.println(ug.next(20).email);
    }
}
