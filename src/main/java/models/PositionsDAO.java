package models;

import APIsqlite.Connect;
import devlRecord.RecordResProc;
import devlRecord.RecordResProcExt;
import devlAPI.APIdevl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.sql.Connection;

import static devlAPI.APIprintService.*;

// TODO: включить наследование extends DAOabstract<Integer, Department>
public class PositionsDAO {

    public static void printAllPosition(){
        var dataLs = getListPosition();

        if (!dataLs.res()){
            return;
        }

        for (var item : (List<Positions>) dataLs.data()){
            println(item.toString());
        }
    }

    public static  RecordResProc getListPosition(){

        RecordResProc res = null;
        List<Positions> resLs = new ArrayList<>();
        var sql = "select id, jobTitle, salary, numLimit  from Positions p;";

        try(Connection conn = Connect.getConnect()){
            var stateMnt = conn.createStatement();
            var rs = stateMnt.executeQuery(sql);

            while (rs.next()){
                resLs.add(
                        new Positions(rs.getInt(1),
                                rs.getString(2),
                                rs.getInt(3),
                                rs.getInt(4)
                        )
                );
            }

            res = new RecordResProc(resLs);
        }
        catch (SQLException ex){
            res = new RecordResProc(ex.getMessage());
        }
        catch (Exception ex){
            res = new RecordResProc(ex.getMessage());
        }

        return res;
    }
    public static RecordResProcExt getNumLimit(int id) {

        RecordResProcExt res = null;

        var sql = """
                SELECT  CASE
                when (select EXISTS(select * from Positions p WHERE id = $id)) > 0
                        THEN (select numLimit  from Positions p2 WHERE id = $id)
                ELSE -1
                END numLimit
                """;

        sql = APIdevl.replaceStrByInt(sql, "%id", id);

        var resSql = DAOcomnAPI.getDataFromSQLscript(sql);
        if (resSql.res()){
            res = new RecordResProcExt(Integer.parseInt(resSql.strData()) );
        } else {
            res = resSql;
        }

        return res;
    }
    public static RecordResProcExt getSalary(Connection conn, int id) {
        RecordResProcExt res = null;

        var sql = """
                SELECT  CASE
                    when (select EXISTS(select * from Positions p WHERE id = %id)) > 0
                            THEN (select salary  from Positions p2 WHERE id = %id)
                    ELSE -1
                  END salary
                """;

        sql = APIdevl.replaceStrByInt(sql, "%id", id);
        var resSQL = (conn == null)
                ? DAOcomnAPI.getDataFromSQLscript(sql)
                : DAOcomnAPI.getDataFromSQLscript(conn, sql);

        res = new RecordResProcExt(true,
                "ok","ok", Integer.parseInt((resSQL.strData())));

        return res;
    }

    public static RecordResProcExt getJobTitle(Connection conn, int id){

        RecordResProcExt res = null;

        var sql = """
                SELECT  CASE
                    	when (select EXISTS(select * from Positions p WHERE id = %id)) > 0
                    			THEN (select jobTitle from Positions p2 WHERE id = %id)
                    	ELSE 'empty'
                    END salary
                """;

        sql = APIdevl.replaceStrByInt(sql, "%id", id);

        var resSQL = (conn == null)
                ? DAOcomnAPI.getDataFromSQLscript(sql)
                : DAOcomnAPI.getDataFromSQLscript(conn, sql);

        return new RecordResProcExt(true, "ok", resSQL.strData(), 0);

    }

}
