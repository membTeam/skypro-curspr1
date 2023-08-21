package models;

public class Salaries {
    private int id;
    private int yymm;
    private int emploeesId;
    private int salary;

    private Salaries[] arrSalaries = null;

    public Salaries[] getArrSalaries(){
        if (arrSalaries == null){
            arrSalaries = SalariesDAO.getArrSalaries(yymm);
        }

        return arrSalaries;
    }
    public int getId() {
        return id;
    }

    public int getYymm() {
        return yymm;
    }

    public void setYymm(int yymm) {
        this.yymm = yymm;
    }

    public int getEmploeesId() {
        return emploeesId;
    }

    public void setEmploeesId(int emploeesId) {
        this.emploeesId = emploeesId;
    }

    public int getSalary() {
        return salary;
    }

    public void setSalary(int salary) {
        this.salary = salary;
    }
}
