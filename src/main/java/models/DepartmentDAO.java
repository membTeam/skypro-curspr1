package models;

import APIsqlite.Connect;
import devlAPI.APIerror;
import devlRecord.RecordResProc;
import devlRecord.RecordResProcExt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static devlAPI.APIprintService.println;

public class DepartmentDAO extends DAOabstract<Department> {

    private final String SQL_SELECT_ALL_DEPARMENT = "select * from Departments d;";
    private final String SQL_SELECT_EMPLOEE_ID = "select * from Departments d WHERE id = 1;";


    // --------------- static
    public static RecordResProc getArrayModel(int id) {
        RecordResProc res = null;
        List<Department> lsDepartment = new ArrayList<>();

        var sql = id > 0
                ? new String("select * from Departments d where id = " + id)
                : "select * from Departments d;";

        try (Connection conn = Connect.getConnect()) {
            var statement = conn.createStatement();
            var rs = statement.executeQuery(sql);

            while (rs.next()) {
                lsDepartment.add(
                        new Department(rs.getInt(1),
                                rs.getString(2))
                );
            }

            res = new RecordResProc(true, "ok", lsDepartment);
        } catch (SQLException ex) {
            res = new RecordResProc(false, ex.getMessage(), null);
        } catch (Exception ex) {
            res = new RecordResProc(false, ex.getMessage(), null);
        }

        return res;
    }

    public static void consComd(String strComd) {
        println("From Department");
    }

    public static void printAllDepartment() {
        var dataDepartment = getArrayModel(0);
        if (!dataDepartment.res()) {
            return;
        }

        var lsDepartment = (List<Department>) dataDepartment.data();
        for (var item : lsDepartment) {
            println(item.toString());
        }
    }

    public static RecordResProcExt getInfo(Connection conn, int id) {
        String sql = """
                SELECT CASE
                	when (select EXISTS(select * from Departments d WHERE id = $id)) > 0
                			THEN (select info from Departments d WHERE id = $id)
                	ELSE  'empty'
                END info
                """;

        sql = sql.replace("$id", String.valueOf(id));

        return (conn == null)
                ? DAOcomnAPI.getDataFromSQLscript(sql)
                : DAOcomnAPI.getDataFromSQLscript(conn, sql);
    }

    public static String getAllPositionForNewEmploee(){

        String res ;

        var sb = new StringBuffer();
        var sql = "select id, jobTitle from Positions p WHERE id > 2;";

        try (Connection conn = Connect.getConnect()) {
            var statement = conn.createStatement();
            var rs = statement.executeQuery(sql);

            while (rs.next()) {
                sb.append(rs.getString(1) + " ");
            }

            res = sb.toString().trim();
        }
        catch (SQLException ex){
            res = "err";
            APIerror.setError(ex.getMessage());
        }

        return res;
    }
    public static String getArrPositionForEmploee(int id){

        String res = "";

        if (id == 0 || id < 0){
            // TODO: проверить и доработать эту процедуру
            return getAllPositionForNewEmploee();
        }

        try(Connection conn = Connect.getConnect()){
            var sqlArr = String.format("select arrPosition from Departments d WHERE id = %d", id);
            var resSQLarr = DAOcomnAPI.getDataFromSQLscript(conn, sqlArr);
            if (!resSQLarr.res()){
                APIerror.setError(resSQLarr.mes());
                return res;
            }
            // -----------------------

            var sql = String.format("select id, salary, jobTitle "
                    + "from Positions p WHERE id in(%s);",
                        resSQLarr.strData().replace(" ", ","));

            var statement = conn.createStatement();
            var rs = statement.executeQuery(sql);

            var sb = new StringBuffer();
            while (rs.next()) {
                sb.append(
                        String.format("%3d %6d %s\n",
                                rs.getInt(1), rs.getInt(2), rs.getString(3) )
                );
            }

            res = sb.toString();

        } catch (SQLException e) {
            APIerror.setError(e.getMessage());
        }

        return res;
    }
    public static String strItemModelForConsPrint(int id) {
        var res = getArrayModel(id);
        if (!res.res()){
            APIerror.setError(res.mes());
            return "";
        }

        var sb = new StringBuffer("Справочник подразделений\n");
        for (var item : (List<Department>) res.data()){
            sb.append( String.format("id:%d %s\n", item.getId(), item.getInfo() ) );
        }

        return sb.toString();
    }

    public static Boolean isExistsItem(int id){
        var sql = String.format("select EXISTS (select * from Departments d WHERE id = %d) res",id );
        var resSql = DAOcomnAPI.getDataFromSQLscript(sql);

        if (resSql.res()){
            var value = Integer.parseInt(resSql.strData());
            return value > 0;
        } else {
            APIerror.setError(resSql.mes());
            return false;
        }
    }

    // ---------------------- Override

    @Override
    public Department findEntityById(int id) {
        Department res = null;
        String sqlScript = "select * from Departments d WHERE id = ?;";

        try (Connection conn = APIsqlite.Connect.getConnect()) {
            PreparedStatement preparedStatement = conn.prepareStatement(sqlScript);
            preparedStatement.setInt(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                res = new Department(resultSet.getInt("ID"), resultSet.getString("info"));
            }

        } catch (Exception ex) {
            APIerror.setError(ex.getMessage());
        }

        return res;
    }

    @Override
    public RecordResProc delete(int id) {

        return null;
    }

    @Override
    public RecordResProc create(Department entity) {
        return null;
    }

    @Override
    public Department update(Department entity, String arrField) {
        return null;
    }

}
