package models;

import APIsqlite.Connect;
import devlAPI.APIdevl;
import devlAPI.APIerror;
import devlRecord.RecordResProc;
import devlRecord.RecordResProcExt;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static devlAPI.APIprintService.println;

public class PositionsDAO {

    public static void printAllPosition(){
        APIerror.resetErr();
        var dataLs = getListPosition();

        if (!dataLs.res()){
            APIerror.setError(dataLs.mes());
            return;
        }

        var lsData = (List<Positions>) dataLs.data();
        for (var item : lsData){
            println(item.toString());
        }
    }

    public static  RecordResProc getListPosition(){
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

            return new RecordResProc(resLs);
        }
        catch (SQLException ex){
            return new RecordResProc(ex.getMessage());
        }
    }

    public static RecordResProcExt getSalary(Connection conn, int id) {

        var sql = """
                SELECT  CASE
                    when (select EXISTS(select * from Positions p WHERE id = %id)) > 0
                            THEN (select cast(salary as integer) from Positions p2 WHERE id = %id)
                    ELSE -1
                  END salary
                """;

        sql = APIdevl.replaceStrByInt(sql, "%id", id);
        var resSQL = (conn == null)
                ? DAOcomnAPI.getDataFromSQLscript(sql)
                : DAOcomnAPI.getDataFromSQLscript(conn, sql);

        try{
            var salary = Integer.parseInt((resSQL.strData()));
            return new RecordResProcExt(salary);
        } catch (NumberFormatException ex) {
            return RecordResProcExt.getExtResultErr(ex.getMessage());
        }
    }

    public static RecordResProcExt getJobTitle(Connection conn, int id){

        var sql = """
                SELECT  CASE
                    	when (select EXISTS(select * from Positions p WHERE id = %id)) > 0
                    			THEN (select jobTitle from Positions p2 WHERE id = %id)
                    	ELSE 'empty'
                    END salary
                """;

        sql = APIdevl.replaceStrByInt(sql, "%id", id);

        return  (conn == null)
                ? DAOcomnAPI.getDataFromSQLscript(sql)
                : DAOcomnAPI.getDataFromSQLscript(conn, sql);

    }

    public static boolean isExistsPositionInDepartment(int departmentId, int positionId){
        APIerror.resetErr();

        var sql = String.format("""
                DROP table if exists buf;
                CREATE temp table buf(
                	positionId integer
                );
                INSERT into buf(positionId)
                select positionId from Emploees e \
                			WHERE idUse > 0 \
                				and departmentsId = %d \
                				and positionId in (select id from Positions p WHERE onlyOne > 0 );
                """, departmentId);
        var sqlQuery = String.format("select exists(SELECT * from buf WHERE positionId = %d) res",
                positionId);

        try(Connection conn = APIsqlite.Connect.getConnect()){
            var stateUpdate = conn.createStatement();
            var stateQuery = conn.createStatement();

            stateUpdate.executeUpdate(sql);
            var rs = stateQuery.executeQuery(sqlQuery);
            boolean resQuery = false;
            while (rs.next()){
                var buf = rs.getString(1);
                resQuery = APIdevl.getBooleanFromStr(buf);
            }

            return resQuery;

        } catch (SQLException ex){
            APIerror.setError(ex.getMessage());
            return false;
        }

    }

}
