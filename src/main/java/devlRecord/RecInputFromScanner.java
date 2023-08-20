package devlRecord;

public record RecInputFromScanner(boolean res, String mes) {

    public RecInputFromScanner(String mes){
        this(false, mes);
    }

}
