import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;

public class SSHTest {

    public static void main(String[] args) throws Exception {
        int lport = 5656;
        String rhost = "remote server";
        String sshHost = "ssh host";
        int rport = 3306;
        String sshUser = "sshuser";
        String sshPassword = "sshpassword";
        String dbUserName = "mysqlUserName";
        String dbPassowrd = "mysqlPassowrd";
//        String url = "jdbc:mysql://localhost:" + lport + "/mydb";

        String url = "jdbc:mysql://localhost:" + lport + "/mydb?" +
                "useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=Europe/Moscow";

        String driverName = "com.mysql.jdbc.Driver";
        Connection conn = null;
        Session session = null;


        String query = "select * from myTable;";

        try {

            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            JSch jSch = new JSch();
            session = jSch.getSession(sshUser, sshHost, 22);
            session.setPassword(sshPassword);
            session.setConfig(config);
            session.connect();
            System.out.println("Connected");
            int assinged_port = session.setPortForwardingL(lport, rhost, rport);
            System.out.println("localhost:" + assinged_port + " -> " + rhost + ":" + rport);
            System.out.println("Port Forwarded");

//            Class.forName(driverName).newInstance();
            conn = DriverManager.getConnection(url, dbUserName, dbPassowrd);
            System.out.println("Database connection established");
            System.out.println("DONE");
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            if (rs.next()) {
                System.out.println(rs.getInt(1));
                System.out.println(rs.getString(2));
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null && !conn.isClosed()) {
                System.out.println("Closing Database Connection");
                conn.close();
            }
            if (session != null && session.isConnected()) {
                System.out.println("Closing SSH Connection");
                session.disconnect();
            }
        }
    }
}
