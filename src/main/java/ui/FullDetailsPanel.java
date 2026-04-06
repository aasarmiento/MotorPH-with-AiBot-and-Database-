package ui;

import java.awt.*;
import java.time.format.DateTimeFormatter;
import javax.swing.*;
import model.Employee;
import service.EmployeeManagementService;


public class FullDetailsPanel extends BasePanel {
    private final EmployeeManagementService service;
    private final Employee employee;

    private JTextField txtEmpNo, txtLastName, txtFirstName, txtStatus, txtPosition, txtSupervisor;
    private JTextField txtBirthday, txtAddress, txtPhone, txtSss, txtPhilHealth, txtTin, txtPagibig;
    private JTextField txtSalary, txtRice, txtPhoneAllowance, txtClothing, txtGross, txtHourly;
    private JLabel lblProfilePic;

    public FullDetailsPanel(EmployeeManagementService service, Employee currentUser) {
        super(); 
        this.service = service;
        this.employee = currentUser;

       
        this.add(createHomePanel(), BorderLayout.CENTER); 

      
        refreshData();
    }

   
    @Override
    public void refreshData() {
        if (this.employee != null) {
            loadPersonalDetails(this.employee);
        }
    }

    private void loadPersonalDetails(Employee emp) {
        if (emp == null || txtEmpNo == null) return; 

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("M/d/yyyy");

        txtEmpNo.setText(String.valueOf(emp.getEmpNo()));
        txtLastName.setText(emp.getLastName());
        txtFirstName.setText(emp.getFirstName());
        txtStatus.setText(emp.getStatus());
        txtPosition.setText(emp.getPosition());
        txtSupervisor.setText(emp.getSupervisor());

        if (emp.getBirthday() != null) {
            txtBirthday.setText(emp.getBirthday().format(dateFormatter));
        }
        
        txtAddress.setText(emp.getAddress());
        txtPhone.setText(emp.getPhone());
        txtSss.setText(emp.getSss());
        txtPhilHealth.setText(emp.getPhilhealth());
        txtTin.setText(emp.getTin());
        txtPagibig.setText(emp.getPagibig());

        txtSalary.setText(String.format("%.2f", emp.getBasicSalary()));
        txtRice.setText(String.format("%.2f", emp.getRiceSubsidy()));
        txtPhoneAllowance.setText(String.format("%.2f", emp.getPhoneAllowance()));
        txtClothing.setText(String.format("%.2f", emp.getClothingAllowance()));
        txtGross.setText(String.format("%.2f", emp.getGrossRate()));
        txtHourly.setText(String.format("%.2f", emp.getHourlyRate()));

        displayEmployeePhoto(lblProfilePic);
    }

    private JPanel createHomePanel() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setOpaque(false); 

        JPanel infoSection = createSection("Employee Information", 1, 1);
        infoSection.setLayout(new BorderLayout(15, 0));

        lblProfilePic = new JLabel("No Image");
        lblProfilePic.setPreferredSize(new Dimension(150, 150));
        lblProfilePic.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        lblProfilePic.setHorizontalAlignment(JLabel.CENTER);
        infoSection.add(lblProfilePic, BorderLayout.WEST);

        JPanel fieldsPanel = new JPanel(new GridLayout(3, 2, 10, 5));
        fieldsPanel.setOpaque(false);
        txtEmpNo = addField(fieldsPanel, "Employee No:");
        txtLastName = addField(fieldsPanel, "Last Name:");
        txtFirstName = addField(fieldsPanel, "First Name:");
        txtStatus = addField(fieldsPanel, "Status:");
        txtPosition = addField(fieldsPanel, "Position:");
        txtSupervisor = addField(fieldsPanel, "Supervisor:");
        infoSection.add(fieldsPanel, BorderLayout.CENTER);

        JPanel personalPanel = createSection("Personal Details", 4, 2);
        txtBirthday = addField(personalPanel, "Birthday:");
        txtAddress = addField(personalPanel, "Address:");
        txtPhone = addField(personalPanel, "Phone:");
        txtSss = addField(personalPanel, "SSS:");
        txtPhilHealth = addField(personalPanel, "PhilHealth:");
        txtTin = addField(personalPanel, "TIN:");
        txtPagibig = addField(personalPanel, "Pag-IBIG:");

        JPanel financePanel = createSection("Financial Information", 3, 2);
        txtSalary = addField(financePanel, "Basic Salary:");
        txtRice = addField(financePanel, "Rice Subsidy:");
        txtPhoneAllowance = addField(financePanel, "Phone Allowance:");
        txtClothing = addField(financePanel, "Clothing Allowance:"); 
        txtGross = addField(financePanel, "Gross Rate:");
        txtHourly = addField(financePanel, "Hourly Rate:");

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        actionPanel.setOpaque(false);
        JButton btnUpdate = new JButton("Update Info");
        btnUpdate.setBackground(UIUtils.MOTORPH_MAROON);
        btnUpdate.setForeground(Color.WHITE);
        btnUpdate.setFocusPainted(false);
        
        btnUpdate.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Update functionality coming soon.");
        });
        actionPanel.add(btnUpdate);

        mainPanel.add(infoSection);
        mainPanel.add(Box.createVerticalStrut(15));
        mainPanel.add(personalPanel);
        mainPanel.add(Box.createVerticalStrut(15));
        mainPanel.add(financePanel);
        mainPanel.add(actionPanel);

        return mainPanel;
    }

    private void displayEmployeePhoto(JLabel photoLabel) {
        if (photoLabel == null || txtEmpNo == null) return;
        try {
            String imagePath = "resources/photos/" + txtEmpNo.getText() + ".png";
            java.io.File file = new java.io.File(imagePath);
            if (file.exists()) {
                ImageIcon icon = new ImageIcon(imagePath);
                Image img = icon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
                photoLabel.setIcon(new ImageIcon(img));
                photoLabel.setText("");
            } else {
                photoLabel.setIcon(null);
                photoLabel.setText("No Photo");
            }
        } catch (Exception e) {
            photoLabel.setText("Error");
        }
    }
}