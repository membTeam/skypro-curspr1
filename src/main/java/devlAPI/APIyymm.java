package devlAPI;

import models.DAOcomnAPI;

import java.time.LocalDate;

public class APIyymm {

    public static int getYYMMfromDate(LocalDate date){
        var yy = date.getYear();
        var mm = date.getMonth().getValue();

        return (yy - 2000) * 100 + mm;
    }

    public static int getCurrentYYMM() {
        var date = LocalDate.now();
        var yy = date.getYear();
        var mm = date.getMonth().getValue();

        return (yy - 2000) * 100 + mm;
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

    public static int getLastYYMM() {
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
}
