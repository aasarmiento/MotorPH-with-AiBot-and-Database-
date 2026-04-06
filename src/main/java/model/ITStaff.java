package model;

import java.time.LocalDate;


public class ITStaff extends Employee implements ISystemOperations {

    // --- CONSTRUCTORS ---
  
    public ITStaff() {
        super();
    }

 public ITStaff(int id, String last, String first, LocalDate bday, double basic, String gender) {
    super(id, last, first, bday); 
    this.setBasicSalary(basic); 
    this.setGender(gender);
    this.setRole(Role.IT_STAFF); 
}

    
public ITStaff(int empNo, String lastName, String firstName, LocalDate birthday, 
             String address, String phone, String sss, String philhealth, 
             String tin, String pagibig, String status, String position, 
             String supervisor, double basicSalary, double riceSubsidy, 
             double phoneAllowance, double clothingAllowance, double grossRate, 
             double hourlyRate, Role role, String gender) { // <--- ADD String gender HERE
    
    // Pass gender to the super constructor
    super(empNo, lastName, firstName, birthday, address, phone, sss, 
          philhealth, tin, pagibig, status, position, supervisor, 
          basicSalary, riceSubsidy, phoneAllowance, clothingAllowance, 
          grossRate, hourlyRate, role, gender); 
}
    @Override
    public Role getRole() {
        return Role.IT_STAFF; 
    }

    // IT requires stronger security (12 chars)
    @Override
    public boolean isPasswordValid(String pass) {
        return pass != null && pass.length() >= 12; 
    }

    @Override
    public double calculateGrossSalary() {
        double technicalPremium = 5000.00; 
        // Inherited base gross + IT bonus
        return super.calculateGrossSalary() + technicalPremium;
    }

    @Override
    public LeaveRequest applyLeave(String type, LocalDate start, LocalDate end) {
        LeaveRequest request = new LeaveRequest(this.getEmpNo(), type, start, end);
        System.out.println("IT Dept: Checking server maintenance schedule for these dates...");
        super.applyLeave(request); 
        return request;
    }

    // --- ISystemOperations Implementation ---
   
    public boolean canApproveTechnicalTickets() {
        return true; 
    }

    @Override
    public String checkSystemHealth() {
        return "System Status: Online";
    }

    @Override
    public boolean resetCredentials(String empID, String adminToken) {
        return "MASTER_TOKEN_2026".equals(adminToken);
    }

   

    // --- IPayrollCalculations Implementation ---

    @Override 
    public double calculateTotalHoursWorked() { 
        return 160.0; 
    }

    @Override
    public double calculateSahod() {
        // IT Staff get a 10% performance multiplier may pabonus pg IT 
        return calculateNetPay() * 1.10; 
    }

    @Override
    public double calculateSalary() {
        return calculateSahod();
    }

    @Override
    public double computeDeductions() { 
        return calculateSSSDeduction() + calculatePhilHealth() + calculatePagIBIG() + calculateWithholdingTax(); 
    }

    @Override 
    public double computeNet() { 
        return calculateNetPay(); 
    }

    @Override 
    public double calculateNetPay() { 
        return calculateGrossSalary() - (calculateSSSDeduction() + calculatePhilHealth() + calculatePagIBIG() + calculateWithholdingTax()); 
    }

   
}
