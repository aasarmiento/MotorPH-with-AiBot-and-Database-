package model;
import java.util.List;



public interface ILeaveOperations {
    
    void applyLeave(LeaveRequest request);
    
    
    List<LeaveRequest> viewAllLeaveRequests();
    
   
}

