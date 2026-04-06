package model;

import java.time.LocalDate;

public class Payslip {
    // 1. Identity Data
    private int employeeId;
    private String fullName;
    private String periodMonth;
    private String periodYear;
    private LocalDate dateGenerated;

    private PayrollBreakdown details; 

    public Payslip(int empId, String name, String month, String year, PayrollBreakdown details) {
        this.employeeId = empId;
        this.fullName = name;
        this.periodMonth = month;
        this.periodYear = year;
        this.details = details;
        this.dateGenerated = LocalDate.now();
    }

    public int getEmployeeId() { return employeeId; }
    public String getFullName() { return fullName; }
    public String getPeriodMonth() { return periodMonth; }
    public String getPeriodYear() { return periodYear; }
    public LocalDate getDateGenerated() { return dateGenerated; }

    
    public double getNetPay() { return details.getNetPay(); }
    public double getGrossPay() { return details.getGrossPay(); }
    public double getSss() { return details.getSss(); }
    public double getPhilhealth() { return details.getPhilhealth(); }
    public double getPagibig() { return details.getPagibig(); }
    public double getTax() { return details.getWithholdingTax(); }
    public double getBasic() { return details.getEarnedBasicPay(); }
    
    public PayrollBreakdown getDetails() { return details; }
}