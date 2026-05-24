package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

import model.PayrollBreakdown;
import model.PeriodSummary;

public class PayrollDAOImpl {

   public PayrollBreakdown getPayrollBreakdown(int empId, LocalDate start, LocalDate end) {
    String sql = "SELECT * FROM public.payroll WHERE employee_id = ? AND pay_period_start = ? AND pay_period_end = ?";

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        
        pstmt.setInt(1, empId);
        pstmt.setDate(2, java.sql.Date.valueOf(start));
        pstmt.setDate(3, java.sql.Date.valueOf(end));

        try (ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                double gross = rs.getDouble("gross_salary");
                double net = rs.getDouble("net_pay");
                double deductions = rs.getDouble("total_deductions");
                
              return new PayrollBreakdown(
    160.0,                  
    rs.getDouble("gross_salary"), 
    3500.0,                
    0.0, 
    3500.0, 
    rs.getDouble("gross_salary"), 
    rs.getDouble("gross_salary"),
    135.0,                  
    150.0,                  
    100.0,                 
    0.0, 
    rs.getDouble("net_pay") 
);
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return null;
}

    
    public PeriodSummary getPeriodSummary(int empId, LocalDate start, LocalDate end) {
        PayrollBreakdown detail = getPayrollBreakdown(empId, start, end);
        if (detail == null) return null;

        return new PeriodSummary(
            detail.getGrossPay(),
            detail.getSss(),
            detail.getPhilhealth(),
            detail.getPagibig(),
            detail.getWithholdingTax(),
            detail.getNetPay()
        );
    }
}
