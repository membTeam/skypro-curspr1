package models;

import devlRecord.RecordResProc;
import devlRecord.RecordResProcExt;

import java.sql.Connection;

import static devlAPI.APIprintService.println;

public class Emploee {
    private int id;
    private String fullName;
    private int departmentsId;
    private int positionId;
    private boolean idUse;

    // ------------------------------------

    public Emploee(){}
    public Emploee(int id, String fullName, int depatment, int position) {

        this.id = id;
        this.fullName = fullName;
        this.departmentsId = depatment;
        this.positionId = position;
        this.idUse = true;
    }

    public int getDepartmentId(){
        return departmentsId;
    }
    public int getPositionId() {
        return positionId;
    }

    public void setPositionId(int positionId) {
        this.positionId = positionId;
    }

    public boolean getIdUse() {
        return idUse;
    }

    public void setIdUse(boolean idUse) {
        this.idUse = idUse;
    }

    public String toString(Connection conn){

        var strDepartment = getDepartment(conn);
        var salary = getSalary(conn);
        return "id:" + id
                + " depatmentId:" + departmentsId
                + " " + strDepartment
                + " " + getPosition(conn)
                + " " + salary + "руб."
                + " " + fullName;
    }

    @Override
    public String toString() {
        return "id:" + id
                + " отдел:" + getDepartment(null)
                + " " + getPosition(null)
                + " зарплата:" + getSalary(null)
                + " " + fullName;
    }

    public int getId() {
        return id;
    }
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public int getDepartmentsId() {
        return departmentsId;
    }

    public void setDepartmentsId(int departmentsId) {
        this.departmentsId = departmentsId;
    }

    public String getDepartment(Connection conn) {
        RecordResProcExt resExt = DepartmentDAO.getInfo(conn, departmentsId);
        if (resExt.res()){
            return resExt.strData();
        } else {
            println(resExt.mes());
            return "err from getDepartment\n" + resExt.mes();
        }
    }

    public int getSalary(Connection conn) {
        RecordResProcExt resExt = PositionsDAO.getSalary(conn, positionId);

        if (resExt.res()){
            return resExt.intData();
        } else {
            println("error from getSalary:\n" +  resExt.mes());
            return  -1;
        }
    }

    public String getPosition(Connection conn){
        var resPos = PositionsDAO.getJobTitle(conn, positionId);
        if (!resPos.res()){
            return  "empty";
        } else {
            return resPos.strData();
        }
    }

}
