package service;

import model.Employee;
import model.PayrollBreakdown;

public class DolePolicy {

    public PayrollBreakdown compute(Employee emp, double hoursWorked) {
        return compute(emp, hoursWorked, 0.0);
    }

    public PayrollBreakdown compute(Employee emp, double hoursWorked, double lateDeduction) {
        if (emp == null || hoursWorked <= 0) {
            return new PayrollBreakdown(
                0.0, 0.0, 0.0, 0.0, 0.0,
                0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0
            );
        }

        double earnedBasicPay = round2(hoursWorked * emp.getHourlyRate());

        // Keep current allowance behavior for now.
        // This already fixes the zero-days-paid bug because no hours = all zero.
        double allowances = emp.getRiceSubsidy()
                + emp.getPhoneAllowance()
                + emp.getClothingAllowance();

        double grossPay = round2(Math.max(0.0, earnedBasicPay + allowances - lateDeduction));

        double sss = round2(getSSSDeduction(grossPay));
        double ph = round2(getPhilHealthDeduction(grossPay));
        double pi = round2(getPagIBIGDeduction(grossPay));

        double totalStatutory = sss + ph + pi;
        double taxableIncome = round2(Math.max(0.0, grossPay - totalStatutory));

        double tax = round2(getWithholdingTax(taxableIncome));
        double netPay = round2(Math.max(0.0, grossPay - totalStatutory - tax));

        return new PayrollBreakdown(
            hoursWorked,
            earnedBasicPay,
            allowances,
            0.0,
            0.0,
            grossPay,
            taxableIncome,
            sss,
            ph,
            pi,
            tax,
            netPay
        );
    }

    private static double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    public static double getSSSDeduction(double basicSalary) {
        if (basicSalary >= 24750) return 1125.00;
        if (basicSalary < 3250) return 135.00;

        double factor = Math.floor((basicSalary - 3250) / 500);
        return 157.50 + (factor * 22.50);
    }

    public static double getPhilHealthDeduction(double basicSalary) {
        double salaryCap = Math.min(Math.max(basicSalary, 10000.0), 60000.0);
        double totalPremium = salaryCap * 0.03;
        return totalPremium / 2.0;
    }

    public static double getPagIBIGDeduction(double basicSalary) {
        return (basicSalary > 1500) ? 100.00 : 0.0;
    }

    public static double getWithholdingTax(double taxableIncome) {
        if (taxableIncome <= 20832) return 0.0;
        if (taxableIncome < 33333) return (taxableIncome - 20833) * 0.20;
        if (taxableIncome < 66667) return 2500 + (taxableIncome - 33333) * 0.25;
        if (taxableIncome < 166667) return 10833 + (taxableIncome - 66667) * 0.30;
        if (taxableIncome < 666667) return 40833.33 + (taxableIncome - 166667) * 0.32;
        return 200833.33 + (taxableIncome - 666667) * 0.35;
    }
}