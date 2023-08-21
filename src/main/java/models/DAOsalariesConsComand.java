package models;

import DevlInterface.IRunComd;
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

        int yymm;
        try{
            yymm = Integer.parseInt(arrComdParams[1].value());
        } catch (NumberFormatException ex){
            return RecordResProc.getResultErr("Ошибка yymm д. быть число");
        }

        // ls
        if (eModfSalalies == EMOdfSalaries.GET_LS_SALARIES){
            var salareesComb = new SalariesCombine(yymm);
            salareesComb.loadDataSalariesExt();

            IRunComd iRunComd = salareesComb::printArrSalarees;
            return new RecordResProc(iRunComd);
        }

        return new RecordResProc();
    }

    public static void setEmodfSalaries(EMOdfSalaries eModf){
        eModfSalaries = eModf;
    }

    public static String getConsoleParameter(){
        return "cmd ls add yymm";
    }

    public static void setArrRecComdParams(RecordComdParams[] arr){
        arrRecComdParams = arr;
    }

    public static void runConsCommand(){

    }

}
