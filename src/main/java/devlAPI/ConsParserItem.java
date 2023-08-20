package devlAPI;

public class ConsParserItem {
    private String strPrint;
    private String strInput;
    private String strField;

    public ConsParserItem(String strPrint, String strField){
        this.strInput = "";
        this.strField = strField;
        this.strPrint = strPrint;
    }

    // ---------------------------------

    public String getStrField() {
        return strField;
    }

    public String getStrPrint() {
        return strPrint;
    }
    public void setStrPrint(String value){
        strPrint = value;
    }

    public String getStrInput() {
        return strInput;
    }

    public void setStrInput(String strInput) {
        this.strInput = strInput;
    }


}
