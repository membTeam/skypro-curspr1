package DAO;


import devlAPI.APIerror;
import models.Statistics;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import static devlAPI.APIprintService.println;
import models.SalariesDAO;

public class StatiticsTest {

    @Test
    public void getStatistics_test(){

        var stat = new Statistics(2308);

        // assert
        Assertions.assertFalse(APIerror.getErr());
        Assertions.assertNotNull(stat);

        println(stat.toString());

    }

}
