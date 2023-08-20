package devlAPI;

import APIsqlite.InitialData;
import DevlInterface.IRunComd;
import devlAPI.enumType.EModfModels;
import devlRecord.*;
import models.*;


import java.util.Arrays;
import java.util.regex.Pattern;

import static devlAPI.APIprintService.*;


/**
 * org.curspr.ConsParser Менеджер обработчиков команд из консоли
 */
public class ConsParser {

    private static interface LIcomdModf {
        RecordResProc apply(String iId, String Item);
    }
    private static ConsParser.LIcomdModf iIcomdModf;
    private static RecRunComd[] arrRecordComd;
    private static RecordComdCons[] arrRecordComdConsModels;
    private static DAOabstract modelsDAO;
    private static String[] arrTypeModf = new String[]{"upd", "ins", "del"};

    static {
        var lsComd = new String[]{
                "list comd -> вывести список допустимых команд",
                "print position.all -> штатное расписание организации",
                "print department.all -> вывод справочника по отделам",
                "print emploee.all -> Вывод справочника по сотрудникам",
                "init emploee -> загрузка начальных данных справочника по сотрудникам (из файла)"
        };

        IRunComd iListComd = () -> {
            for (var item : lsComd) {
                println(item);
            }
        };

        arrRecordComd = new RecRunComd[]{
                new RecRunComd("list comd", iListComd),
                new RecRunComd("print department.all", DepartmentDAO::printAllDepartment),
                new RecRunComd("print position.all", PositionsDAO::printAllPosition),
                new RecRunComd("print emploee.all", EmploeeDAO::printAllEmploee),
                new RecRunComd("init emploee", InitialData::initialEmploee)
        };

        arrRecordComdConsModels = new RecordComdCons[]{
                new RecordComdCons("emploee", EmploeeDAO::consComd),
                new RecordComdCons("department", DepartmentDAO::consComd),
                new RecordComdCons("salaries", SalariesDAO::consComd)
        };

        Arrays.parallelSort(arrTypeModf);
    }

    public static RecordResProc menedjConsoleComd(String strInput) {
        RecordResProc res = null;

        if (strInput.substring(0, 3).toLowerCase().equals("dao")) {
            res = consoleModf(strInput);
        } else {
            res = consoleComd(strInput);
        }

        return res;
    }

    /**
     * Return Объект, который будет обрабатывать консВвод параметров для обновления модели
     *
     * @param strComd
     * @return
     */
    public static RecordResProc consoleModf(String strComd) {

        // формат команды: dao emloee id:number upd|ins|del

        DAObaseConsComd daoBaseConsComd = null;
        int id;

        var strTempl = "\\s+(\\w+)\\s+id:(\\d+)\\s+(upd|ins|del)";
        var pattern = Pattern.compile(strTempl);
        var matcher = pattern.matcher(strComd);

        String strModel = "",
                strId = "",
                method = "";

        if (matcher.find()) {
            strModel = matcher.group(1);
            strId = matcher.group(2);
            method = matcher.group(3);
        }

        if (method.isEmpty() || Arrays.binarySearch(arrTypeModf, method) < 0) {
            return RecordResProc.getResultErr("Ошибка модификатор д.быть upd or ins or del");
        }

        if (strModel.isEmpty() || strId.isEmpty()) {
            return RecordResProc.getResultErr("Ошибка формата команды");
        }

        var eModfMode = switch (method.toLowerCase()){
            case "upd" -> EModfModels.valueOf("UPDATE");
            case "ins" -> EModfModels.valueOf("INSERT");
            default    -> EModfModels.valueOf("DELETE");
        };

        var eModfModel = method.equals("upd") ? EModfModels.UPDATE
                : method.equals("del") ? EModfModels.DELETE
                : EModfModels.INSERT;

        try {
            id = eModfMode == EModfModels.INSERT ? -1 : Integer.parseInt(strId);
        } catch (NumberFormatException es) {
            return RecordResProc.getResultErr("Ошибка id д. быть числом");
        }

        daoBaseConsComd = switch (strModel.trim()) {
            case "emploee" -> new DAOEmploeeConsComd(id, eModfModel);
            default -> null;
        };

        if (daoBaseConsComd != null
                && eModfModel != EModfModels.INSERT
                && !((DAOEmploeeConsComd) daoBaseConsComd).isExistsModel() ){
            return RecordResProc.getResultErr(
                    String.format("%s нет данных по id %d", strModel, id));
        }

        return new RecordResProc(daoBaseConsComd);
    }

    public static RecordResProc consoleComd(String strComd) {

        RecordResProc res = null;

        boolean exists = false;
        var strInput = strComd.trim();
        for (var item : arrRecordComd) {
            if (item.comd().equals(strInput)) {
                exists = true;
                item.iRunComd().apply();
                break;
            }
        }

        if (!exists) {
            res = RecordResProc.getResultErr("Команда не распознана");
        } else {
            res = new RecordResProc();
        }

        return res;
    }

}
