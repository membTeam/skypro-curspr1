package devlAPI;

import devlRecord.RecRunComd;
import devlRecord.RecordComdParams;
import devlRecord.RecordResProc;
import models.*;

import java.util.Arrays;

import static devlAPI.APIprintService.println;

public class ConsRunComand {
    private final RecRunComd[] arrRecordComd;
    private String[] arrBaseComd;
    private RecordResProc recForRunConsoleComd = null;

    {
        // массив доступных консльных команд
        var lsComd = new String[]{
                "print comands -> вывести список допустимых команд",
                "print position -> штатное расписание организации",
                "print department -> вывод справочника по отделам",
                "print emploee -> Вывод справочника по сотрудникам",
                "help dao emploee -> шаблон команды для изм. справочника по сотрудникам",
                "help dao salaries -> статистика по выплатам на заданный период",
        };

        // Массив консольных команд
        arrRecordComd = new RecRunComd[]{
                new RecRunComd("print comands", () -> {
                                    for (var item : lsComd) {
                                        println(item);
                                    }  }),
                new RecRunComd("print department", DepartmentDAO::printAllDepartment),
                new RecRunComd("print position", PositionsDAO::printAllPosition),
                new RecRunComd("print emploee", EmploeeDAO::printAllEmploee),
                new RecRunComd("help dao salaries", ()->{
                            var strPrint = "dao salaries --cmd stat --yymm 2308 статистика на заданный период";
                            println(strPrint);
                        }),
                new RecRunComd("help dao emploee", () -> {
                    var strPrint = """
                            dao emploee --cmd ins --id 0 -> ввод нового сотрудника
                            dao emploee --cmd upd --id 2 -> изменение данных сотрудника
                            dao emploee --cmd del --id 2 -> удаление сотрудника
                            dao emploee --cmd pr --gr 2 -> список сотрудников по отделу                                                         
                            """;
                        println(strPrint.indent(5));
                    }),
        };
    }

    public ConsRunComand(String strComd) {

        int indexSpace = strComd.indexOf(' ');
        if (indexSpace < 0) {
            recForRunConsoleComd = RecordResProc.getResultErr("Не полный ввод команды");
            return;
        }

        arrBaseComd = new String[]{"print", "help"};
        Arrays.sort(arrBaseComd);

        String baseComd = strComd.substring(0, indexSpace);

        // baseComd -> print, help, dao
        if (Arrays.binarySearch(arrBaseComd, baseComd) > -1) {

            // Обработка из списка простых команд
            var item = findSimplComd(strComd); // from arrRecordComd

            if (item != null) {
                recForRunConsoleComd = new RecordResProc(item.iRunComd());
            } else {
                recForRunConsoleComd =
                        RecordResProc.getResultErr(strComd.substring(indexSpace)
                                + " не распознана");
            }
        } else {
            if (!baseComd.equalsIgnoreCase("dao")) {
                recForRunConsoleComd = RecordResProc.getResultErr(baseComd + " не распознана");
            }
        }

        if (recForRunConsoleComd != null) {
            return;
        }

        // baseComd is console comand dao
        String subComd = strComd.substring(indexSpace).trim();
        recForRunConsoleComd = parserConsoleComand(subComd);
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

    private String[] getArrFromString(String str, String delimeter) {
        var arr = str.split(delimeter);
        Arrays.sort(arr);
        return arr;
    }

    public RecordResProc runConsoleComd() {
        return recForRunConsoleComd;
    }

    public RecordResProc parserConsoleComand(String strComd) {

        // формат команды: dao emploee --cmd upd | ins | del --id Number

        String[] arrStructParams = strComd.trim().split("--");
        if (arrStructParams.length < 3) {
            return RecordResProc.getResultErr("Не полный набор параметров");
        }

        var baseModel = arrStructParams[0].trim().toLowerCase();

        var strParams = switch (baseModel) {
            case "emploee" -> DAOEmploeeConsComd.getConsoleParameter();
            case "salaries" -> DAOsalariesConsComand.getConsoleParameter();
            default -> "";
        };
        if (strParams.isEmpty()) {
            return RecordResProc.getResultErr("Модель " + baseModel + " не распознана");
        }

        var arrParams = getArrFromString(strParams, " ");
        var arrRecComdParams = new RecordComdParams[arrStructParams.length - 1];

        // проход по аргументам (--cmd  --id or --yymm ) консольной команды
        for (int i = 1; i < arrStructParams.length; i++) {
            var bufArr = arrStructParams[i].trim().split(" ");

            if (bufArr.length < 2) {
                return RecordResProc.getResultErr("Не полный набор аргументов");
            }

            var par = bufArr[0].toLowerCase().trim(); // it is method
            var val = bufArr[1].trim();

            // верификация идентификатора параметра
            if (Arrays.binarySearch(arrParams, par) < 0) {
                return RecordResProc.getResultErr(arrParams[i] + " не распознано");
            }
            arrRecComdParams[i - 1] = new RecordComdParams(par, val);
        }

        var resInitInstence = switch (baseModel) {
            case "emploee" -> DAOEmploeeConsComd.initInstenceConsComand(arrRecComdParams);
            case "salaries" -> DAOsalariesConsComand.initInstenceForConsComd(arrRecComdParams);
            default -> null;
        };

        if (resInitInstence == null && !resInitInstence.res()) {
            return RecordResProc.getResultErr("Не создан экземпляр-исполнитель консольной команды");
        }

        return resInitInstence;
    }

}
