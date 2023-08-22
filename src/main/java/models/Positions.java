package models;


/**
 * Структура Position создается один раз при начальном заполнении БД
 */
public class Positions {
    private int id;
    private String jobTitle;
    private int salary;     // только для Администратор
    private int numLimit;   // только для Администратор
    private boolean onlyOne;

    public Positions(int id, String jobTitle, int salary, int numLimit){
        this.id = id;
        this.jobTitle = jobTitle;
        this.salary = salary;
        this.numLimit = numLimit;
    }

    @Override
    public String toString(){
        var strFormat = String.format("id:%2d %6d \t limit:%2d %s", id, salary, numLimit, jobTitle );
        return strFormat.toString();
    }

    public boolean setOnlyOne(int value){
        return this.onlyOne = value > 0;
    }
    public boolean getOnlyOne(){
        return onlyOne;
    }

    public int getNumLimit() {
        return numLimit;
    }

    public void setNumLimit(int numLimit) {
        this.numLimit = numLimit;
    }

    public int getId() {
        return id;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public int getSalary() {
        return salary;
    }

    public void setSalary(int salary) {
        this.salary = salary;
    }
}
