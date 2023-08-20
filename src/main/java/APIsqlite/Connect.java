package APIsqlite;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Connect {

    private static final String URL = "jdbc:sqlite:coursePr.db3";
    public static Connection getConnect() throws SQLException {
        Connection conn = null;

        conn = DriverManager.getConnection(URL);

        if (conn == null)
            throw new SQLException("Connect is null");
        return conn;
    }

}