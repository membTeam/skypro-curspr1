package devlRecord;

/**
 * return ok -> new RecordResProcExt(string or int or StringAndInt)
 * @param res
 * @param mes
 * @param strData
 * @param intData
 */
public record RecordResProcExt(boolean res, String mes, String strData, int intData) {

    public RecordResProcExt(String dataStr){
        this(true,"ok", dataStr, 0);
    }

    public RecordResProcExt(int dataInt){
        this(true,"ok", "", dataInt);
    }

    public RecordResProcExt(String dataStr, int dataInt ){
        this(true,"ok", dataStr, dataInt);
    }

    public static RecordResProcExt getExtResultErr(String mes){
        return new RecordResProcExt(false, mes, "err", -1);
    }

}
