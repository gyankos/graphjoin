package it.giacomobergami.usergenerator;

import it.giacomobergami.usergenerator.classes.User;
import it.giacomobergami.usergenerator.generators.UserGenerator;
import it.giacomobergami.usergenerator.utils.CSVUtil;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

public class UserCSV {

    public static void main(String args[]) {
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream("user_data_10_8.csv"), "utf-8"))) {
            UserGenerator ug = new UserGenerator();
            Set<User> users = new HashSet<>();
            String header = null;
            String row = null;
            for (long i = 1; i<=100000000; i++) {
                User u_i;
                do {
                    u_i = ug.next();
                } while (users.contains(u_i));
                u_i.id = i;
                if (i % 1000 == 0) {
                    System.out.println(i);
                }
                if (header == null) {
                    header = CSVUtil.getHeaders(u_i,',').concat("\n");
                    writer.write(header);
                }
                row = CSVUtil.addObjectRow(u_i, ',');
                if (i < 100000000L) {
                    row = row.concat("\n");
                }
                writer.write(row);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
