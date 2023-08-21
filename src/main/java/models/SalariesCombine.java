package models;


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
