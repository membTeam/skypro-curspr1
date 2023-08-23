package models;

import DevlInterface.IRunComd;
import devlAPI.enumType.EMOdfSalaries;
import devlRecord.RecordComdParams;
import devlRecord.RecordResProc;

import static devlAPI.APIprintService.println;

public class DAOsalariesConsComand {

    private static RecordComdParams[] arrRecComdParams;

    private static EMOdfSalaries eModfSalaries;

    public static RecordResProc initInstenceForConsComd(RecordComdParams[] arrComdParams) {

        int value;
        try {
            value = Integer.parseInt(arrComdParams[1].value());
        } catch (NumberFormatException ex) {
            return RecordResProc.getResultErr("Ошибка yymm д. быть число");
        }

        var method = arrComdParams[0].value();
        var res = switch (method) {
            case "add" -> addSalaries(value);
            case "ls" -> printArrSalarees(value);
            case "stat" -> printStatistics(value);
            case "incr" -> incrSalaries(value);
            default -> RecordResProc.getResultErr(
                    String.format("(%s) нет такой команды", method));
        };

        return res;
    }

    private static RecordResProc printStatistics(int yymm) {
        var statistica = new Statistics(yymm);

        return new RecordResProc((IRunComd) statistica::printEntity);
    }

    private static RecordResProc incrSalaries(int value) {
        return SalariesDAO.incrSalaries(value);
    }

    private static RecordResProc addSalaries(int yymm) {
        var resAdd = SalariesDAO.addSalaries(yymm);

        return new RecordResProc((IRunComd) () -> {
                    if (resAdd.res()) {
                        println(String.format("Выполнено начисление заработной платы за период %d", yymm));
                    } else {
                        println(resAdd.mes());
                    }
                });
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
