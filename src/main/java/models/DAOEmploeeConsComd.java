package models;

import DevlInterface.IRunComd;
import devlAPI.APIerror;
import devlAPI.ConsParserItem;
import devlAPI.enumType.EModfModels;
import devlRecord.RecordComdParams;
import devlRecord.RecordResProc;

import static devlAPI.APIprintService.println;

public class DAOEmploeeConsComd extends DAObaseConsComand {
    private Emploee model = null;
    private final EModfModels eModfModels;
    private String strDeparment;

    public DAOEmploeeConsComd(int id, EModfModels eModfModels) {

        super(new EmploeeDAO(), id, eModfModels);

        this.eModfModels = eModfModels;
        if (this.eModfModels != EModfModels.INSERT) {
            model = ((EmploeeDAO) getModelDAO()).findEntityById(id);
        }

        initArrConsParserItemModf();
    }

    // ---------------- static
    public static String getConsoleParameter(){
        return "cmd id pr gr";
    }

    public static RecordResProc initInstenceConsComand(RecordComdParams[] arrComdParams){

        var method = arrComdParams[0].value();
        var eModfModel = switch (method) {
            case "upd" -> EModfModels.UPDATE;
            case "ins" -> EModfModels.INSERT;
            case "del" -> EModfModels.DELETE;
            case "pr"  -> EModfModels.PRINT;
            default -> EModfModels.EMPTY;
        };

        if (eModfModel == EModfModels.EMPTY){
            return RecordResProc.getResultErr("Метод " + method + " не распознан д/быть upd ins del");
        }

        var strId = arrComdParams[1].value();
        int id;
        try {
            id = eModfModel == EModfModels.INSERT ? -1 : Integer.parseInt(strId);
        } catch (NumberFormatException es) {
            return RecordResProc.getResultErr("Ошибка id д. быть число");
        }

        // Вывод сообщения в консоль
        if (eModfModel == EModfModels.PRINT){
            EmploeeDAO.setIdValue(id);
            return new RecordResProc((IRunComd) EmploeeDAO::printEmploeesForDepartment);
        }

        var daoBaseConsComd = new DAOEmploeeConsComd(id, eModfModel);

        if (eModfModel != EModfModels.INSERT
                && !((DAOEmploeeConsComd) daoBaseConsComd).isExistsModel()) {
            return RecordResProc.getResultErr(
                    String.format("%s нет данных по id %d", "Emploee", id));
        }

        return new RecordResProc(daoBaseConsComd);
    }

    // ------------------------------------------------

    @Override
    public boolean isExistsModel() {
        return model != null;
    }

    private int getDepartmentId(ConsParserItem[] arrConsParserItemModf) {
        if (model != null) {
            return model.getDepartmentsId();
        }

        // for insert Emploee
        ConsParserItem selItem = null;
        for (var item : arrConsParserItemModf) {
            if (item.getStrField().equals("departmentsId")) {
                selItem = item;
                break;
            }
        }

        // TODO: Доработка сообщений error
        if (selItem == null) {
            APIerror.setError("getDepartmentId() не обработано");
            return -1;
        }

        var strInput = selItem.getStrInput();
        try {
            return Integer.parseInt(strInput);
        } catch (NumberFormatException ex) {
            APIerror.setError(ex.getMessage());
            return -1;
        }
    }

    public String getStrPosition(ConsParserItem[] arrConsParserItemModf) {
        var depatmentsId = getDepartmentId(arrConsParserItemModf);
        if (depatmentsId < 0) {
            return "";
        }

        var resProc = DepartmentDAO.getArrPositionForEmploee(depatmentsId);
        if (resProc.isEmpty()) {
            APIerror.setError("getStrPosition: Нет данных по справочнику должностей");
            return "";
        }

        int curPositionId = model != null ? model.getPositionId() : -1;

        var strPrint = "";
        if (curPositionId > 0) {
            strPrint = String.format("%s (%d)", resProc, curPositionId);
        } else {
            strPrint = resProc;
        }

        strPrint += " выберите значение: ";

        return strPrint;
    }

    private ConsParserItem[] initArrayConsParser() {

        if (eModfModels == EModfModels.UPDATE) {
            return new ConsParserItem[]{
                    new ConsParserItem(
                            "\t(" + model.getFullName() + ") изменение fullName: ",
                            "fullName"),
                    new ConsParserItem(
                            "strPostion",
                            "positionId"
                    )};

        } else if (eModfModels == EModfModels.INSERT) {
            if (!getStrPrintAllDeparment()) {
                APIerror.setError("Нет данных по справочнику подразделений");
                return null;
            }

            return new ConsParserItem[]{
                    new ConsParserItem(
                            "введите fullName: ",
                            "fullName"),
                    new ConsParserItem(
                            strDeparment + "введите подразделение: ",
                            "departmentsId"),
                    new ConsParserItem(
                            "strPosition",
                            "positionId")
            };

        } else {
            return new ConsParserItem[]{
                    new ConsParserItem(
                            "Подтвердите удаление сотрудника (Y-да  N-нет) ", "id"
                    )};
        }
    }

    private void initArrConsParserItemModf() {

        if (eModfModels != EModfModels.INSERT && model == null) {
            return;
        }

        var array = initArrayConsParser();
        setArrConsParserItemModf(array);
    }

    private boolean getStrPrintAllDeparment() {

        var strPrint = DepartmentDAO.strItemModelForConsPrint(0);
        if (strPrint.isEmpty()) {
            if (APIerror.getErr()) {
                println(APIerror.getMes());
            }

            return false;
        }

        strDeparment = strPrint;

        return true;
    }


    // ----------------- save data
    private int verfLimitPosition(int positionId) {
        var limitPosition = new PostionLimit(positionId);
        if (APIerror.getErr()) {
            return -1;
        }

        return limitPosition.getPosLimit() - limitPosition.getPosUsed();
    }

    private RecordResProc saveUpdate() {

        var strInput = getItemFromArrItemModf("fullName");
        var stringBuffer = new StringBuffer();
        if (!strInput.isEmpty() && !model.getFullName().equalsIgnoreCase(strInput)) {
            model.setFullName(strInput);
            stringBuffer.append("fullName\n");
        }

        strInput = getItemFromArrItemModf("positionId");
        if (!strInput.isEmpty()) {
            var inputPositionId = Integer.parseInt(strInput);

            if (model.getPositionId() != inputPositionId) {
                // Проверка переполнения штатного расписания
                var verLimit = verfLimitPosition(inputPositionId);
                if (APIerror.getErr()) {
                    return RecordResProc.getResultErr(APIerror.getMes());
                } else if (verLimit < 0) {
                    return RecordResProc.getResultErr("Штатное расписание заполнено.");
                }

                var existsPositionIsOne = PositionsDAO.isExistsPositionInDepartment(model.getDepartmentId(), inputPositionId);
                if (APIerror.getErr()){
                    return RecordResProc.getResultErr(APIerror.getMes());
                }
                if (existsPositionIsOne){
                    return RecordResProc.getResultErr("Есть сотрудник на этой должности (только один сотрудник)");
                }

                model.setPositionId(inputPositionId);
                stringBuffer.append("positionId\n");
            }
        }

        if (stringBuffer.isEmpty()) {
            return RecordResProc.getResultErr("Нет данных для обработки");
        }

        var resUpd = ((EmploeeDAO) getModelDAO()).update(model, stringBuffer.toString());

        if (resUpd == null) {
            return RecordResProc.getResultErr(APIerror.getMes());
        } else {

            return new RecordResProc(resUpd);
        }
    }

    private RecordResProc saveDelete() {

        var strInput = getItemFromArrItemModf("id");
        if (strInput.isEmpty() || strInput.equalsIgnoreCase("n")) {
            return RecordResProc.getResultErr("Отказ от удаления сотрудника");
        }

        var resUpd = ((EmploeeDAO) getModelDAO()).delete(model.getId());
        if (!resUpd.res()) {
            return resUpd;
        }

        return new RecordResProc("Выполнено удаление сотрудника");
    }

    private RecordResProc saveInsert() {

        var emploee = new Emploee();

        String strInput = getItemFromArrItemModf("departmentsId");
        if (!strInput.isEmpty()) {
            var departmentsId = Integer.parseInt(strInput);
            if (!DepartmentDAO.isExistsItem(departmentsId)) {
                return RecordResProc.getResultErr("Нет такого подразделения");
            } else if (APIerror.getErr()) {
                return RecordResProc.getResultErr(APIerror.getMes());
            }

            emploee.setDepartmentsId(departmentsId);
        } else {
            return RecordResProc.getResultErr("Не выбрано подразделение");
        }

        strInput = getItemFromArrItemModf("positionId");
        if (!strInput.isEmpty()) {
            var inputPositionId = Integer.parseInt(strInput);

            // Проверка штатного расписания
            var verLimit = verfLimitPosition(inputPositionId);
            if (APIerror.getErr()) {
                return RecordResProc.getResultErr(APIerror.getMes());
            } else if (verLimit < 0) {
                return RecordResProc.getResultErr("Штатное расписание заполнено.");
            }

            var existsPositionIsOne =
                    PositionsDAO.isExistsPositionInDepartment(emploee.getDepartmentId(), inputPositionId);
            if (APIerror.getErr()){
                return RecordResProc.getResultErr(APIerror.getMes());
            }
            if (existsPositionIsOne){
                return RecordResProc.getResultErr("Есть сотрудник на этой должности (только один сотрудник)");
            }

            emploee.setPositionId(inputPositionId);
        } else {
            return RecordResProc.getResultErr("Нет данных по специальности");
        }

        strInput = getItemFromArrItemModf("fullName");
        if (!strInput.isEmpty()) {
            emploee.setFullName(strInput);
        } else {
            return RecordResProc.getResultErr("Не заполнено поле fullName");
        }

        return ((EmploeeDAO) getModelDAO()).create(emploee);
    }

    @Override
    public RecordResProc saveModel() {

        if (eModfModels == EModfModels.UPDATE) {
            return saveUpdate();
        } else if (eModfModels == EModfModels.DELETE) {
            return saveDelete();
        } else {
            return saveInsert();
        }

    }

}
