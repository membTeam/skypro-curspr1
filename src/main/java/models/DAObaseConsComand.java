package models;


import devlAPI.APIerror;
import devlAPI.ConsParserItem;
import devlAPI.enumType.EModfModels;
import devlRecord.RecordResProc;

/**
 * Базовый класс для DAOEmploeeConsComd и других DAO***ConsComd
 *
 * @param <T>
 */
public abstract class DAObaseConsComand<T extends DAOabstract> {
    private ConsParserItem[] arrConsParserItemModf;
    private int numPositionArr = 0;
    private int idModel;
    private Object modelItem;
    private T modelDAO;
    private EModfModels eModfModels;

    public DAObaseConsComand(T obj, int id, EModfModels eModfModels) {
        modelDAO = obj;
        idModel = id;
        this.eModfModels = eModfModels;
    }


    // ---------------------------------------------------------
    public abstract boolean isExistsModel();

    /**
     * for DAOEmploeeConsComd, DAOPositionConsComd, DAODepartmentConsComd
     */
    public T getModelDAO() {
        return modelDAO;
    }


    /**
     * Заполняется из дочерних объектов
     *
     * @param arr
     */
    public void setArrConsParserItemModf(ConsParserItem[] arr) {
        arrConsParserItemModf = arr;
    }

    public String nextStr(String strInput) {
        String res = "";

        if (arrConsParserItemModf == null){
            APIerror.setError("Структура диалога не инициализирована");
            return res;
        }

        // сохранить введенную строку
        if (!strInput.isEmpty()) {
            arrConsParserItemModf[numPositionArr - 1].setStrInput(strInput);
        }

        if (numPositionArr < arrConsParserItemModf.length) {
            if (arrConsParserItemModf[numPositionArr].getStrField().equals("positionId")) {

                var strPrint = "";
                if (this instanceof DAOEmploeeConsComd){
                    strPrint = ((DAOEmploeeConsComd)this).getStrPosition(arrConsParserItemModf);

                    if (strPrint.isEmpty()) {
                        return res;
                    }
                }

                arrConsParserItemModf[numPositionArr].setStrPrint(strPrint);
            }

            res = arrConsParserItemModf[numPositionArr++].getStrPrint();
        }

        return res;
    }


    /**
     * Используется из дочерних объектов
     *
     * @param item
     * @return
     */
    public String getItemFromArrItemModf(String item) {
        for (var i = 0; i < arrConsParserItemModf.length; i++) {
            if (arrConsParserItemModf[i].getStrField().equalsIgnoreCase(item)) {
                return arrConsParserItemModf[i].getStrInput();
            }
        }

        APIerror.setError("Значение не найдено");
        return "";
    }

    public abstract RecordResProc saveModel();

}
