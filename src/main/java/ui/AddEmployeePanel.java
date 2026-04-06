package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import service.EmployeeManagementService;

public class AddEmployeePanel extends JPanel {

    private final EmployeeManagementService service;

    private final JTextField empNo = new JTextField();
    private final JTextField lastName = new JTextField();
    private final JTextField firstName = new JTextField();
    private final JTextField status = new JTextField();
    private final JTextField position = new JTextField();
    private final JTextField supervisor = new JTextField();
    private final JTextField birthday = new JTextField();
    private final JTextField address = new JTextField();
    private final JTextField phone = new JTextField();
    private final JTextField sss = new JTextField();
    private final JTextField philhealth = new JTextField();
    private final JTextField tin = new JTextField();
    private final JTextField pagibig = new JTextField();
    private final JTextField basicSalary = new JTextField();
    private final JTextField riceSubsidy = new JTextField();
    private final JTextField phoneAllowance = new JTextField();
    private final JTextField clothingAllowance = new JTextField();
    private final JTextField grossRate = new JTextField();
    private final JTextField hourlyRate = new JTextField();

    public AddEmployeePanel(EmployeeManagementService service) {
        this.service = service;
        
        setLayout(new BorderLayout());

        JLabel addHeading = UIUtils.createHeaderLabel("Add Employee");
        add(addHeading, BorderLayout.NORTH);

        JPanel buttons = new JPanel();
        JButton btnAddEmployee = UIUtils.createButton("Add New Employee", new Color(0, 180, 0), Color.WHITE);
        JButton btnClear = UIUtils.createButton("Clear", Color.GRAY, Color.WHITE);
        buttons.add(btnAddEmployee);
        buttons.add(btnClear);

        JPanel formPanel = new JPanel(new GridLayout(3, 1, 5, 2));
        formPanel.add(UIUtils.createEmployeeInfoPanel(empNo, lastName, firstName, status, position, supervisor));
        formPanel.add(UIUtils.createPersonalInfoPanel(birthday, address, phone, sss, philhealth, tin, pagibig));
        formPanel.add(UIUtils.createFinancialInfoPanel(basicSalary, riceSubsidy, phoneAllowance, clothingAllowance, grossRate, hourlyRate));

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(formPanel, BorderLayout.CENTER);
        
        add(buttons, BorderLayout.WEST);
        add(bottomPanel, BorderLayout.CENTER);

        empNo.setEditable(false);
        updateNextEmployeeID();

        btnAddEmployee.addActionListener(e -> handleAddEmployee());
        btnClear.addActionListener(e -> clearFields());
    }

    private void updateNextEmployeeID() {
        int nextId = service.generateNextEmployeeId(); 
        empNo.setText(String.valueOf(nextId));
    }

    private void handleAddEmployee() {
        if (lastName.getText().trim().isEmpty() || firstName.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in Name.", "Missing Info", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            
            
            JOptionPane.showMessageDialog(this, "Employee successfully added!");
            clearFields();
            updateNextEmployeeID();
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error adding employee: " + ex.getMessage());
        }
    }

    private void clearFields() {
        lastName.setText("");
        firstName.setText("");
        status.setText("");
        position.setText("");
        supervisor.setText("");
        birthday.setText("");
        address.setText("");
        phone.setText("");
        sss.setText("");
        philhealth.setText("");
        tin.setText("");
        pagibig.setText("");
        basicSalary.setText("");
        riceSubsidy.setText("");
        phoneAllowance.setText("");
        clothingAllowance.setText("");
        grossRate.setText("");
        hourlyRate.setText("");
    }
}