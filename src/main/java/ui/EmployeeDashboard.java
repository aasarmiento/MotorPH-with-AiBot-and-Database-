package ui;

import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;
import model.Employee;
import service.EmployeeManagementService;


public class EmployeeDashboard extends BasePanel {
    private final JTable employeeTable;
    private final DefaultTableModel tableModel;
    private final EmployeeManagementService service; 
    
    private JTextField txtEmpNo, txtLastName, txtFirstName, txtStatus, txtPosition, txtSupervisor;

    public EmployeeDashboard(EmployeeManagementService service) {
        super(); 
        this.service = service; 

        
        String[] columns = {
            "ID", "Last Name", "First Name", "Birthday", "Address", "Phone", 
            "SSS", "Philhealth", "TIN", "Pagibig", "Status", "Position", "Supervisor"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        
        employeeTable = new JTable(tableModel);
        employeeTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); 
        employeeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        employeeTable.getSelectionModel().addListSelectionListener(this::handleTableSelection);
        
        
        add(new JScrollPane(employeeTable), BorderLayout.CENTER);
        add(createBottomPanel(), BorderLayout.SOUTH);
        
        refreshData();
    }

    
    @Override
    public void refreshData() {
        refreshTable();
    }

    private JPanel createBottomPanel() {
        JPanel mainBottom = new JPanel(new BorderLayout());
        mainBottom.setOpaque(false); // Maintain clean UI look
        mainBottom.setBorder(BorderFactory.createTitledBorder("Employee Information"));

        JPanel formPanel = new JPanel(new GridLayout(3, 4, 10, 10));
        formPanel.setOpaque(false);
        
        txtEmpNo = new JTextField(); txtLastName = new JTextField();
        txtFirstName = new JTextField(); txtStatus = new JTextField();
        txtPosition = new JTextField(); txtSupervisor = new JTextField();

        formPanel.add(new JLabel("EmployeeNo:")); formPanel.add(txtEmpNo);
        formPanel.add(new JLabel("LastName:")); formPanel.add(txtLastName);
        formPanel.add(new JLabel("FirstName:")); formPanel.add(txtFirstName);
        formPanel.add(new JLabel("Status:")); formPanel.add(txtStatus);
        formPanel.add(new JLabel("Position:")); formPanel.add(txtPosition);
        formPanel.add(new JLabel("Supervisor:")); formPanel.add(txtSupervisor);

        JPanel buttonPanel = new JPanel(new GridLayout(4, 1, 5, 5));
        buttonPanel.setOpaque(false);
        
        
        buttonPanel.add(UIUtils.createButton("Calculate Salary", new Color(52, 58, 235), Color.white));
        buttonPanel.add(UIUtils.createButton("Set Password", new Color(235, 122, 52), Color.white));
        buttonPanel.add(UIUtils.createButton("Update Employee", new Color(0, 150, 0), Color.white));
        buttonPanel.add(UIUtils.createButton("Delete Employee", new Color(150, 0, 0), Color.white));

        mainBottom.add(formPanel, BorderLayout.CENTER);
        mainBottom.add(buttonPanel, BorderLayout.EAST);
        return mainBottom;
    }

    public final void refreshTable() {
        if (tableModel == null) return;
        tableModel.setRowCount(0); 
        
        try {
            List<Employee> list = service.getAll(); 

            for (Employee emp : list) {
                tableModel.addRow(new Object[]{
                    emp.getEmpNo(), 
                    emp.getLastName(), 
                    emp.getFirstName(), 
                    emp.getBirthday(),
                    emp.getAddress(), 
                    emp.getPhone(), 
                    emp.getSss(), 
                    emp.getPhilhealth(),
                    emp.getTin(), 
                    emp.getPagibig(), 
                    emp.getStatus(), 
                    emp.getPosition(), 
                    emp.getSupervisor()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Refresh Error: " + e.getMessage());
        }
    }

    public void handleTableSelection(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting() && employeeTable.getSelectedRow() != -1) {
            int row = employeeTable.getSelectedRow();
            txtEmpNo.setText(safeGet(row, 0));
            txtLastName.setText(safeGet(row, 1));
            txtFirstName.setText(safeGet(row, 2));
            txtStatus.setText(safeGet(row, 10));
            txtPosition.setText(safeGet(row, 11));
            txtSupervisor.setText(safeGet(row, 12));
        }
    }
    
    private String safeGet(int row, int col) {
        Object val = tableModel.getValueAt(row, col);
        return (val == null) ? "" : val.toString();
    }

   
    public void reloadCSV() { refreshTable(); }
}