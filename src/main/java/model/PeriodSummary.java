package model;

public class PeriodSummary {
    private double grossIncome, sss, philhealth, pagibig, tax, netPay;
    private double lateDeduction; 
    private int totalLateMinutes; 

    public PeriodSummary(double gross, double sss, double ph, double pi, double tax, double net) {
        this.grossIncome = gross;
        this.sss = sss;
        this.philhealth = ph;
        this.pagibig = pi;
        this.tax = tax;
        this.netPay = net;
    }

    public double getGrossIncome() { return grossIncome; }
    public double getSss() { return sss; }
    public double getPhilhealth() { return philhealth; }
    public double getPagibig() { return pagibig; }
    public double getTax() { return tax; }
    
    public double getNetIncome() { return netPay; }
    public void setNetIncome(double netPay) { this.netPay = netPay; }

    public double getLateDeduction() { return lateDeduction; }
    public void setLateDeduction(double lateDeduction) { this.lateDeduction = lateDeduction; }

    public int getTotalLateMinutes() { return totalLateMinutes; }
    public void setTotalLateMinutes(int totalLateMinutes) { this.totalLateMinutes = totalLateMinutes; }

    public double getNetPay() { return netPay; }
    public double getTin() { return tax; } 
    
    public void setGrossIncome(double grossIncome) { this.grossIncome = grossIncome; }
    public void setSss(double sss) { this.sss = sss; }
    public void setPhilhealth(double philhealth) { this.philhealth = philhealth; }
    public void setPagibig(double pagibig) { this.pagibig = pagibig; }
    public void setTax(double tax) { this.tax = tax; }
}
