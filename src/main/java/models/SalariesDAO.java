package models;

import DevlInterface.IRunComd;
import devlAPI.APIdevl;
import devlAPI.APIerror;
import devlAPI.APIfiles;
import devlAPI.enumType.ETypeFile;
import devlRecord.RecStatistics;
import devlRecord.RecordResProc;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static devlAPI.APIprintService.println;
import static devlAPI.APIyymm.*;

/**
 * DAO обработка данных по начислениям заработной платы
 */
public class SalariesDAO {

    /*public static void getArrSalaries(int yymm) {

        APIerror.resetErr();

        var sql = String.format("""
                select s.yymm, s.salary, e.fullName, p.jobTitle \
                    from Salaries s, Emploees e, Positions p \
                WHERE s.emploeesId = e.id \
                    and e.positionId = p.id \
                    and yymm = %d order by e.departmentsId, e.positionId ;
                """, yymm);
        try(Connection conn = APIsqlite.Connect.getConnect()){
            var stateQuery = conn.createStatement();
            var rs = stateQuery.executeQuery(sql);

            while (rs.next()){
                println(String.format("%d %d %20s %20s",
                        rs.getInt(1),
                        rs.getInt(2),
                        rs.getString(3),
                        rs.getString(4) ));
            }

        } catch (Exception ex){
            APIerror.setError(ex.getMessage());
        }
    }*/

    public static RecordResProc incrSalaries(int proc){
        var readFile = APIfiles.readFile("emplAdd_salaries.sql", ETypeFile.SQL_CODE);
        if (!readFile.res()){
            return RecordResProc.getResultErr("Файл emplAdd_salaries.sql не найден");
        }

        var sql = readFile.strData().replace("@pr", String.valueOf(1d + proc/100d));
        var sqlQuery = "SELECT id, jobTitle, salBase, salEnd from buf;";

        try(Connection conn = APIsqlite.Connect.getConnect()){

            var stateMent = conn.createStatement();
            var stateQuery = conn.createStatement();

            stateMent.executeUpdate(sql);
            var rs = stateQuery.executeQuery(sqlQuery);

            var sb = new StringBuffer("Перерасчет по заработной плате\n");
            sb.append(String.format("Перерасчет на %d процентов\n", proc).indent(3));
            while (rs.next()){
                sb.append(
                        String.format("%-25s %6d -> %d\n",rs.getString(2), rs.getInt(3), rs.getInt(4)
                        ).indent(3));
            }

            IRunComd iRunComd = ()->{
                var arrStr = sb.toString().split("\n");
                for (var i=0; i < arrStr.length; i++ ){
                    println(arrStr[i]);
                }
            };

            return new RecordResProc(iRunComd);

        } catch (SQLException ex){
            return new RecordResProc( (IRunComd)()->{
                    println(ex.getMessage());
                } );
        }
    }

    public static boolean verfExistsData(){
        APIerror.resetErr();

        var sql = """
                SELECT  CASE
                    	when (select EXISTS(select * from Salaries s)) > 0 \
                    			THEN 1 \
                    	ELSE 0 
                    END salary
                """;

        var resSql = DAOcomnAPI.getDataFromSQLscript(sql);
        if (!resSql.res()) {
            APIerror.setError(resSql.mes());
            return false;
        }

        return APIdevl.getBooleanFromStr(resSql.strData());
    }

    public static boolean verfExistsData(int yymm) {
        APIerror.resetErr();

        var sql = String.format("""
                SELECT  CASE
                    	when (select EXISTS(select * from Salaries s WHERE yymm = %s)) > 0
                    			THEN 1
                    	ELSE 0
                    END salary
                """, yymm);

        var resSql = DAOcomnAPI.getDataFromSQLscript(sql);
        if (!resSql.res()) {
            APIerror.setError(resSql.mes());
            return false;
        }

        return APIdevl.getBooleanFromStr(resSql.strData());
    }

    public static int getMaxId() {

        APIerror.resetErr();

        var sql = "select ifnull(max(id), 0) from Salaries s";
        var resSql = DAOcomnAPI.getDataFromSQLscript(sql);

        if (!resSql.res()) {
            APIerror.setError(resSql.mes());
            return -1;
        }

        try {
            var maxId = Integer.parseInt(resSql.strData());
            return maxId;
        } catch (NumberFormatException ex) {
            APIerror.setError(ex.getMessage());
            return -1;
        }

    }

    public static RecordResProc addSalaries(int yymm) {

        APIerror.resetErr();

        var MaxId = getMaxId();
        if (APIerror.getErr()) {
            return RecordResProc.getResultErr(APIerror.getMes());
        }

        var lastYYMM = getLastYYMM();
        if (APIerror.getErr()) {
            return RecordResProc.getResultErr(APIerror.getMes());
        }

        if (lastYYMM > 0 && yymm <= lastYYMM){
            return RecordResProc.getResultErr("Отмена операции: повторное начисление");
        }

        var currYYMM = getCurrentYYMM();
        if (APIerror.getErr()){
            return RecordResProc.getResultErr(APIerror.getMes());
        }

        if (yymm > currYYMM){
            return RecordResProc.getResultErr("Интервал не соответствует расчетному месяцу");
        }

        MaxId++;
        try(Connection conn = APIsqlite.Connect.getConnect()){
            var sb = new StringBuffer();
            sb.append("INSERT INTO Salaries (id, yymm, emploeesId, salary)\n");

            boolean firstVal = true;
            for (var item : (List<Emploee>) EmploeeDAO.getAllEmploee().data()) {
                //String strValue;
                if (firstVal){
                    sb.append(String.format("values(%d,%d,%d,%d)",
                            MaxId++, yymm, item.getId(), item.getSalary(conn)) + "\n");
                    firstVal = false;
                    continue;
                }
                sb.append("," + String.format("(%d,%d,%d,%d)",
                        MaxId++, yymm,
                        item.getId(),
                        item.getSalary(conn)) + "\n");
            }

            var statMent = conn.createStatement();
            statMent.executeUpdate(sb.toString());

            return new RecordResProc();

        } catch (Exception ex){
            return RecordResProc.getResultErr(ex.getMessage());
        }
    }

    public static RecStatistics getStatistics(int yymm){

        var resRead = APIfiles.readFile("statistics.sql", ETypeFile.SQL_CODE);
        if (!resRead.res()){
            APIerror.setError( "Файл statistics.sql не найден");
            return null;
        }

        var sql = resRead.strData()
                .replace("@yymm", String.valueOf(yymm));
        var sqlSel = """
                select id, yymm, avg, sumSal, maxSalr, minSalr, emplMax, emplMin \
                from buf bs
                """;

        try(Connection conn = APIsqlite.Connect.getConnect()){
            var stateInit = conn.createStatement();
            stateInit.executeUpdate(sql);

            var stateSel = conn.createStatement();
            var rs = stateSel.executeQuery(sqlSel);

            RecStatistics res = null;
            while (rs.next()){
                res = new RecStatistics(
                        rs.getDouble(3),
                        rs.getInt(4),
                        rs.getInt(5),
                        rs.getInt(6),
                        rs.getString(7),
                        rs.getString(8)
                        );
            }
            return res;

        } catch (SQLException ex){
            println(ex.getMessage());
            return null;
        }
    }

}
