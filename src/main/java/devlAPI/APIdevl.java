package devlAPI;

import java.util.Calendar;

public class APIdevl {
    public static String replaceStrByInt(String str, String arg, int argInt){
        return str.replace(arg, String.valueOf(argInt));
    }

    public static int getCurrentYear(){
        var calendar = Calendar.getInstance();
        int yyyy = calendar.get(Calendar.YEAR);

        return  yyyy;
    }

}