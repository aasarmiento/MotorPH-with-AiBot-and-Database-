package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.text.DecimalFormat;
import java.time.LocalDate;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import model.Employee;
import service.EmployeeManagementService;
import service.PayrollCalculator;
import service.PayrollService; 

public class MyPayslip extends BasePanel {
    private final EmployeeManagementService service;
    private final PayrollCalculator calc;
    private final PayrollService payrollService;
    private final Employee currentUser;
    private final DecimalFormat df = new DecimalFormat("#,##0.00");

    private final Color MOTORPH_MAROON = UIUtils.MOTORPH_MAROON;
    private final Color BG_LIGHT = UIUtils.BG_LIGHT;
    private final Color BORDER_COLOR = new Color(225, 225, 225);
    private final Color DIRT_WHITE = new Color(245, 245, 240);

    private final Font sectionFont = UIUtils.FONT_LABEL; 
    private final Font labelFont = UIUtils.FONT_BODY;
    private final Font valueFont = new Font("DM Sans Medium", Font.BOLD, 13);

    private JComboBox<String> monthPicker, yearPicker;
    private JLabel lblGross, lblSss, lblPhilhealth, lblTax, lblPagibig, lblNetPay;
    private JLabel lblEmployeeId, lblEmployeeName, lblPosition, lblStatus, lblDaysPresent;
    private JLabel lblBasic, lblAllowances, lblLateMinutes, lblLateDeduction, lblTaxableIncome;
    private JPanel paper; 

    public MyPayslip(EmployeeManagementService service, PayrollCalculator calc, PayrollService payrollService, Employee user) {
        super(); 
        this.service = service;
        this.calc = calc;
        this.payrollService = payrollService;
        this.currentUser = user;


        add(createPayslipHeader(), BorderLayout.NORTH);
        paper = createPayslipContent();
        
        JPanel centeringWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 20));
        centeringWrapper.setBackground(BG_LIGHT);
        centeringWrapper.add(paper);

        JScrollPane scrollPane = new JScrollPane(centeringWrapper);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        refreshData(); 
    }

    
        @Override
        public void refreshData() {
            calculateSalary(false);
        }

        private JPanel createPayslipHeader() {
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 15));
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR));

        JLabel lblPeriod = new JLabel("Period: ");
        lblPeriod.setFont(UIUtils.FONT_LABEL);

        monthPicker = new JComboBox<>(new String[]{
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        });
        monthPicker.setFont(labelFont);
        monthPicker.setSelectedIndex(LocalDate.now().getMonthValue() - 1);

        yearPicker = new JComboBox<>(new String[]{"2024", "2025", "2026"});
        yearPicker.setFont(labelFont);
        yearPicker.setSelectedItem("2024");

        JButton btnCalculate = UIUtils.createPrimaryButton("Generate Payslip");
        btnCalculate.setPreferredSize(new Dimension(160, 36)); 
        btnCalculate.addActionListener(e -> calculateSalary(true));

        JButton btnPrint = new JButton("Print PDF");
        btnPrint.setBackground(Color.WHITE);
        btnPrint.setForeground(MOTORPH_MAROON);
        btnPrint.setFocusPainted(false);
        btnPrint.setFont(UIUtils.FONT_LABEL);
        btnPrint.setBorder(BorderFactory.createLineBorder(MOTORPH_MAROON, 1));
        btnPrint.setPreferredSize(new Dimension(100, 32));
        btnPrint.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnPrint.addActionListener(e -> printToPDF());

        header.add(lblPeriod);
        header.add(monthPicker);
        header.add(yearPicker);
        header.add(Box.createHorizontalStrut(15));
        header.add(btnCalculate);
        header.add(Box.createHorizontalStrut(5));
        header.add(btnPrint);

        return header;
    }

    private JPanel createPayslipContent() {
        JPanel paperPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2.setColor(BORDER_COLOR);
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 15, 15);
                g2.dispose();
            }
        };
        paperPanel.setLayout(new BoxLayout(paperPanel, BoxLayout.Y_AXIS));
        paperPanel.setPreferredSize(new Dimension(800, 950)); 
        paperPanel.setBackground(Color.WHITE);
        paperPanel.setOpaque(false);
        paperPanel.setBorder(new EmptyBorder(0, 0, 40, 0)); 

        JPanel headerBrand = new JPanel();
        headerBrand.setLayout(new BoxLayout(headerBrand, BoxLayout.Y_AXIS));
        headerBrand.setOpaque(true); 
        headerBrand.setBackground(DIRT_WHITE); 
        headerBrand.setAlignmentX(Component.CENTER_ALIGNMENT);
        headerBrand.setMaximumSize(new Dimension(Integer.MAX_VALUE, 180));
        headerBrand.setBorder(new EmptyBorder(30, 0, 30, 0)); 

        try {
            ImageIcon logoIcon = new ImageIcon("/Volumes/WORK/311 721 2/resources/logo.png");
            Image img = logoIcon.getImage().getScaledInstance(-1, 60, Image.SCALE_SMOOTH);
            JLabel logoLabel = new JLabel(new ImageIcon(img));
            logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            headerBrand.add(logoLabel);
        } catch (Exception e) {}

        JLabel title = UIUtils.createHeaderLabel("PAYSLIP OVERVIEW");
        title.setForeground(MOTORPH_MAROON);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        headerBrand.add(title);
        paperPanel.add(headerBrand); 

        JPanel bodyPadding = new JPanel();
        bodyPadding.setLayout(new BoxLayout(bodyPadding, BoxLayout.Y_AXIS));
        bodyPadding.setOpaque(false);
        bodyPadding.setBorder(new EmptyBorder(25, 50, 20, 50)); 

        addSectionHeader(bodyPadding, "EMPLOYEE INFORMATION");
        lblEmployeeId = createLabeledRow(bodyPadding, "Employee #:");
        lblEmployeeName = createLabeledRow(bodyPadding, "Full Name:");
        lblPosition = createLabeledRow(bodyPadding, "Position:");
        lblStatus = createLabeledRow(bodyPadding, "Employment Status:");
        bodyPadding.add(Box.createVerticalStrut(20));

        addSectionHeader(bodyPadding, "ATTENDANCE SUMMARY");
        lblDaysPresent = createLabeledRow(bodyPadding, "Total Days Present:");
        lblLateMinutes = createLabeledRow(bodyPadding, "Late Minutes (Gross):");
        lblLateDeduction = createLabeledRow(bodyPadding, "Total Late Deductions:");
        bodyPadding.add(Box.createVerticalStrut(20));

        addSectionHeader(bodyPadding, "EARNINGS");
        lblBasic = createLabeledRow(bodyPadding, "Monthly Basic Salary:");
        lblAllowances = createLabeledRow(bodyPadding, "Total Monthly Allowances:");
        lblGross = createLabeledRow(bodyPadding, "Gross Pay (Adjusted):");
        bodyPadding.add(Box.createVerticalStrut(20));

        addSectionHeader(bodyPadding, "STATUTORY DEDUCTIONS & TAX");
        lblSss = createLabeledRow(bodyPadding, "SSS Contribution:");
        lblPhilhealth = createLabeledRow(bodyPadding, "PhilHealth Contribution:");
        lblPagibig = createLabeledRow(bodyPadding, "Pag-IBIG Contribution:");
        lblTaxableIncome = createLabeledRow(bodyPadding, "Computed Taxable Income:");
        lblTax = createLabeledRow(bodyPadding, "Withholding Tax:");
        
        bodyPadding.add(Box.createVerticalGlue());
        
        JPanel netPayPanel = new JPanel(new BorderLayout());
        netPayPanel.setOpaque(true);
        netPayPanel.setBackground(new Color(248, 248, 248));
        netPayPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        netPayPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        JLabel netLabel = new JLabel("TOTAL NET PAY");
        netLabel.setFont(UIUtils.FONT_LABEL);
        lblNetPay = new JLabel("PHP 0.00");
        lblNetPay.setFont(new Font("DM Sans Bold", Font.BOLD, 24));
        lblNetPay.setForeground(MOTORPH_MAROON);
        netPayPanel.add(netLabel, BorderLayout.WEST);
        netPayPanel.add(lblNetPay, BorderLayout.EAST);
        
        bodyPadding.add(netPayPanel);
        paperPanel.add(bodyPadding);
        return paperPanel;
    }

    private void addSectionHeader(JPanel parent, String text) {
        JLabel header = new JLabel(text);
        header.setFont(UIUtils.FONT_LABEL);
        header.setForeground(MOTORPH_MAROON);
        header.setAlignmentX(Component.LEFT_ALIGNMENT);
        parent.add(header);
        parent.add(Box.createVerticalStrut(10));
    }

    private JLabel createLabeledRow(JPanel parent, String labelText) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        JLabel label = new JLabel(labelText);
        label.setFont(labelFont);
        label.setForeground(Color.GRAY);
        JLabel value = new JLabel("0.00");
        value.setFont(valueFont);
        value.setForeground(Color.BLACK);
        value.setHorizontalAlignment(SwingConstants.RIGHT);
        row.add(label, BorderLayout.WEST);
        row.add(value, BorderLayout.EAST);
        row.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, UIUtils.BG_LIGHT));
        parent.add(row);
        parent.add(Box.createVerticalStrut(5));
        return value;
    }

    private void printToPDF() {
        JOptionPane.showMessageDialog(this, "Exporting to PDF...");
    }

    public final void calculateSalary(boolean showMessages) {
    try {
        if (monthPicker == null || yearPicker == null) return;

        // 1. Get Selections from UI
        String month = (String) monthPicker.getSelectedItem();
        String year = (String) yearPicker.getSelectedItem();
        int empNo = currentUser.getEmpNo();

        // 2. Data Retrieval (Let the Service handle the logic)
        model.PeriodSummary summary = service.getPayrollForEmployee(empNo, month, year);

        if (summary == null) {
            if (showMessages) {
                JOptionPane.showMessageDialog(this, "No payroll data found for " + month + " " + year);
            }
            clearLabels();
            return;
        }

        // 3. Update Profile Labels (Visuals)
        lblEmployeeId.setText(String.valueOf(empNo));
        lblEmployeeName.setText(currentUser.getFirstName() + " " + currentUser.getLastName());
        lblPosition.setText(currentUser.getPosition());
        lblStatus.setText(currentUser.getStatus());

        // 4. Update Attendance Visuals
        // Note: 'rawLogs.length' is the easiest way to show presence visually from the DAO
        Object[][] rawLogs = service.getAttendanceLogs(empNo, month, year);
        lblDaysPresent.setText(String.valueOf(rawLogs != null ? rawLogs.length : 0));

        // 5. Update Earnings & Deductions (Using summary data)
        lblLateMinutes.setText(summary.getTotalLateMinutes() + " mins");
        lblLateDeduction.setText("-" + df.format(summary.getLateDeduction()));
        
        // Use basic_salary from currentUser
        lblBasic.setText(df.format(currentUser.getBasicSalary()));

        double totalAllowances = currentUser.getRiceSubsidy() 
                               + currentUser.getPhoneAllowance() 
                               + currentUser.getClothingAllowance();
        lblAllowances.setText(df.format(totalAllowances));

        // 6. Final Payroll Totals
        lblGross.setText(df.format(summary.getGrossIncome()));
        lblSss.setText(df.format(summary.getSss()));
        lblPhilhealth.setText(df.format(summary.getPhilhealth()));
        lblPagibig.setText(df.format(summary.getPagibig()));

        // Tax Calculation (Keep it simple for the label)
        double taxable = summary.getGrossIncome() - (summary.getSss() + summary.getPhilhealth() + summary.getPagibig());
        lblTaxableIncome.setText(df.format(Math.max(0, taxable)));
        lblTax.setText(df.format(summary.getTax()));

        // 7. The "Big Number" (Net Pay)
        lblNetPay.setText("PHP " + df.format(Math.max(0, summary.getNetIncome())));

        // Refresh UI
        this.revalidate();
        this.repaint();

    } catch (Exception ex) {
        if (showMessages) {
            JOptionPane.showMessageDialog(this, "Error updating payslip: " + ex.getMessage());
        }
        ex.printStackTrace();
    }
}


    private void clearLabels() {
        lblDaysPresent.setText("0");
        lblLateMinutes.setText("0");
        lblLateDeduction.setText("0.00");
        lblGross.setText("0.00");
        lblNetPay.setText("PHP 0.00");
        lblSss.setText("0.00");
        lblPhilhealth.setText("0.00");
        lblPagibig.setText("0.00");
        lblTax.setText("0.00");
    }
}
