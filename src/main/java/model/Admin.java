package model;

import java.time.LocalDate;

// child ito ni employee ang special ability nya IS MAKIKITA YA LAHAT tapos kaya nya mag approve so kailangan ung create employee na gagamitin natin sa service may parameter naa iadmin and Ileavesuperior 
public class Admin extends Employee implements IAdminOperations, ILeaveOperationsSuperior {

    public Admin() {
        super();
    }

    // Constructor 
public Admin(int empNo, String lastName, String firstName, LocalDate birthday, 
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



public Admin(int id, String last, String first, LocalDate bday, double basic, String gender) {
    
    super(id, last, first, bday); 
    
    
    this.setBasicSalary(basic); 
    this.setGender(gender);
    this.setRole(Role.ADMIN); //<-- kita nyo guysimportante yan 
}
    
// tignan nyo to inoverride ung calculatesahod tas nag implement sya ng sakanya pero ung method dun kuha na nya pag tanong kayo pagkaiba ngcalcualte sahod sa calcualte slary ung calculate sahod daw para sa cashier sa calculate salary naman ung machine na ginamit  
    @Override
    public double calculateSahod() {
        double managementAllowance = 5000.0;
        return calculateNetPay() + managementAllowance;
    } 
    

    // ito ung sa interface na Icalculate para makapagcalculate mga admin
    @Override public double calculateTotalHoursWorked() { return 160.0; }
    @Override public double computeNet() { return calculateNetPay(); }
    @Override public double calculateSalary() { return calculateSahod(); }
    @Override public Role getRole() { return Role.ADMIN; }

    // ito ung special ability NYA dapat ipapass dto sa service and dao dito magcoconnect paputnag ui 
    @Override public void createEmployee(Employee emp) { System.out.println("Admin creating ID: " + emp.getEmpNo()); }
    @Override public int generateNextEmpNo() { return 0; }
    @Override public boolean isEmployeeValid(Employee emp) { return emp != null; }
    @Override public void updateEmployee(int empNo) { }
    @Override public void removeEmployee(int empNo) {System.out.println("Admin " + this.getFirstName() + " is authorizing deletion of " + empNo); }

    // for leave approval ito admin and hr lang pwede 
    @Override
    public void updateLeaveStatus(LeaveRequest request, String newStatus) {
        request.setStatus(newStatus);
    }

    @Override
    public LeaveRequest applyLeave(String type, LocalDate start, LocalDate end) {
        LeaveRequest request = new LeaveRequest(this.getEmpNo(), type, start, end);
        super.applyLeave(request);
        return request;
    }

   @Override
    public double calculatePagIBIG() {
        return calc.getPagIBIGDeduction(this.getBasicSalary());
    }

    
}