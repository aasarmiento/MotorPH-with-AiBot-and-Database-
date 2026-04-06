package model;

public final class PayrollBreakdown {
    private final double hoursWorked;
    private final double earnedBasicPay;
    private final double totalAllowances;
    private final double taxableAllowances;
    private final double nonTaxableAllowances;
    private final double grossPay;
    private final double taxableIncome;
    private final double sss;
    private final double philhealth;
    private final double pagibig;
    private final double withholdingTax;
    private final double netPay;

    public PayrollBreakdown(
            double hoursWorked,
            double earnedBasicPay,
            double totalAllowances,
            double taxableAllowances,
            double nonTaxableAllowances,
            double grossPay,
            double taxableIncome,
            double sss,
            double philhealth,
            double pagibig,
            double withholdingTax,
            double netPay
    ) {
        this.hoursWorked = hoursWorked;
        this.earnedBasicPay = earnedBasicPay;
        this.totalAllowances = totalAllowances;
        this.taxableAllowances = taxableAllowances;
        this.nonTaxableAllowances = nonTaxableAllowances;
        this.grossPay = grossPay;
        this.taxableIncome = taxableIncome;
        this.sss = sss;
        this.philhealth = philhealth;
        this.pagibig = pagibig;
        this.withholdingTax = withholdingTax;
        this.netPay = netPay;
    }

    public double getHoursWorked() { return hoursWorked; }
    public double getEarnedBasicPay() { return earnedBasicPay; }
    public double getTotalAllowances() { return totalAllowances; }
    public double getTaxableAllowances() { return taxableAllowances; }
    public double getNonTaxableAllowances() { return nonTaxableAllowances; }
    public double getGrossPay() { return grossPay; }
    public double getTaxableIncome() { return taxableIncome; }
    public double getSss() { return sss; }
    public double getPhilhealth() { return philhealth; }
    public double getPagibig() { return pagibig; }
    public double getWithholdingTax() { return withholdingTax; }
    public double getNetPay() { return netPay; }
}