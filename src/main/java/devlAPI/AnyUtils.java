package devlAPI;

import java.util.Calendar;

public class AnyUtils {
    private static int yymmCurr = -1;

    private static int setYYMM_curr(){
        var calendar = Calendar.getInstance();

        int yyyy = calendar.get(Calendar.YEAR);
        int mm = calendar.get(Calendar.MONTH);

        return  (yyyy-2000) *100 + mm;
    }

    public static int getCurrYYMM(){
        if (yymmCurr < 0)
            yymmCurr = setYYMM_curr();
        return yymmCurr;
    }
}