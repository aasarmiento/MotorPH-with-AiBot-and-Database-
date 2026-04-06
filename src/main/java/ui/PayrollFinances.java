package ui;

import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import model.Employee;
import service.EmployeeManagementService;
import service.PayrollCalculator;
import service.PayrollService;

// Updated to extend BasePanel and properly implement OOP principles
public class PayrollFinances extends BasePanel {
    private final EmployeeManagementService empService;
    private final PayrollService payrollService;
    private final PayrollCalculator calc;
    
    private JTable payrollTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> monthPicker, yearPicker;
    private final DecimalFormat df = new DecimalFormat("#,##0.00");
    
    private JLabel lblTotalSalary, lblTotalAllowances, lblTotalGross, lblTotalNet;
    
    private BarChartPanel overviewChart;
    private DoughnutChartPanel breakdownChart;

    public PayrollFinances(EmployeeManagementService empService, PayrollService payrollService, PayrollCalculator calc, Employee user) {
        super(); // Initializes BasePanel background, layout, and padding
        this.empService = empService;
        this.payrollService = payrollService;
        this.calc = calc;
        
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel topPanel = new JPanel(new BorderLayout(0, 10));
        topPanel.setOpaque(false);
        topPanel.add(createFilterPanel(), BorderLayout.NORTH);
        topPanel.add(createKPIRow(), BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout(20, 20));
        centerPanel.setOpaque(false);

        JPanel chartPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        chartPanel.setOpaque(false);
        
        overviewChart = new BarChartPanel("Payroll Overview", motorPHRed);
        breakdownChart = new DoughnutChartPanel("Payroll Breakdown", motorPHRed);
        
        chartPanel.add(overviewChart);
        chartPanel.add(breakdownChart);
        
        centerPanel.add(chartPanel, BorderLayout.NORTH);
        centerPanel.add(createPayrollListTable(), BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);
        
        SwingUtilities.invokeLater(this::refreshData);
    }

    private JPanel createFilterPanel() {
        JPanel filter = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filter.setOpaque(false);

        monthPicker = new JComboBox<>(new String[]{
            "January", "February", "March", "April", "May", "June", 
            "July", "August", "September", "October", "November", "December"
        });
        yearPicker = new JComboBox<>(new String[]{"2024", "2025", "2026"});
        
        monthPicker.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) refreshData();
        });
        yearPicker.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) refreshData();
        });

        JButton btnGenerate = new JButton("Generate Payroll");
        btnGenerate.setPreferredSize(new Dimension(150, 30));
        btnGenerate.setBackground(motorPHRed);
        btnGenerate.setForeground(Color.WHITE);
        btnGenerate.setFocusPainted(false);
        btnGenerate.addActionListener(e -> generatePayroll());

        filter.add(new JLabel("Payroll Period:"));
        filter.add(monthPicker);
        filter.add(yearPicker);
        filter.add(btnGenerate);
        
        return filter;
    }

    private JPanel createKPIRow() {
        JPanel row = new JPanel(new GridLayout(1, 4, 15, 0));
        row.setOpaque(false);
        
        KPICard card1 = new KPICard("Total Basic", "₱0.00", motorPHRed);
        lblTotalSalary = card1.getValueLabel();
        KPICard card2 = new KPICard("Total Allowances", "₱0.00", motorPHRed);
        lblTotalAllowances = card2.getValueLabel();
        KPICard card3 = new KPICard("Total Gross", "₱0.00", motorPHRed);
        lblTotalGross = card3.getValueLabel();
        KPICard card4 = new KPICard("Total Net Pay", "₱0.00", new Color(40, 167, 69)); // Success Green
        lblTotalNet = card4.getValueLabel();

        row.add(card1); row.add(card2); row.add(card3); row.add(card4);
        return row;
    }

    private JPanel createPayrollListTable() {
        // Use the styled tile logic from BasePanel
        JPanel container = createStyledTile();
        container.setLayout(new BorderLayout());
        container.setBackground(Color.WHITE);

        JLabel title = new JLabel("Employee Payroll Summary");
        title.setFont(new Font("SansSerif", Font.BOLD, 16));
        container.add(title, BorderLayout.NORTH);

        String[] columns = {"ID", "Name", "Position", "Basic Salary", "Allowances", "Gross Pay", "Net Pay", "Status", "Action"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        payrollTable = new JTable(tableModel);
        payrollTable.setRowHeight(35);
        payrollTable.getTableHeader().setFont(cardTitleFont); // Inherited from BasePanel
        payrollTable.setSelectionBackground(motorPHRed); 
        payrollTable.setSelectionForeground(Color.WHITE);          
        payrollTable.setShowGrid(false);
        payrollTable.setIntercellSpacing(new Dimension(0, 0));

        payrollTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                int column = payrollTable.columnAtPoint(evt.getPoint());
                int row = payrollTable.rowAtPoint(evt.getPoint());
                if (row != -1 && column == 8) {
                    try {
                        Object idValue = tableModel.getValueAt(row, 0);
                        showDetailedRecord(Integer.parseInt(idValue.toString()));
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(payrollTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        container.add(scrollPane, BorderLayout.CENTER);
        
        return container;
    }

    private void showDetailedRecord(int empId) {
        Employee selectedEmp = empService.getEmployeeDao().findById(empId);
        if (selectedEmp != null) {
            MyPayslip payslipPanel = new MyPayslip(empService, calc, payrollService, selectedEmp);
            Window parentWindow = SwingUtilities.getWindowAncestor(this);
            JDialog dialog = new JDialog(parentWindow instanceof Frame ? (Frame)parentWindow : null, "Employee Payslip Record", true);
            dialog.add(payslipPanel);
            dialog.setSize(850, 900); 
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
        }
    }

    @Override
    public void refreshData() {
        if (tableModel == null || monthPicker == null || yearPicker == null) return;
        tableModel.setRowCount(0); 
        String month = (String) monthPicker.getSelectedItem();
        String year = (String) yearPicker.getSelectedItem();

        try {
            List<Object[]> report = payrollService.getFullPayrollReport(month, year);
            if (report == null) return;

            double totalBasic = 0, totalAllowances = 0, totalGross = 0, totalNet = 0;

            for (Object[] row : report) {
                double b = (row[3] instanceof Number) ? ((Number) row[3]).doubleValue() : 0.0;
                double a = (row[4] instanceof Number) ? ((Number) row[4]).doubleValue() : 0.0;
                double g = (row[5] instanceof Number) ? ((Number) row[5]).doubleValue() : 0.0;
                double n = (row[6] instanceof Number) ? ((Number) row[6]).doubleValue() : 0.0;

                tableModel.addRow(new Object[]{
                    row[0], row[1], row[2], "₱" + df.format(b), "₱" + df.format(a), 
                    "₱" + df.format(g), "₱" + df.format(n), row[7], "View Record >"
                });
                
                totalBasic += b; totalAllowances += a; totalGross += g; totalNet += n;
            }
            
            lblTotalSalary.setText("₱" + df.format(totalBasic));
            lblTotalAllowances.setText("₱" + df.format(totalAllowances));
            lblTotalGross.setText("₱" + df.format(totalGross));
            lblTotalNet.setText("₱" + df.format(totalNet));

            overviewChart.updateData(totalBasic, totalAllowances, totalGross, totalNet);
            breakdownChart.updateData(totalNet, (totalGross - totalNet));
            
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void generatePayroll() {
        JOptionPane.showMessageDialog(this, "Batch processing complete.");
        refreshData();
    }

    // --- Inner classes for Charts ---

    private static class BarChartPanel extends JPanel {
        private double b, a, g, n;
        private final String title;
        private final Color motorPhMaroon;
        private final Color motorPhBlack = new Color(30, 30, 30);
        private final Color motorPhLightMaroon = new Color(200, 50, 50);

        public BarChartPanel(String title, Color themeColor) {
            this.title = title;
            this.motorPhMaroon = themeColor;
            setBackground(Color.WHITE);
            setPreferredSize(new Dimension(0, 250));
            setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        }

        public void updateData(double b, double a, double g, double n) {
            this.b = b; this.a = a; this.g = g; this.n = n;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g2) {
            super.paintComponent(g2);
            Graphics2D g2d = (Graphics2D) g2;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            g2d.setFont(new Font("SansSerif", Font.BOLD, 40));
            g2d.setColor(new Color(245, 245, 245));
            g2d.drawString("MotorPH", getWidth()/2 - 80, getHeight()/2 + 20);

            g2d.setFont(new Font("SansSerif", Font.BOLD, 14));
            g2d.setColor(motorPhBlack);
            g2d.drawString(title, 15, 25);

            double max = Math.max(g, 1.0);
            int chartH = getHeight() - 100;
            double[] vals = {b, a, g, n};
            String[] lbls = {"Basic", "Allow", "Gross", "Net"};
            Color[] clrs = {motorPhMaroon, motorPhLightMaroon, Color.DARK_GRAY, motorPhBlack};

            for (int i = 0; i < 4; i++) {
                int h = (int)((vals[i]/max) * chartH);
                int x = 40 + (i * 70);
                int y = getHeight() - 50 - h;
                
                GradientPaint gp = new GradientPaint(x, y, clrs[i], x, y + h, clrs[i].darker());
                g2d.setPaint(gp);
                g2d.fillRoundRect(x, y, 40, h, 10, 10);
                
                g2d.setColor(motorPhBlack);
                g2d.setFont(new Font("SansSerif", Font.PLAIN, 10));
                g2d.drawString(lbls[i], x, getHeight() - 35);
            }
        }
    }

    private static class DoughnutChartPanel extends JPanel {
        private double net, deduct;
        private final String title;
        private final Color themeColor;

        public DoughnutChartPanel(String title, Color themeColor) {
            this.title = title;
            this.themeColor = themeColor;
            setBackground(Color.WHITE);
            setPreferredSize(new Dimension(0, 250));
            setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        }

        public void updateData(double net, double deduct) {
            this.net = net; this.deduct = Math.max(0, deduct);
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g2) {
            super.paintComponent(g2);
            Graphics2D g2d = (Graphics2D) g2;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            g2d.setFont(new Font("SansSerif", Font.BOLD, 40));
            g2d.setColor(new Color(245, 245, 245));
            g2d.drawString("MotorPH", getWidth()/2 - 80, getHeight()/2 + 20);

            g2d.setFont(new Font("SansSerif", Font.BOLD, 14));
            g2d.setColor(new Color(30, 30, 30));
            g2d.drawString(title, 15, 25);

            double total = net + deduct;
            if (total <= 0) return;

            int diameter = 140;
            int x = (getWidth() - diameter) / 2;
            int y = (getHeight() - diameter) / 2 + 10;
            int startAngle = 90;
            int netAngle = (int)((net/total) * 360);

            g2d.setColor(new Color(30, 30, 30));
            g2d.fillArc(x, y, diameter, diameter, startAngle, netAngle);
            g2d.setColor(themeColor);
            g2d.fillArc(x, y, diameter, diameter, startAngle + netAngle, 360 - netAngle);

            g2d.setColor(Color.WHITE);
            g2d.fillOval(x + 35, y + 35, 70, 70); 
            
            g2d.setFont(new Font("SansSerif", Font.PLAIN, 10));
            g2d.setColor(new Color(30, 30, 30));
            g2d.fillRect(15, getHeight()-30, 10, 10);
            g2d.drawString("Net Pay", 30, getHeight()-22);
            g2d.setColor(themeColor);
            g2d.fillRect(80, getHeight()-30, 10, 10);
            g2d.drawString("Deductions", 95, getHeight()-22);
        }
    }

    private static class KPICard extends JPanel {
        private final JLabel valueLabel;
        public KPICard(String title, String value, Color themeColor) {
            setLayout(new BorderLayout());
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
            ));
            JPanel textPanel = new JPanel(new GridLayout(2, 1));
            textPanel.setOpaque(false);
            JLabel lblTitle = new JLabel(title);
            lblTitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
            lblTitle.setForeground(Color.GRAY);
            valueLabel = new JLabel(value);
            valueLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
            textPanel.add(lblTitle); textPanel.add(valueLabel);
            JLabel iconLabel = new JLabel("₱", SwingConstants.CENTER);
            iconLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
            iconLabel.setPreferredSize(new Dimension(45, 45));
            iconLabel.setOpaque(true);
            iconLabel.setBackground(new Color(themeColor.getRed(), themeColor.getGreen(), themeColor.getBlue(), 30));
            iconLabel.setForeground(themeColor);
            add(textPanel, BorderLayout.CENTER); add(iconLabel, BorderLayout.EAST);
        }
        public JLabel getValueLabel() { return valueLabel; }
    }
}