package util;

import java.awt.*;
import java.text.DecimalFormat; 
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import model.Employee;
import service.EmployeeManagementService;
import service.PayrollCalculator;
import service.PayrollService;
import ui.MyPayslip;

public class PayrollHistoryView extends JPanel {
    private JTable historyTable;
    private DefaultTableModel tableModel;
    private final PayrollService payrollService;
    private final EmployeeManagementService empService;
    private final PayrollCalculator calculator;
    private final DecimalFormat df = new DecimalFormat("#,##0.00");

    public PayrollHistoryView(PayrollService service, EmployeeManagementService empService, PayrollCalculator calculator) {
        this.payrollService = service;
        this.empService = empService;
        this.calculator = calculator;
        
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(900, 500));

        String[] columns = {"Period", "Employee ID", "Full Name", "Net Pay", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        
        historyTable = new JTable(tableModel);
        historyTable.setRowHeight(30);

        historyTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    int row = historyTable.getSelectedRow();
                    if (row != -1) {
                        int empId = Integer.parseInt(tableModel.getValueAt(row, 1).toString());
                        showDetailedPayslip(empId);
                    }
                }
            }
        });

        add(new JScrollPane(historyTable), BorderLayout.CENTER);
    }

   private void showDetailedPayslip(int empId) {
        Employee selectedEmp = empService.getEmployeeDao().findById(empId);
        if (selectedEmp != null) {
            MyPayslip payslipPanel = new MyPayslip(empService, calculator, payrollService, selectedEmp);
            
            Window parentWindow = SwingUtilities.getWindowAncestor(this);
            JDialog dialog = new JDialog(parentWindow, "Detailed Payroll Record", Dialog.ModalityType.APPLICATION_MODAL);
            
            dialog.add(payslipPanel);
            dialog.setSize(650, 850); 
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
        }
    }

    public void loadHistory(String month, String year) {
        tableModel.setRowCount(0);
        List<Object[]> historyData = payrollService.getHistoricalSummary(month, year);
        
        if (historyData != null) {
            for (Object[] row : historyData) {
                try {
                    double netPayValue = Double.parseDouble(row[3].toString());
                    row[3] = df.format(netPayValue); 
                } catch (Exception e) {
                }
                tableModel.addRow(row);
            }
        }
    }
}