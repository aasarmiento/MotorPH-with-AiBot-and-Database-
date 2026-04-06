package service;

import model.Employee;
import model.PayrollBreakdown;

public interface PayrollPolicy {
    PayrollBreakdown compute(Employee employee, double hoursWorked);
}