package devlAPI;

import devlRecord.RecordResProc;

/**
 * Обработка сообщений исключаний
 */
public class APIerror {
    private static boolean err = false;
    private static String mes;

    /**
     * После считывания сообщения состояние сбрасывается
     */
    public static String getMes(){
        String res = mes;
        resetErr();
        return res;
    }
    public static boolean getErr(){
        return  err;
    }

    public static void resetErr(){
        err = false;
        mes = "";
    }

    public static void setError(String mesErr){
        err = true;
        mes = mesErr;
    }
}
