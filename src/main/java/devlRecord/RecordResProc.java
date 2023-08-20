package devlRecord;

/**
 * return ok -> RecordResProc(Object data)
 * @param res
 * @param mes
 * @param data
 */
public record RecordResProc(boolean res, String mes, Object data){

    public RecordResProc(){
        this(true,"ok",null);
    }

    public RecordResProc(String mes){ this(true,mes,null); }

    public RecordResProc(Object data){
        this(true, "ok", data);
    }

    public static RecordResProc getResultErr(String err){
        return new RecordResProc(false, err, null);
    }

}
