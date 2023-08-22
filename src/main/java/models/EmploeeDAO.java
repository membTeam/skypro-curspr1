package models;

import devlAPI.APIdevl;
import devlAPI.APIerror;
import devlRecord.RecordResProc;
import devlRecord.RecordResProcExt;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Struct;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static devlAPI.APIprintService.println;

public class EmploeeDAO extends DAOabstract<Emploee> {
    static private final String SQL_UPD_MODEL = "UPDATE Emploees " +
            "SET %s WHERE id=%d";
    static private final String SQL_SELECT_ALL_EMPLOEES =
            "select id, fullName, departmentsId, positionId  from Emploees e WHERE idUse > 0 ";
    static private final String SQL_SELECT_EMPLOEE_ID = "select * from Emploees e WHERE ID = ?;";
    static private final String SQL_VERF_EXISTS = "select EXISTS(select * from Emploees WHERE id = %d) res";
    static private final String SQL_DELETE = "UPDATE Emploees set idUse = 0 WHERE id = %d";
    private static RecordResProc resProcOk = new RecordResProc(true, "ok", null);

    // ----------------------------

    static public RecordResProc getAllEmploee() {
        RecordResProc res = null;

        List<Emploee> lsEmploee = new ArrayList<>();

        try (Connection conn = APIsqlite.Connect.getConnect()) {
            var statement = conn.createStatement();
            var rs = statement.executeQuery(SQL_SELECT_ALL_EMPLOEES);

            while (rs.next()) {
                lsEmploee.add(
                        new Emploee(
                                rs.getInt(1),
                                rs.getString(2),
                                rs.getInt(3),
                                rs.getInt(4)
                        ));
            }

            res = new RecordResProc(lsEmploee);

        } catch (SQLException ex) {
            res = RecordResProc.getResultErr (ex.getMessage());
        }

        return res;
    }

    static public void printAllEmploee() {
        APIerror.resetErr();

        var resEmploee = getAllEmploee();
        if (!resEmploee.res()) {
            APIerror.setError("Нет данных по сотрудникам");
            return;
        }

        try (Connection conn = APIsqlite.Connect.getConnect()) {
            for (var item : (List<Emploee>) resEmploee.data()) {
                var str = item.toString(conn);
                println(str);
            }
        } catch (SQLException ex) {
            APIerror.setError("err:\n" + ex.getMessage());
        }
    }

    static public int getMaxId(){
        var sql = """
                SELECT  CASE
                    	when (select EXISTS(select * from Emploees e )) > 0
                    			THEN (select max(id) from Emploees)
                    	ELSE 0
                    END id;
                """;
        var resSql = DAOcomnAPI.getDataFromSQLscript(sql);

        return Integer.parseInt(resSql.strData());
    }

    static public void printItemEmploee(int id) {

        APIerror.resetErr();

        try (Connection conn = APIsqlite.Connect.getConnect()) {
            var statement = conn.prepareStatement(SQL_SELECT_EMPLOEE_ID);
            statement.setInt(1, id);

            var rs = statement.executeQuery();
            if (rs.next()) {
                println(new Emploee(
                        rs.getInt(1),
                        rs.getString(2),
                        rs.getInt(3),
                        rs.getInt(4)).toString());
            }

        } catch (SQLException ex) {
            APIerror.setError("err findEntityById:\n" + ex.getMessage());
        }
    }

    public RecordResProcExt verfExistsEmploee(int id) {
        var sql = String.format(SQL_VERF_EXISTS, id);

        return DAOcomnAPI.getDataFromSQLscript(sql);
    }

    // ------------ Overrride -----------------------
    @Override
    public Emploee findEntityById(int id) {
        APIerror.resetErr();

        Emploee emploee = null;
        try (Connection conn = APIsqlite.Connect.getConnect()) {
            var statement = conn.prepareStatement(SQL_SELECT_EMPLOEE_ID);
            statement.setInt(1, id);

            var rs = statement.executeQuery();
            if (rs.next()) {
                emploee = new Emploee(
                        rs.getInt(1),
                        rs.getString(2),
                        rs.getInt(3),
                        rs.getInt(4));

                var idUse = APIdevl.getBooleanFromStr(rs.getString(5));
                emploee.setIdUse(idUse);
            }

            return emploee;

        } catch (SQLException ex) {
            APIerror.setError("err findEntityById:\n" + ex.getMessage().indent(3));
            return null;
        }
    }

    @Override
    public RecordResProc delete(int id) {

        var emploee = findEntityById(id);
        if (emploee == null && !APIerror.getErr()){
            return RecordResProc.getResultErr("Нет данных по сотруднику в БД");
        } else if(APIerror.getErr()){
            return RecordResProc.getResultErr(APIerror.getMes());
        }

        if (!emploee.getIdUse()){
            return RecordResProc.getResultErr("Сотрудник помечен на удаление");
        }

        var sql = String.format(SQL_DELETE, id);  // Удаление заменяется на измПоля idUse = 0
        var resSql = DAOcomnAPI.modfModelFromSQLscript(sql);

        if (!resSql.res()) {
            return RecordResProc.getResultErr(resSql.mes());
        }

        return resProcOk;
    }

    @Override
    public RecordResProc create(Emploee entity) {
        var keyId = getMaxId() + 1;
        var sql = String.format("""
                insert into Emploees(id, fullName, departmentsId, positionId, idUse)
                values(%d,'%s',%d,%d,1)
                """, keyId,
                        entity.getFullName(),
                        entity.getDepartmentsId(),
                        entity.getPositionId());

        return DAOcomnAPI.modfModelFromSQLscript(sql);
    }

    @Override
    public Emploee update(Emploee emploee, String arrFields) {

        APIerror.resetErr();

        // ----------- верификация входящих данных
        if (arrFields.isEmpty()) {
            APIerror.setError("Данные не изменились");
            return null;
        }

        if (findEntityById(emploee.getId()) == null){
            APIerror.setError("Сотрудник не найден");
            return null;
        }

        // -------------------------

        var str = "";
        for (var s : arrFields.split("\n")) {
            var buf = "";

            if (s.equalsIgnoreCase("fullName")) {
                buf = "fullName = '" + emploee.getFullName() + "'";
            } else if (s.equalsIgnoreCase("positionId")) {
                buf = "positionId = " + emploee.getPositionId();
            } else {
                APIerror.setError(s + " не известный тип поля");
                return null;
            }

            if (!buf.isEmpty()) {
                str = str.isEmpty() ? buf : str + ", " + buf;
            }
        }

        var strSql = String.format(SQL_UPD_MODEL, str, emploee.getId());

        var res = DAOcomnAPI.modfModelFromSQLscript(strSql);

        if (!res.res()) {
            APIerror.setError(res.mes());
            return null;
        }

        return findEntityById(emploee.getId());
    }

}
