package models;

import DevlInterface.IRunComd;
import devlRecord.RecordComdParams;
import devlRecord.RecordResProc;

import static devlAPI.APIprintService.println;

public class DAOsalariesConsComand {

    public static RecordResProc initInstenceForConsComd(RecordComdParams[] arrComdParams) {

        int value;
        try {
            value = Integer.parseInt(arrComdParams[1].value());
        } catch (NumberFormatException ex) {
            return RecordResProc.getResultErr("Ошибка yymm д. быть число");
        }

        var method = arrComdParams[0].value();

        RecordResProc res;
        res = switch (method) {
            case "add" -> addSalaries(value);
            case "ls" -> printArrSalarees(value);
            case "stat" -> printStatistics(value);
            case "incr" -> SalariesDAO.incrSalaries(value);
            default -> RecordResProc.getResultErr(
                    String.format("(%s) нет такой команды", method));
        };

        return res;
    }

    private static RecordResProc printStatistics(int yymm) {
        var statistica = new Statistics(yymm);

        return new RecordResProc((IRunComd) statistica::printEntity);
    }

    private static RecordResProc addSalaries(int yymm) {
        var resAdd = SalariesDAO.addSalaries(yymm);

        IRunComd iRunComd = ()->{
            if (resAdd.res()) {
                println(String.format("Выполнено начисление заработной платы за период %d", yymm));
            } else {
                println(resAdd.mes());
            }
        };

        return new RecordResProc(iRunComd);
    }

    private static RecordResProc printArrSalarees(int yymm) {
        var salareesComb = new SalariesCombine(yymm);
        salareesComb.loadDataSalariesExt();

        IRunComd iRunComd = salareesComb::printArrSalarees;
        return new RecordResProc(iRunComd);
    }

    public static String getConsoleParameter() {
        return "cmd ls add incr pr yymm stat";
    }

}
