package util;

import dao.CSVHandler;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import model.Employee;
import model.RegularStaff;
import ui.UIUtils;

public class EmployeeStyle extends JFrame {

   
    private final CSVHandler csvHandler = new CSVHandler();
    
    private final JTextField txtEmpNo = UIUtils.createTextField(false); 
    private final JTextField txtLastName = UIUtils.createTextField(true);
    private final JTextField txtFirstName = UIUtils.createTextField(true);
    private final JTextField txtBirthday = UIUtils.createTextField(true); 
    private final JTextField txtAddress = UIUtils.createTextField(true);  
    private final JTextField txtPhone = UIUtils.createTextField(true);    
    
    public EmployeeStyle() {
        setTitle("MotorPH Employee Management System");
        setLayout(new BorderLayout());
        
        JPanel infoContainer = new JPanel(new GridLayout(1, 3, 10, 10));
        
        infoContainer.add(UIUtils.createEmployeeInfoPanel(
            txtEmpNo, 
            txtLastName, 
            txtFirstName, 
            txtBirthday, 
            txtAddress, 
            txtPhone
        ));
        
        add(infoContainer, BorderLayout.CENTER);
        
        JButton btnSave = UIUtils.createButton("Save Changes", Color.BLUE, Color.WHITE);
        btnSave.addActionListener(e -> handleSave());
        add(btnSave, BorderLayout.SOUTH);

        setSize(900, 600);
        setLocationRelativeTo(null);
    }

 private void handleSave() {
        try {
            int id = Integer.parseInt(txtEmpNo.getText());
            String ln = txtLastName.getText();
            String fn = txtFirstName.getText();

            
            java.time.LocalDate placeholderDate = java.time.LocalDate.now();
            double placeholderSalary = 0.0;

            Employee emp = new RegularStaff(id, ln, fn, placeholderDate, placeholderSalary);
            
            
            csvHandler.create(emp); 
            
            JOptionPane.showMessageDialog(this, "Employee Saved Successfully!");
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid Employee ID format.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error saving: " + ex.getMessage());
        }
    }
}