package models;

import devlAPI.enumType.EMOdfSalaries;

import devlAPI.enumType.EModfModels;
import devlRecord.RecordComdParams;
import devlRecord.RecordResProc;

public class DAOsalariesConsComand {

    private static RecordComdParams[] arrRecComdParams;

    private static EMOdfSalaries eModfSalaries;

    public static RecordResProc initInstenceForConsComd(RecordComdParams[] arrComdParams){
        var method = arrComdParams[0].value();
        var eModfSalalies = switch (method) {
            case "add" -> EMOdfSalaries.ADD_SALARIES;
            case "ls" -> EMOdfSalaries.GET_LS_SALARIES;
            default -> EMOdfSalaries.EMPTY;
        };



        return new RecordResProc();
    }

    public static void setEmodfSalaries(EMOdfSalaries eModf){
        eModfSalaries = eModf;
    }

    public static String getConsoleParameter(){
        return "add yymm";
    }

    public static void setArrRecComdParams(RecordComdParams[] arr){
        arrRecComdParams = arr;
    }

    public static void runConsCommand(){

    }

}
