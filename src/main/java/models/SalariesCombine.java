package models;


import devlAPI.APIerror;
import devlAPI.APIyymm;
import devlRecord.RecordResProc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static devlAPI.APIprintService.println;

public class SalariesCombine {
    private List<Salaries> arrSalaries;
    private int yymm;
    private boolean loadExt = false;

    public SalariesCombine(int yymm){
        this.yymm = yymm;
    }

    public static void printArrSalareesExt(){
        var yymm = APIyymm.getLastYYMM();
        var salariesCombine = new SalariesCombine(yymm);
        salariesCombine.loadDataSalariesExt();

        salariesCombine.printArrSalarees();
    }

    public static void printGroupSalaries(){
        APIerror.resetErr();

        var sql = """
                select yymm, sum(salary) sumSal, max(salary) maxSal, \
                min(salary) minSal, AVG(salary) avg \
                from Salaries s group by yymm ORDER by yymm desc LIMIT 6;
                """;
        try(Connection conn = APIsqlite.Connect.getConnect()){
            var stateQuery = conn.createStatement();
            var rs = stateQuery.executeQuery(sql);

            while (rs.next()){
                println(String.format("yymm:%4d sum:%-6d max:%-6d min:%-6d avg:%.3f",
                        rs.getInt(1),
                        rs.getInt(2),
                        rs.getInt(3),
                        rs.getInt(4),
                        rs.getDouble(5)
                        ));
            }

        } catch (Exception ex){
            APIerror.setError(ex.getMessage());
        }
    }

    public int getYymm(){
        return this.yymm;
    }
    public List<Salaries> getArrSalaries(){
        return arrSalaries;
    }

    public RecordResProc loadDataSalaries(){
        if (!SalariesDAO.verfExistsData(yymm)){
            return RecordResProc.getResultErr("На данный период нет данных");
        }

        var sql = String.format("""
                                SELECT id, yymm, emploeesId, salary \
                                from Salaries s WHERE yymm = %d;
                                """, yymm);
        arrSalaries = new ArrayList<Salaries>();

        try(Connection conn = APIsqlite.Connect.getConnect()){
            Statement statement = conn.createStatement();
            ResultSet rs = statement.executeQuery(sql);
            while (rs.next()) {
                arrSalaries.add(
                        new Salaries(rs.getInt(1),
                                rs.getInt(2),
                                rs.getInt(3),
                                rs.getInt(4)
                                ) );
            }

            return new RecordResProc();

        } catch (SQLException ex){
            return RecordResProc.getResultErr(ex.getMessage());
        }
    }

    public RecordResProc loadDataSalariesExt(){
        if (!SalariesDAO.verfExistsData(yymm)){
            return RecordResProc.getResultErr("На данный период нет данных");
        }

        var sql = String.format("""
                SELECT s.id, yymm, emploeesId, salary, e.fullName \
                	from Salaries s, Emploees e \
                WHERE s.emploeesId = e.id and yymm = %d;
                                """, yymm);
        arrSalaries = new ArrayList<Salaries>();

        try(Connection conn = APIsqlite.Connect.getConnect()){
            Statement statement = conn.createStatement();
            ResultSet rs = statement.executeQuery(sql);
            while (rs.next()) {
                var item = new Salaries(rs.getInt(1),
                        rs.getInt(2),
                        rs.getInt(3),
                        rs.getInt(4)
                );
                item.setFullName(rs.getString(5));
                arrSalaries.add(item);
            }

            loadExt = true;
            return new RecordResProc();

        } catch (SQLException ex){
            return RecordResProc.getResultErr(ex.getMessage());
        }
    }

    public void printArrSalarees(){
            for (var item : arrSalaries){
                if (loadExt){
                    println(item.toStringExt());
                } else {
                    println(item.toString());
                }
            }
    }

}
