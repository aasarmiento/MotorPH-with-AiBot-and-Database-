package model;

import java.io.File;
import java.util.List;

public interface IFinanceOperations {
    
    public void batchProcessPayroll();

    public Payslip generatePayslip(String empNo, String payPeriod);

    public File generateTaxReport(String quarter);

   
    public List<Payslip> generateDeductionSummary(String month);
}