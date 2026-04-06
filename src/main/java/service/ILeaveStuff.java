package service;
import model.Employee;

public interface ILeaveStuff {

    boolean processLeave(Employee emp, String leaveType, String startDate, String endDate);
}
