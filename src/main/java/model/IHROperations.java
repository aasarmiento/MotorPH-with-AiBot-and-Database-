package model;

public interface IHROperations {
    Employee viewEmployeeProfile(int empID);
    void approveLeaveRequest(String leaveID);
    void rejectLeaveRequest(String leaveID, String reason);
    void updateLeaveStatus(String leaveID, String newStatus); 
    void updateEmployeeDetails(int empID, Employee updatedData);
}