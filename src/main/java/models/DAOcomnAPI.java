package models;

import devlRecord.RecordResProc;
import devlRecord.RecordResProcExt;

import java.sql.Connection;
import java.sql.SQLException;
import static devlAPI.APIprintService.*;

public class DAOcomnAPI {

    public static RecordResProc modfModelFromSQLscript(String strModf){
        RecordResProc res = null;

        try(Connection conn = APIsqlite.Connect.getConnect()){
            var statement = conn.createStatement();
            var rs = statement.executeUpdate(strModf);

            res = new RecordResProc("ok");
        }
        catch (SQLException ex){
            res = RecordResProc.getResultErr(ex.getMessage());
        }

        return res;
    }


    /**
     * Выборка данных одна запись, один столбец return StringObj
     * @param sql
     * @return
     */
    public static RecordResProcExt getDataFromSQLscript(Connection conn, String sql){
        RecordResProcExt res = null;

        try{
            var statement = conn.createStatement();
            var rs = statement.executeQuery(sql);

            String resStr = "";
            while (rs.next()){
                resStr = rs.getString(1);
            }

            res = new RecordResProcExt(resStr);
        }
        catch (SQLException ex){
            println("err: \n" + ex.getMessage());
            res = RecordResProcExt.getExtResultErr (ex.getMessage());
        }

        return  res;
    }

    public static RecordResProcExt getDataFromSQLscript(String sql){
        RecordResProcExt res = null;

        try(Connection conn = APIsqlite.Connect.getConnect()){
            var statement = conn.createStatement();
            var rs = statement.executeQuery(sql);

            String resStr = "";
            while (rs.next()){
                resStr = rs.getString(1);
            }

            res = new RecordResProcExt(resStr);
        }
        catch (SQLException ex){
            println("err: \n" + ex.getMessage());
            res = RecordResProcExt.getExtResultErr (ex.getMessage());
        }

        return  res;
    }
}
