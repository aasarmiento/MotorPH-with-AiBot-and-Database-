package model;


public interface ILeaveOperationsSuperior {
    
    void updateLeaveStatus(LeaveRequest request, String newStatus);
}