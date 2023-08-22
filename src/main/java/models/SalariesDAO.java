package models;

import devlAPI.APIdevl;
import devlAPI.APIerror;
import devlRecord.RecStatistics;
import devlRecord.RecordResProc;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import static devlAPI.APIyymm.*;

/**
 * DAO обработка данных по начислениям заработной платы
 */
public class SalariesDAO extends DAOabstract<Salaries> {

    public static Salaries[] getArrSalaries(int yymm) {
        return null;
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

        } catch (SQLException ex){
            return RecordResProc.getResultErr(ex.getMessage());
        }
    }

    public static RecStatistics getStatistics(int yymm){

        APIerror.resetErr();

        var sql = """
                DELETE from BufStatistics WHERE id> 0;
                INSERT INTO BufStatistics(yymm) values(2308);
                                
                UPDATE BufStatistics
                	set avg  = (select avg(salary) from Salaries WHERE yymm = 2308),
                		maxSalr = (select max(salary) from Salaries WHERE yymm = 2308),
                		minSalr = (select min(salary) from Salaries WHERE yymm = 2308),
                		emplMax  = (select avg(salary) from Salaries WHERE yymm = 2308)
                	where id > 0;
                                
                UPDATE BufStatistics
                	set
                		sumSal = (select sum(salary) from Salaries WHERE yymm = 2308),
                		emplMax = (select (e.fullName || ' ' || salary || ' руб') res
                			from Salaries s, Emploees e
                			WHERE s.emploeesId = e.id
                				and yymm = 2308
                				and salary = (select max(salary) from Salaries s2 WHERE yymm = 2308)),
                			
                		emplMin = (select (e.fullName || ' ' || salary || ' руб') res
                		from Salaries s, Emploees e
                		WHERE s.emploeesId = e.id
                			and yymm = 2308
                			and salary = (select min(salary) from Salaries s2 WHERE yymm = 2308))
                where id > 0;	
                """;
        var sqlSel = "select id,yymm, avg, sumSal, maxSalr, minSalr, emplMax, emplMin from BufStatistics bs";

        try(Connection conn = APIsqlite.Connect.getConnect()){
            var stateInit = conn.createStatement();
            stateInit.executeUpdate(sql);

            var stateSel = conn.createStatement();
            var rs = stateSel.executeQuery(sqlSel);

            while (rs.next()){
                // double avg, int sumSal, int maxSalr, int minSalr, String emplMax, String emplMin
                return new RecStatistics(
                        rs.getDouble(3),
                        rs.getInt(4),
                        rs.getInt(5),
                        rs.getInt(6),
                        rs.getString(7),
                        rs.getString(8)
                        );
            }

        } catch (SQLException ex){
            APIerror.setError(ex.getMessage());
        }


        return null;
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
