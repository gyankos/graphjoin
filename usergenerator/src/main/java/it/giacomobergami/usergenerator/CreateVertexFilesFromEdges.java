package it.giacomobergami.usergenerator;

import it.giacomobergami.usergenerator.classes.User;
import it.giacomobergami.usergenerator.generators.UserGenerator;
import it.giacomobergami.usergenerator.utils.CSVUtil;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * This class generates the vertices' informations for the associated edge files
 */
public class CreateVertexFilesFromEdges {

    public static class LRUCache<K, V> extends LinkedHashMap<K, V> {
        private int cacheSize;

        public LRUCache(int cacheSize) {
            super(16, 0.75f, true);
            this.cacheSize = cacheSize;
        }

        protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
            return size() >= cacheSize;
        }
    }

    public static void execute(String args[], File toExclude) throws FileNotFoundException {
        LRUCache<Long, User> map = new LRUCache<>(1000000);

        String header = null;
        File dir = new File(args[0]);
        UserGenerator ug = new UserGenerator();
        /*System.out.println("Generating all the possible candidates:");
            Set<User> users = new HashSet<>();
            String row = null;
            for (long i = 1; i<=134217728; i++) {
                User u_i;
                do {
                    u_i = ug.next();
                } while (users.contains(u_i));
                u_i.id = i;
                if (i % 100000 == 0) {
                    System.out.println(i);
                }
                if (header == null) {
                    header = CSVUtil.getHeaders(u_i,',').concat("\n");
                }
                row = CSVUtil.addObjectRow(u_i, ',');
                map.put(i-1L, row.replaceFirst(i+"",(i-1)+""));
            }*/
        File[] files = dir.listFiles();
        Arrays.sort(files);
        for (int i = files.length-1; i >= 0; i--) {
            File f = files[i];
            if (f.isFile() && (toExclude == null || (!toExclude.equals(f)))) {
                System.out.println(f.getName());
                Scanner s = new Scanner(new FileReader(f));
                TreeSet<Long> order = new TreeSet<>();
                {
                    HashSet<Long> set = new HashSet<>();
                    while (s.hasNextLine()) {
                        for (String num : s.nextLine().split("\t"))
                            set.add(Long.valueOf(num));
                    }
                    order.addAll(set);
                }
                try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                        new FileOutputStream(f.getAbsolutePath() + "_vertices.csv"), StandardCharsets.UTF_8))) {
                    Iterator<Long> vertices = order.iterator();
                    while (vertices.hasNext()) {
                        Long next = vertices.next();
                        User u = map.get(next);
                        if (u == null) {
                            u = ug.next(next);
                            map.put(next, u);
                        }
                        u.id = next;
                        writer.write(CSVUtil.addObjectRow(u, ','));
                        if (vertices.hasNext())
                            writer.write("\n");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public static void main(String args[]) throws FileNotFoundException {
        execute(args, null);
    }

}
