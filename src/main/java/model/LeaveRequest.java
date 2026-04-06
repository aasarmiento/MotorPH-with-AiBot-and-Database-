package model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class LeaveRequest {
    private String empID;
    private String lastName;
    private String firstName;
    private LocalDate startDate;
    private LocalDate endDate;
    private String leaveType;
    private String reason;
    private String status;

    private String requestId;
    private final LocalDate submittedDate;
    private LocalDate approvalDate;
    private String approvedBy;
    private double deductedDays;

    // 1. ADDED: No-argument constructor for the DAO/Database mapping
    public LeaveRequest() {
        this.submittedDate = LocalDate.now();
        this.status = "PENDING";
        this.reason = "N/A";
    }

    public LeaveRequest(int empNo, String lastName, String firstName, String leaveType, LocalDate startDate, LocalDate endDate, String reason) {
        this.empID = String.valueOf(empNo);
        this.lastName = lastName;
        this.firstName = firstName;
        this.leaveType = leaveType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.reason = (reason == null || reason.isEmpty()) ? "N/A" : reason;
        
        this.status = "PENDING";
        this.submittedDate = LocalDate.now();
        this.requestId = "LR-" + this.empID + "-" + System.currentTimeMillis();
        calculateDeductedDays();
    }

   
    public LeaveRequest(int empNo, String type, LocalDate start, LocalDate end) {
        this.empID = String.valueOf(empNo); 
        this.leaveType = type;              
        this.startDate = start;
        this.endDate = end;
        this.status = "Pending";
        this.reason = "N/A";
        this.lastName = "";
        this.firstName = "";
        this.submittedDate = LocalDate.now(); 
        calculateDeductedDays();
    }

    public final void calculateDeductedDays() {
        if (startDate != null && endDate != null) {
            this.deductedDays = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        } else {
            this.deductedDays = 0;
        }
    }

    // Getters and Setters
    public String getEmpID() { return empID; }
    public void setEmpID(String empID) { this.empID = empID; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { 
        this.startDate = startDate; 
        calculateDeductedDays(); // Recalculate if date is set manually
    }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { 
        this.endDate = endDate; 
        calculateDeductedDays(); // Recalculate if date is set manually
    }

    public String getLeaveType() { return leaveType; }
    public void setLeaveType(String leaveType) { this.leaveType = leaveType; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }

    public LocalDate getSubmittedDate() { return submittedDate; }
    public LocalDate getApprovalDate() { return approvalDate; }
    public void setApprovalDate(LocalDate approvalDate) { this.approvalDate = approvalDate; }
    public String getApprovedBy() { return approvedBy; }
    public void setApprovedBy(String approvedBy) { this.approvedBy = approvedBy; }
    public double getDeductedDays() { return deductedDays; }
}