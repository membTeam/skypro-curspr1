package models;

import devlAPI.APIerror;
import devlRecord.RecordResProc;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

/**
 * DAO обработка данных по начислениям заработной платы
 */
public class SalariesDAO extends DAOabstract<Salaries> {

    private static Salaries[] arrSalaries;

    public static Salaries[] getArrSalaries(int yymm) {
        return null;
    }

    private static int getCurrentYYMM() {
        var date = LocalDate.now();
        var yy = date.getYear();
        var mm = date.getMonth().getValue();

        return yy * 100 + mm;
    }

    public static int incYYMM(int yymm) {

        if (yymm == 0) {
            return getCurrentYYMM();
        }

        int yy = yymm / 100;
        int mm = yymm % 100;

        var dateLast = LocalDate.of((yy+2000), mm, 1);
        var dateNext = dateLast.plusMonths(1);

        return (dateNext.getYear() - 2000) * 100 + dateNext.getMonth().getValue();
    }

    public static boolean verfExistsData(int yymm) {

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

        var res = Integer.parseInt(resSql.strData());
        return res > 0;
    }

    public static int getMaxYYMM() {
        var sql = """
                SELECT  CASE
                    	when (select EXISTS(select * from Salaries s)) > 0
                    			THEN (select max(yymm) from Salaries s2)
                    	ELSE 0
                    END salary
                """;

        var resSql = DAOcomnAPI.getDataFromSQLscript(sql);
        if (!resSql.res()) {
            APIerror.setError(resSql.mes());
            return -1;
        }

        return Integer.parseInt(resSql.strData());
    }

    public static int getMaxId() {

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

    public static RecordResProc setSalaries(int yymm) {
            /*INSERT INTO Salaries (id, yymm, emploeesId, salary)
                VALUES(0, 0, 0, 0);*/

        var MaxId = getMaxId();
        if (APIerror.getErr()) {
            return RecordResProc.getResultErr(APIerror.getMes());
        }

        var lastYYMM = getMaxYYMM();
        if (APIerror.getErr()) {
            return RecordResProc.getResultErr(APIerror.getMes());
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
                        MaxId++, yymm, item.getId(), item.getSalary(conn)) + "\n");


            }

            var statMent = conn.createStatement();
            statMent.executeUpdate(sb.toString());

            return new RecordResProc();

        } catch (SQLException ex){
            return RecordResProc.getResultErr(ex.getMessage());
        }

    }

    // ----------------- Override

    @Override
    public Salaries findEntityById(int id) {
        return null;
    }

    @Override
    public RecordResProc delete(int id) {
        return null;
    }

    @Override
    public RecordResProc create(Salaries entity) {
        return null;
    }

    @Override
    public Salaries update(Salaries entity, String arrFields) {
        return null;
    }
}
