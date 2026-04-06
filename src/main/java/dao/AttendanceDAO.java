package dao;
import java.util.List; 
import java.util.Map;

import model.Attendance;

public interface AttendanceDAO {
    
    List<Attendance> getAttendanceByEmployee(int empNo);

    List<model.LeaveRequest> findLeavesByEmployeeId(int empNo);
    
   
    Object[][] getAttendanceByMonth(int empNo, String month, String year);
    
   
String getLastStatus(int empId);

Map<String, Integer> countWorkingDaysPerMonth();
    
    public void recordAttendance(int empNo, String lastName, String firstName, String type);
}