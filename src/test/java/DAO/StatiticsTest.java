package DAO;


import devlAPI.APIerror;
import models.SalariesDAO;
import models.Statistics;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class StatiticsTest {

    @Test
    public void getStatistics_test(){

        var yymm = 2307;
        var stat = new SalariesDAO();

        var statistics = new Statistics(yymm);
        statistics.printEntity();

        // assert
        Assertions.assertFalse(APIerror.getErr());
        Assertions.assertNotNull(stat);
        Assertions.assertInstanceOf(SalariesDAO.class, stat);
        Assertions.assertNotNull(statistics);

    }

}
