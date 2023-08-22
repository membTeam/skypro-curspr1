package DAO;
import devlAPI.APIerror;
import models.EmploeeDAO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
public class SalareesDAOTest {

    @Test
    public void getAllEmploee_test(){

    }

    @Test
    public void printAllEmploee_test(){
        EmploeeDAO.printAllEmploee();

        Assertions.assertFalse(APIerror.getErr());
    }



}
