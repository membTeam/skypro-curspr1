package devlAPI;

import DevlInterface.IRunComd;
import devlAPI.enumType.EModfModels;
import devlRecord.RecRunComd;
import devlRecord.RecordComdParams;
import devlRecord.RecordResProc;
import models.*;

import java.util.Arrays;

import static devlAPI.APIprintService.println;

public class ConsRunComand {
    private String err = "";
    private RecRunComd recRunSimpleComd;
    private boolean isSimpl = true;
    private final RecRunComd[] arrRecordComd;
    private DAObaseConsComand daoBaseConsComd;

    public String getErr() {
        return err;
    }
    public boolean isErr() {
        return !err.isEmpty();
    }

    {
        var lsComd = new String[]{
                "print comands -> вывести список допустимых команд",
                "print position -> штатное расписание организации",
                "print department -> вывод справочника по отделам",
                "print emploee -> Вывод справочника по сотрудникам",
                "help dao emploee -> шаблон команды для изм. справочника по сотрудникам",
        };

        IRunComd iListComd = () -> {
            for (var item : lsComd) {
                println(item);
            }
        };

        arrRecordComd = new RecRunComd[]{
                new RecRunComd("print comands", iListComd),
                new RecRunComd("print department", DepartmentDAO::printAllDepartment),
                new RecRunComd("print position", PositionsDAO::printAllPosition),
                new RecRunComd("print emploee", EmploeeDAO::printAllEmploee),
        };
    }

    public ConsRunComand(String strComd) {

        int indexSpace = strComd.indexOf(' ');
        if (indexSpace < 0) {
            err = "Не полный ввод команды";
            return;
        }

        var arrBaseComd = new String[] {"print","help"};
        Arrays.sort(arrBaseComd);

        String baseComd = strComd.substring(0, indexSpace);

        if (Arrays.binarySearch(arrBaseComd,baseComd) > -1) {
            var item = findSimplComd(strComd);
            if (item != null) {
                recRunSimpleComd = item;
            } else {
                err = strComd.substring(indexSpace) + " не распознана";
            }
        } else {
            if (baseComd.equals("dao")){
                isSimpl = false;
            } else {
                err = baseComd + " не распознана";
            }
        }

        if (isErr() || isSimpl){
            return;
        }

        String subComd = strComd.substring(indexSpace).trim();
        var resParser = parserConsoleComand(subComd);

        if (resParser.res()) {
            daoBaseConsComd = (DAObaseConsComand) resParser.data();
        } else {
            err = resParser.mes();
        }
    }


    // -------------------------------------------------

    private RecRunComd findSimplComd(String comd) {

        for (int i = 0; i < arrRecordComd.length; i++) {
            if (arrRecordComd[i].comd().equals(comd)) {
                return arrRecordComd[i];
            }
        }
        return null;
    }

    private String[] getArrFromString(String str, String delimeter){
        var arr = str.split(delimeter);
        Arrays.sort(arr);
        return arr;
    }

    public RecordResProc runConsoleComd() {
        if (!err.isEmpty()) {
            return RecordResProc.getResultErr(err);
        }

        if (isSimpl) {
            recRunSimpleComd.iRunComd().apply();
            return new RecordResProc(true, "ok", null);
        }

        return new RecordResProc(daoBaseConsComd);
    }

    public RecordResProc parserConsoleComand(String strComd) {

        // формат команды: dao emploee --cmd upd | ins | del --id Number

        String[] arrStructParams = strComd.trim().split("--");
        if (arrStructParams.length < 3){
            return RecordResProc.getResultErr("Не полный набор параметров");
        }

        var baseModel = arrStructParams[0].trim().toLowerCase();

        var strParams = switch (baseModel){
            case "emploee" -> DAOEmploeeConsComd.getConsoleParameter();
            case "salaries" -> DAOsalariesConsComand.getConsoleParameter();
            default -> "";
        };
        if (strParams.isEmpty()) {
            return RecordResProc.getResultErr("Модель " + baseModel + " не распознана");
        }

        var arrParams = getArrFromString(strParams," ");
        var arrRecComdParams = new RecordComdParams[arrStructParams.length - 1];

        // проход по аргументам (--cmd  --id ) консольной команды
        for (int i = 1; i < arrStructParams.length; i++) {
            var bufArr = arrStructParams[i].trim().split(" ");

            if (bufArr.length < 2){
                return RecordResProc.getResultErr("Не полный набор аргументов");
            }

            var par = bufArr[0].toLowerCase().trim(); // it is method
            var val = bufArr[1].trim();

            // верификация идентификатора параметра
            if ( Arrays.binarySearch(arrParams, par) < 0) {
                return RecordResProc.getResultErr(arrParams[i] + " не распознано");
            }
            arrRecComdParams[i - 1] = new RecordComdParams(par, val);
        }

        var res = switch (baseModel){
            case "emploee" -> DAOEmploeeConsComd.initInstenceForConsComd(arrRecComdParams);
            case "salaries" -> DAOsalariesConsComand.initInstenceForConsComd(arrRecComdParams);
            default -> null;
        };

        if (res == null){
            return RecordResProc.getResultErr("Не создан экземпляр-исполнитель консольной команды");
        }

        return new RecordResProc(res);

        /*var method = arrRecComdParams[0].value();
        var eModfModel = switch (method) {
            case "upd" -> EModfModels.UPDATE;
            case "ins" -> EModfModels.INSERT;
            case "del" -> EModfModels.DELETE;
            default -> EModfModels.EMPTY;
        };

        if (eModfModel == EModfModels.EMPTY){
            return RecordResProc.getResultErr("Метод " + method + " не распознан д/быть upd ins del");
        }

        var strId = arrRecComdParams[1].value();
        int id;
        try {
            id = eModfModel == EModfModels.INSERT ? -1 : Integer.parseInt(strId);
        } catch (NumberFormatException es) {
            return RecordResProc.getResultErr("Ошибка id д. быть число");
        }

        var daoBaseConsComd = switch (baseModel) {
            case "emploee" -> new DAOEmploeeConsComd(id, eModfModel);
            default -> null;
        };

        if (daoBaseConsComd == null){
            return RecordResProc.getResultErr("Модель обработки не создана");
        }

        if (eModfModel != EModfModels.INSERT
                && !((DAOEmploeeConsComd) daoBaseConsComd).isExistsModel()) {
            return RecordResProc.getResultErr(
                    String.format("%s нет данных по id %d", baseModel, id));
        }

        return new RecordResProc(daoBaseConsComd);*/
    }

}
