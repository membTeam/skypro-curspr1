package models;

public class Department {
    private final int id;
    private String info;
    private String arrPosition;

    @Override
    public String toString(){
        return  "id:" + id + " " + info;
    }
    public Department(int id, String info){
        this.id = id;
        this.info = info;
    }

    public String getArrPosition(){
        return arrPosition;
    }

    public int getId() {
        return id;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

}
