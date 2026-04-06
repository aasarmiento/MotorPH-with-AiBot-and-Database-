package model;

import java.io.File;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

public class AccountingStaff extends Employee implements IFinanceOperations {

    public AccountingStaff() {
        super();
    }
    
    public AccountingStaff(int empNo, String lastName, String firstName, LocalDate birthday, 
             String address, String phone, String sss, String philhealth, 
             String tin, String pagibig, String status, String position, 
             String supervisor, double basicSalary, double riceSubsidy, 
             double phoneAllowance, double clothingAllowance, double grossRate, 
             double hourlyRate, Role role, String gender) {  
    
        super(empNo, lastName, firstName, birthday, address, phone, sss, 
              philhealth, tin, pagibig, status, position, supervisor, 
              basicSalary, riceSubsidy, phoneAllowance, clothingAllowance, 
              grossRate, hourlyRate, role, gender); 
    }

    public AccountingStaff(int id, String last, String first, LocalDate bday, double basic, String gender) {
        super(id, last, first, bday); 
        this.setBasicSalary(basic); 
        this.setGender(gender);
        this.setRole(Role.ACCOUNTING); 
    }

    @Override
    public double calculateTotalHoursWorked() {
        return 160.0; 
    }

    @Override
    public double calculateSalary() {
        return calculateSahod(); 
    }

    @Override
    public double calculateSahod() {
        return calculateNetPay();
    }

    @Override
    public double computeNet() {
        return calculateNetPay();
    }


    @Override
    public void batchProcessPayroll() {
        System.out.println("Accounting: Triggering batch calculation for current pay period.");
    }

    @Override 
    public Payslip generatePayslip(String empNo, String payPeriod) { 
        return new Payslip(Integer.parseInt(empNo), this.getFirstName(), payPeriod, "2024", null); 
    }

    @Override 
    public File generateTaxReport(String quarter) { 
        return null; 
    }

   
    @Override 
    public List<Payslip> generateDeductionSummary(String month) { 
        return new ArrayList<>(); 
    }

    @Override
    public LeaveRequest applyLeave(String type, LocalDate start, LocalDate end) {
        LeaveRequest newRequest = new LeaveRequest(this.getEmpNo(), type, start, end);
        this.leaveRequests.add(newRequest);
        return newRequest;
    }

    @Override 
    public Role getRole() { 
        return Role.ACCOUNTING; 
    }

    @Override 
    public boolean isPasswordValid(String pass) { 
        return pass != null && pass.length() >= 8; 
    }

    @Override public double calculateSSSDeduction() { return super.calculateSSSDeduction(); }
    @Override public double calculatePhilHealth() { return super.calculatePhilHealth(); }
    @Override public double calculatePagIBIG() { return super.calculatePagIBIG(); }
    @Override public double calculateWithholdingTax() { return super.calculateWithholdingTax(); }

}