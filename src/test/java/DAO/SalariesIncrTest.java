package DAO;

import DevlInterface.IRunComd;
import devlAPI.APIerror;
import devlRecord.RecordResProc;
import models.SalariesDAO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
public class SalariesIncrTest {
    @Test
    public void incrSalaries_test(){
        var proc = 10;
        var resIncr = SalariesDAO.incrSalaries(proc);

        // assert
        Assertions.assertTrue(resIncr.res());
        Assertions.assertInstanceOf(IRunComd.class, resIncr.data());

        ((IRunComd) resIncr.data()).apply();
    }

}
