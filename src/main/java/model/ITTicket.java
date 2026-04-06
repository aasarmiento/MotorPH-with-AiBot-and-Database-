package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ITTicket {
    private static final DateTimeFormatter TIMESTAMP_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private String ticketId;
    private int employeeNo;
    private String username;
    private String fullName;
    private String issueType;
    private String description;
    private String status;
    private String createdAt;
    private String resolvedAt;
    private String resolvedBy;

    public ITTicket(String ticketId, int employeeNo, String username, String fullName,
                    String issueType, String description) {
        this.ticketId = ticketId;
        this.employeeNo = employeeNo;
        this.username = username;
        this.fullName = fullName;
        this.issueType = issueType;
        this.description = description;
        this.status = "OPEN";
        this.createdAt = LocalDateTime.now().format(TIMESTAMP_FORMAT);
        this.resolvedAt = "";
        this.resolvedBy = "";
    }

    public ITTicket(String ticketId, int employeeNo, String username, String fullName,
                    String issueType, String description, String status,
                    String createdAt, String resolvedAt, String resolvedBy) {
        this.ticketId = ticketId;
        this.employeeNo = employeeNo;
        this.username = username;
        this.fullName = fullName;
        this.issueType = issueType;
        this.description = description;
        this.status = status;
        this.createdAt = createdAt;
        this.resolvedAt = resolvedAt;
        this.resolvedBy = resolvedBy;
    }

    public void markResolved(String resolverName) {
        this.status = "RESOLVED";
        this.resolvedBy = resolverName;
        this.resolvedAt = LocalDateTime.now().format(TIMESTAMP_FORMAT);
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setResolvedBy(String resolvedBy) {
        this.resolvedBy = resolvedBy;
    }

    public void setResolvedAt(String resolvedAt) {
        this.resolvedAt = resolvedAt;
    }

    public String getTicketId() { return ticketId; }
    public int getEmployeeNo() { return employeeNo; }
    public String getUsername() { return username; }
    public String getFullName() { return fullName; }
    public String getIssueType() { return issueType; }
    public String getDescription() { return description; }
    public String getStatus() { return status; }
    public String getCreatedAt() { return createdAt; }
    public String getResolvedAt() { return resolvedAt; }
    public String getResolvedBy() { return resolvedBy; }

    public void setTicketId(String ticketId) { this.ticketId = ticketId; }
    public void setEmployeeNo(int employeeNo) { this.employeeNo = employeeNo; }
    public void setUsername(String username) { this.username = username; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setIssueType(String issueType) { this.issueType = issueType; }
    public void setDescription(String description) { this.description = description; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
   
    public String toCSV() {
        return String.format("%s,%d,\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"",
            ticketId, employeeNo, username, fullName, 
            issueType, description, status, createdAt, 
            resolvedAt, resolvedBy);
    } 
}