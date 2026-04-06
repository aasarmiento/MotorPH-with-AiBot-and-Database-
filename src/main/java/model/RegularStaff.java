package model;

import java.time.LocalDate;

public class RegularStaff extends Employee {

    
    public RegularStaff() {
        super();
    }

    public RegularStaff(int id, String last, String first, LocalDate bday, double basic) {
        super(id, last, first, bday);
        this.basicSalary = basic;
        this.setRole(Role.REGULAR_STAFF); 
    }

    
public RegularStaff(int empNo, String lastName, String firstName, LocalDate birthday, 
             String address, String phone, String sss, String philhealth, 
             String tin, String pagibig, String status, String position, 
             String supervisor, double basicSalary, double riceSubsidy, 
             double phoneAllowance, double clothingAllowance, double grossRate, 
             double hourlyRate, Role role, String gender) { // <--- ADD String gender HERE
    
    super(empNo, lastName, firstName, birthday, address, phone, sss, 
          philhealth, tin, pagibig, status, position, supervisor, 
          basicSalary, riceSubsidy, phoneAllowance, clothingAllowance, 
          grossRate, hourlyRate, role, gender); 
}


    @Override
    public double calculateTotalHoursWorked() {
        return 160.0; 
    }

    @Override
    public double calculateGrossSalary() {
        double basePay = this.hourlyRate * calculateTotalHoursWorked();

      
        if ("Probationary".equalsIgnoreCase(this.getStatus())) {
            return basePay;
        } 
        
        return basePay + this.riceSubsidy + this.phoneAllowance + this.clothingAllowance;
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
    public Role getRole() {
        return Role.REGULAR_STAFF; 
    }

    @Override
    public boolean isPasswordValid(String pass) {
        return pass != null && pass.length() >= 8;
    }

    
    @Override
    public LeaveRequest applyLeave(String type, LocalDate start, LocalDate end) {
        LeaveRequest request = new LeaveRequest(this.getEmpNo(), type, start, end);
        
        this.leaveRequests.add(request);
        
        System.out.println("Regular Staff leave request submitted for: " + this.getLastName());
        return request;
    }


         
 
}