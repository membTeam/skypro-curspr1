package models;


/**
 * Структура Position создается один раз при начальном заполнении БД
 */
public class Positions {
    private final int id;
    private final String jobTitle;
    private int salary;
    private final int numLimit;

    public Positions(int id, String jobTitle, int salary, int numLimit){
        this.id = id;
        this.jobTitle = jobTitle;
        this.salary = salary;
        this.numLimit = numLimit;
    }

    @Override
    public String toString(){
        return String.format("id:%2d %6d \t limit:%2d %s", id, salary, numLimit, jobTitle );
    }

    public int getId() {
        return id;
    }

    public int getSalary() {
        return salary;
    }

    public void setSalary(int salary) {
        this.salary = salary;
    }
}
