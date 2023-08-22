package models;

import DevlInterface.IRunComd;
import devlAPI.enumType.EMOdfSalaries;

import devlAPI.enumType.EModfModels;
import devlRecord.RecordComdParams;
import devlRecord.RecordResProc;

import java.lang.module.ModuleDescriptor;

public class DAOsalariesConsComand {

    private static RecordComdParams[] arrRecComdParams;

    private static EMOdfSalaries eModfSalaries;

    public static RecordResProc initInstenceForConsComd(RecordComdParams[] arrComdParams){

        int yymm;
        try{
            yymm = Integer.parseInt(arrComdParams[1].value());
        } catch (NumberFormatException ex){
            return RecordResProc.getResultErr("Ошибка yymm д. быть число");
        }

        var method = arrComdParams[0].value();
        var res = switch (method) {
            case "add" -> addSalaries(yymm);
            case "ls" -> printArrSalarees(yymm);
            case "stat" -> printStatistics(yymm);
            default -> RecordResProc.getResultErr(
                    String.format("(%s) нет такой команды", method));
        };

        return res;
    }

    private static RecordResProc printStatistics(int yymm){
        var statistica = new Statistics(yymm);

        return new RecordResProc((IRunComd) statistica::printEntity);
    }

    private static RecordResProc addSalaries(int yymm) {

        return null;
    }

    private static RecordResProc printArrSalarees(int yymm) {
        var salareesComb = new SalariesCombine(yymm);
        salareesComb.loadDataSalariesExt();

        IRunComd iRunComd = salareesComb::printArrSalarees;
        return new RecordResProc(iRunComd);
    }


    public static String getConsoleParameter(){
        return "cmd ls add yymm stat";
    }

}
