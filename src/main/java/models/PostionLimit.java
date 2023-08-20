 package models;

 import devlAPI.APIerror;
 import devlAPI.APIfiles;
 import devlAPI.enumType.ETypeFile;
 import devlRecord.RecordResProc;

 import java.sql.Connection;
 import java.sql.SQLException;

 import static devlAPI.APIprintService.println;

 public class PostionLimit {
    private int id;
    private int posUsed;
    private int posLimit;

    public PostionLimit(int id){
        /*var resRead = APIfiles.readFile("limitPosition.sql", ETypeFile.sqlcode);
        if (!resRead.res()){
            APIerror.setError(resRead.mes());
            return;
        }*/

        var sql = """
                DELETE from LimitPosition WHERE id > 0;
                INSERT into LimitPosition (id) values(@id);
                UPDATE LimitPosition set posLimit  = (SELECT numLimit
                		from Positions p WHERE id = @id)
                	WHERE id > 0;
                UPDATE LimitPosition set posUsed = (select COUNT(*) num
                		from Emploees e WHERE positionId = @id)
                	WHERE id > 0;
                """;

        sql = sql.replace("@id", String.valueOf(id));

        var sqlSelect = "select id, posUsed, posLimit from LimitPosition";

        try(Connection conn = APIsqlite.Connect.getConnect()){
            var comdUpd = conn.createStatement();
            comdUpd.executeUpdate(sql);

            var comdSelect = conn.createStatement();
            var rs = comdSelect.executeQuery(sqlSelect);

            while (rs.next()){
                id = rs.getInt(1);
                posUsed = rs.getInt(2);
                posLimit = rs.getInt(3);
            }

        } catch (Exception ex){
            APIerror.setError("err findEntityById:\n" + ex.getMessage());
        }
    }

    // ------------------------ get

     public int getId() {
         return id;
     }

     public int getPosUsed() {
         return posUsed;
     }

     public int getPosLimit() {
         return posLimit;
     }
 }
