package APIsqlite;

import java.sql.Connection;
import java.sql.SQLException;

public class ApiModels {

    public static int getMaxIdEmploees() throws SQLException {
        String sql = "select ifnull(max(id), 0) id from Emploees;";
        int res = 0;

        try(Connection conn = Connect.getConnect()) {
            var stmt = conn.createStatement();
            var rs = stmt.executeQuery(sql);

            while (rs.next())
                res = rs.getInt("id");
        }

        return res;
    }

}