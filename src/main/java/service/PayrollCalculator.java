package service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import model.Employee;
import model.PayrollBreakdown;
import model.PeriodSummary;

public class PayrollCalculator {

    private final DolePolicy policy;
    private static final LocalTime SHIFT_START = LocalTime.of(8, 0);
    private static final int GRACE_MINUTES = 10;
    private static final LocalTime LATE_CUTOFF = SHIFT_START.plusMinutes(GRACE_MINUTES);
    private static final DateTimeFormatter UI_DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    public PayrollCalculator() {
        this.policy = new DolePolicy();
    }

    public double getSSSDeduction(double monthlySalary) {
        if (monthlySalary >= 24750) return 1125.00;
        if (monthlySalary < 3250) return 135.00;
        double factor = Math.floor((monthlySalary - 3250) / 500);
        return round2(157.50 + (factor * 22.50));
    }

    public double getPhilHealthDeduction(double monthlySalary) {
        double salaryCap = Math.min(Math.max(monthlySalary, 10000.0), 60000.0);
        return round2((salaryCap * 0.03) / 2.0);
    }

    public double getPagIBIGDeduction(double monthlySalary) {
        return (monthlySalary > 1500) ? 100.00 : 0.0;
    }

    public double getWithholdingTax(double taxableIncome) {
        if (taxableIncome <= 20832) return 0.0;
        if (taxableIncome < 33333) return round2((taxableIncome - 20833) * 0.20);
        if (taxableIncome < 66667) return round2(2500 + (taxableIncome - 33333) * 0.25);
        if (taxableIncome < 166667) return round2(10833 + (taxableIncome - 66667) * 0.30);
        if (taxableIncome < 666667) return round2(40833.33 + (taxableIncome - 166667) * 0.32);
        return round2(200833.33 + (taxableIncome - 666667) * 0.35);
    }

    public PayrollBreakdown calculateBreakdown(Employee emp, double hoursWorked) {
        return policy.compute(emp, hoursWorked);
    }

    public double calculateGrossIncome(Employee emp, double hoursWorked) {
        PayrollBreakdown breakdown = calculateBreakdown(emp, hoursWorked);
        return breakdown != null ? breakdown.getGrossPay() : 0.0;
    }

    public PeriodSummary calculateFullSummary(Employee emp, double hoursWorked) {
        if (emp == null) {
            return new PeriodSummary(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
        }

        PayrollBreakdown breakdown = calculateBreakdown(emp, hoursWorked);
        
        if (breakdown == null) {
            return new PeriodSummary(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
        }

        return new PeriodSummary(
                breakdown.getGrossPay(),
                breakdown.getSss(),
                breakdown.getPhilhealth(),
                breakdown.getPagibig(),
                breakdown.getWithholdingTax(),
                breakdown.getNetPay()
        );
    }

    public int calculateLateMinutes(Object[][] rawLogs) {
        if (rawLogs == null || rawLogs.length == 0) return 0;

        Map<LocalDate, LocalTime> firstLogInPerDay = new HashMap<>();

        for (Object[] row : rawLogs) {
            if (row == null || row.length < 2 || row[0] == null || row[1] == null) continue;

            try {
                LocalDate date = LocalDate.parse(row[0].toString().trim(), UI_DATE_FORMAT);
                String timeStr = row[1].toString().trim();
                
                if (timeStr.isEmpty() || "N/A".equalsIgnoreCase(timeStr)) continue;
                
                LocalTime logIn = LocalTime.parse(timeStr, TIME_FORMAT);

                firstLogInPerDay.merge(date, logIn, (oldVal, newVal) -> newVal.isBefore(oldVal) ? newVal : oldVal);
            } catch (Exception ignored) {
            }
        }

        int totalLateMinutes = 0;
        for (LocalTime firstLogIn : firstLogInPerDay.values()) {
            if (firstLogIn.isAfter(LATE_CUTOFF)) {
                totalLateMinutes += (int) Duration.between(SHIFT_START, firstLogIn).toMinutes();
            }
        }
        return totalLateMinutes;
    }

    private double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}