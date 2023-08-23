package models;

public class Salaries {
    private int id;
    private int yymm;
    private int emploeesId;
    private String fullName;
    private int salary;

    public Salaries(int id, int yymm, int emploeeId, int salary){
        this.id = id;
        this.yymm = yymm;
        this.emploeesId = emploeeId;
        this.salary = salary;
    }

    public void setFullName(String fullName){
        this.fullName = fullName;
    }

    // -----------------

    public int getYymm(){
        return yymm;
    }

    @Override
    public String toString() {
        return String.format("%d yymm:%d emplId:%-3d %d",id, yymm, emploeesId, salary );
    }

    public String toStringExt() {
        return String.format("%d yymm:%d emplId:%-3d %6d руб. %s",id, yymm, emploeesId, salary, fullName );
    }

}
