package CGEA;

import CGEA.withReflection.dbms.Database;
import CGEA.withReflection.dbms.rdbms.PostgreSQL;
import it.giacomobergami.utils.BenchmarkClass;
import javafx.util.Pair;
import org.postgresql.copy.CopyManager;
import org.postgresql.core.BaseConnection;

import javax.sound.sampled.*;
import java.io.*;
import java.sql.*;
import java.util.Optional;
import java.util.Properties;

public class RunPostgresqlTests extends BenchmarkClass {

    public static void main(String args[]) throws IOException, SQLException, LineUnavailableException, UnsupportedAudioFileException, InterruptedException {
        Properties properties = new Properties();
        properties.load(new FileReader(args[0]));

        Properties connectionProps = new Properties();
        connectionProps.put("user", properties.getProperty("dbuname"));
        connectionProps.put("password", properties.getProperty("dbpassw"));
        Connection conn = DriverManager.getConnection(
                "jdbc:postgresql://localhost/"+properties.getProperty("dbname"),
                connectionProps);

        Optional<Database> connection = Database.openOrCreate(new PostgreSQL(), properties.getProperty("dbname"), properties.getProperty("dbuname"), properties.getProperty("dbpassw"));
        if (!connection.isPresent())
            return;
        PrintWriter pw = new PrintWriter(new FileOutputStream(new File(properties.getProperty("REDIRECT_SAMPLING_CSV"))), true);

        String init = "CREATE DOMAIN gender CHAR(1) CHECK (value IN ( 'F' , 'M' ) );";
        Database db = connection.get();
        db.rawSqlStatement(init);

        String[] operandSeed = properties.getProperty("NEXT_PARAMETERS").split(",");
        long operandLeft = Long.valueOf(operandSeed[0].trim());
        long operandRight = Long.valueOf(operandSeed[1].trim());
        String[] operandSize = properties.getProperty("SAMPLES_PARAMETERS").split(",");
        for (int i = 7, operandSizeLength = operandSize.length; i < operandSizeLength; i++) {
            long currentSize = Long.valueOf(operandSize[i].trim());
            System.out.println("Current experiment no = " + currentSize);

            CopyManager copyManager = new CopyManager((BaseConnection) db.getConnection());
            long nanoLoading = 0;
            // Left operand Creation
            {
                System.out.println("Left");
                String leftOpTableE = createEdgeTable(true); // run
                String leftOpTableV = createVertexTable(true); // run
                String leftFileE = generateEdgeFileName(properties, currentSize, operandLeft);
                String loadLeftE = loadOperandEdge(true); // run
                String leftFileV = generateVertexFileName(properties, currentSize, operandLeft);
                String loadLeftV = loadOperandVertex(true); // run

                long timeLeft = System.nanoTime();
                db.rawSqlStatement(leftOpTableE);
                db.rawSqlStatement(leftOpTableV);
                //db.rawSqlStatement(loadLeftE);
                FileReader fileReader = new FileReader(leftFileE);
                copyManager.copyIn(loadLeftE, fileReader);
                //db.rawSqlStatement(loadLeftV);
                fileReader = new FileReader(leftFileV);
                copyManager.copyIn(loadLeftV, fileReader);
                nanoLoading += (System.nanoTime()-timeLeft);
                pw.flush();
            }

            // Right operand creation
            {
                System.out.println("Right");
                String rightOpTableE = createEdgeTable(false); // run
                String rightOpTableV = createVertexTable(false); // run
                String rightFileE = generateEdgeFileName(properties, currentSize, operandRight);
                String loadRightE = loadOperandEdge(false); // run
                String rightFileV = generateVertexFileName(properties, currentSize, operandRight);
                String loadRightV = loadOperandVertex(false); // run

                long timeLeft = System.nanoTime();
                db.rawSqlStatement(rightOpTableE);
                db.rawSqlStatement(rightOpTableV);
                //db.rawSqlStatement(loadRightE);
                FileReader fileReader = new FileReader(rightFileE);
                copyManager.copyIn(loadRightE, fileReader);
                //db.rawSqlStatement(loadRightV);
                fileReader = new FileReader(rightFileV);
                copyManager.copyIn(loadRightV, fileReader);
                nanoLoading += ((System.nanoTime()-timeLeft));
                pw.println("right,"+currentSize+",loading,"+printNanoAsSecondExp(nanoLoading));
                pw.flush();
            }

            String vertex = "explain analyze select lv.id as l, rv.id as r from lv, rv where lv.dob = rv.dob and lv.company = rv.company;";
            String edge1 = "create view vertices as select lv.id as l, rv.id as r from lv, rv where lv.dob = rv.dob and lv.company = rv.company;";
            String edge2 = "explain analyze select src.l, src.r, dst.l, dst.r from vertices src, vertices dst, le, re where src.l = le.src and dst.l = le.dst and src.r = re.src and dst.r = re.dst;";

            long timeLeft = System.nanoTime();
            System.out.println("Vertex");
            Pair<ResultSet, Statement> cp = db.rawSqlQueryPair(vertex);
            cp.getKey().close();
            cp.getValue().close();
            System.out.println("View");
            db.rawSqlStatement(edge1);
            System.out.println("Edges");
            cp = db.rawSqlQueryPair(edge2);
            cp.getKey().close();
            cp.getValue().close();
            pw.println("leftright,"+currentSize+",joining,"+printNanoAsSecondExp(System.nanoTime()-timeLeft));

            String drop1 = "DROP TABLE lv;";
            String drop2 = "DROP TABLE le;";
            String drop3 = "DROP TABLE rv;";
            String drop4 = "DROP TABLE re;";
            String drop5 = "DROP VIEW vertices;";

            System.out.println("Drops");
            db.rawSqlStatement(drop5);
            db.rawSqlStatement(drop1);
            db.rawSqlStatement(drop2);
            db.rawSqlStatement(drop3);
            db.rawSqlStatement(drop4);
            pw.flush();
        }
        pw.close();
    }

}
