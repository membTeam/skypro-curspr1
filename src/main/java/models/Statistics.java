package models;

import devlAPI.APIerror;

import static devlAPI.APIprintService.println;

public class Statistics {
    private int yymm;
    private int sumSalaries;
    private double avgSalaries;
    private int maxSalaries;
    private int minSalaries;
    private String emploeeMax;
    private String emploeeMin;

    public Statistics(int yymm){
        var res = SalariesDAO.getStatistics(yymm);

        if (res == null || APIerror.getErr()){
            return;
        }

        var emplMax = "Нет данных";
        var emplMin = "Нет данных";
        if (res.emplMax() != null){
            emplMax = res.emplMax();
        }
        if (res.emplMin() != null){
            emplMin = res.emplMin();
        }

        this.yymm = yymm;
        avgSalaries = res.avg();
        sumSalaries = res.sumSal();
        maxSalaries = res.maxSalr();
        minSalaries = res.minSalr();
        emploeeMax = emplMax;
        emploeeMin = emplMin;

    }

    public void printEntity(){
        println(this.toString());
    }

    @Override
    public String toString(){
        return String.format("""
                                Расчетный период yymm:%d
                                \tСреднНачисл:%.3f ОбщСумм:%6d
                                \tМаксЗарпл:%6d МинЗарпл%6d
                                \tСотрудник с МаксНичисл %s
                                \tСотрудник с МинНачисл  %s
                                """,
                yymm,
                avgSalaries,
                sumSalaries,
                maxSalaries,
                minSalaries,
                emploeeMax,
                emploeeMin
                );
    }

}
