package models;

import devlAPI.APIerror;
import devlRecord.RecordResProc;
import devlRecord.RecordResProcExt;

import java.sql.Connection;
import java.sql.SQLException;
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

    public static void consComd(String strComd) {
        var templMatcher = "(\\w+):(\\w++)";

        var pattern = Pattern.compile(templMatcher);
        var matcher = pattern.matcher(strComd);

        var keyAndValue = "";
        String key = "";
        String value = "";
        if (matcher.find()) {
            key = matcher.group(1);
            value = matcher.group(2);
        } else {
            println("Команда не обработана. \nПроверьте правильность написания команды");
        }

        if (key.equals("print")) {
            if (value.equals("all")) {
                printAllEmploee();
                return;
            } else if (value.equals("item")) {
                templMatcher = "id:(\\d++)";

                pattern = Pattern.compile(templMatcher);
                matcher = pattern.matcher(strComd);

                if (matcher.find()) {
                    printItemEmploee(Integer.parseInt(matcher.group(1)));
                    return;
                }
            } else {
                println("""
                        Список команд для Emploees:
                            dao emploee print:help
                            dao emploee print:all
                            dao emploee print:item id:Number                            
                        """
                );
            }
        }
    }

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

            res = new RecordResProc(true, "ok", lsEmploee);

        } catch (SQLException ex) {
            res = new RecordResProc(false, ex.getMessage(), null);
            println("err:\n" + ex.getMessage());
        }

        return res;
    }

    static public void printAllEmploee() {
        var resEmploee = getAllEmploee();
        if (!resEmploee.res()) {
            return;
        }

        try (Connection conn = APIsqlite.Connect.getConnect()) {
            for (var item : (List<Emploee>) resEmploee.data()) {
                var str = item.toString(conn);
                println(str);
            }
        } catch (SQLException ex) {
            println("err:\n" + ex.getMessage());
        }
    }

    static private int getMaxId(){
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
            println("err findEntityById:\n" + ex.getMessage());
        }
    }

    private RecordResProcExt verfExistsEmploee(int id) {
        var sql = String.format(SQL_VERF_EXISTS, id);

        return DAOcomnAPI.getDataFromSQLscript(sql);
    }

    // ------------ Overrride -----------------------
    @Override
    public Emploee findEntityById(int id) {
        Emploee resProc = null;
        try (Connection conn = APIsqlite.Connect.getConnect()) {
            var statement = conn.prepareStatement(SQL_SELECT_EMPLOEE_ID);
            statement.setInt(1, id);

            var rs = statement.executeQuery();
            if (rs.next()) {
                resProc = new Emploee(
                        rs.getInt(1),
                        rs.getString(2),
                        rs.getInt(3),
                        rs.getInt(4));
            }

        } catch (SQLException ex) {
            println("err findEntityById:\n" + ex.getMessage());
        }
        return resProc;
    }

    @Override
    public RecordResProc delete(int id) {
        var verfExist = verfExistsEmploee(id);
        if (!verfExist.res()) {
            return RecordResProc.getResultErr(verfExist.mes());
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

        if (arrFields.isEmpty()) {
            APIerror.setError("Данные не изменились");
            return null;
        }

        var str = "";
        for (var s : arrFields.split("\n")) {
            var buf = "";

            if (s.equalsIgnoreCase("fullName")) {
                buf = "fullName = '" + emploee.getFullName() + "'";
            } else if (s.equalsIgnoreCase("positionId")) {
                buf = "positionId = " + emploee.getPositionId();
            } else {
                continue;
            }

            if (!buf.isEmpty()) {
                str = str.isEmpty() ? buf : ", " + buf;
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
