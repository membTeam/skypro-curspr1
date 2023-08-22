package devlAPI;

import java.util.Calendar;

public class APIdevl {
    public static boolean getBooleanFromStr(String str) {

        // Значения boolean в БД with 0 or 1
        boolean res = switch (str.toLowerCase()) {
            case "0", "false" -> false;
            case "1", "true" -> true;
            default -> {
                try{
                    var iValue = Integer.parseInt(str);
                    yield iValue > 0;
                } catch (NumberFormatException ex){
                    APIerror.setError("Значение (" + str + ") не соответствует boolean");
                    yield false;
                }
            }
        };

        return res;
    }

    public static String replaceStrByInt(String value, String arg, int argInt) {
        return value.replace(arg, String.valueOf(argInt));
    }

}