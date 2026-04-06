
package model;



public interface IPayrollCalculations {
    public abstract double calculateTotalHoursWorked();
    public abstract double calculateGrossSalary();
    public abstract double calculateSSSDeduction();
    public abstract double calculatePhilHealth();
    public abstract double calculatePagIBIG();
    public abstract double calculateWithholdingTax();
    public abstract double calculateNetPay();
     public abstract double calculateSalary();
    public abstract double computeDeductions();
    public abstract double computeNet();
    

    
    
}
    