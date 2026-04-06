package util;

import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;
import model.Employee;
import model.Role;
import service.EmployeeManagementService;

public class EmployeeDetailForm extends JFrame {
    private JTextField[] fields;
    private JComboBox<String> comboPosition;
    private JComboBox<String> comboSupervisor;
    private JComboBox<String> comboStatus; 
    private JComboBox<String> comboRole; 
    
    private JButton btnEdit, btnSave;
    private final EmployeeManagementService service;
    private final Employee actor;
    private final Employee viewedEmployee;
    private JLabel lblProfilePic;
    
    private String[] originalValues;

    private final Color primaryMaroon = new Color(128, 0, 0);
    private final Color bgColor = new Color(245, 245, 245);
    private final Color darkGray = new Color(45, 45, 45);
    private final Font labelFont = new Font("DM Sans Bold", Font.BOLD, 12);
    private final Font fieldFont = new Font("DM Sans Regular", Font.PLAIN, 13);

    private final String[] labels = {
        "Employee #", "Last Name", "First Name", "Gender", "Birthday", "Address", "Phone #",
        "SSS #", "Philhealth #", "TIN #", "Pag-ibig #", "Status", "Position",
        "Immediate Supervisor", "Basic Salary", "Rice Subsidy", "Phone Allowance",
        "Clothing Allowance", "Gross Semi-monthly Rate", "Hourly Rate", "Access Role" 
    };

    public EmployeeDetailForm(Object[] rawRowData, EmployeeManagementService service, Employee actor, Employee viewedEmployee) {
        this.service = service;
        this.actor = actor;
        this.viewedEmployee = viewedEmployee;

        setTitle("MotorPH - Employee Full Details");
        setSize(550, 900); 
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        
        setLayout(new BorderLayout());
        getContentPane().setBackground(bgColor);

        String[] displayData = service.getFormattedDataForForm(rawRowData);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 20));
        topPanel.setOpaque(false);

        lblProfilePic = new RoundedImageLabel();
        lblProfilePic.setPreferredSize(new Dimension(120, 120));
        lblProfilePic.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblProfilePic.setToolTipText("Click to change photo");
        
        lblProfilePic.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Images", "jpg", "png", "jpeg"));
                if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    service.updateEmployeePhoto(viewedEmployee, fileChooser.getSelectedFile());
                    displayEmployeePhoto(lblProfilePic);
                }
            }
        });
        
        displayEmployeePhoto(lblProfilePic);
        topPanel.add(lblProfilePic);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230)),
            BorderFactory.createEmptyBorder(20, 30, 20, 30)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 5, 6, 5);
        fields = new JTextField[labels.length];
        originalValues = new String[labels.length];

        String[] positions = {
            "Chief Executive Officer", "Chief Operating Officer", "Chief Finance Officer", 
            "Chief Marketing Officer", "IT Operations and Systems", "HR Manager", 
            "HR Team Leader", "HR Rank and File", "Accounting Head", "Payroll Manager", 
            "Payroll Team Leader", "Payroll Rank and File", "Account Manager", 
            "Account Team Leader", "Account Rank and File", "Sales & Marketing", 
            "Supply Chain and Logistics", "Customer Service and Relations"
        };
        
        String[] statusOptions = {"Probationary", "Regular"};
        
        String[] roleLabels = {
            Role.ADMIN.getLabel(), 
            Role.HR_STAFF.getLabel(), 
            Role.IT_STAFF.getLabel(), 
            Role.ACCOUNTING.getLabel(), 
            Role.REGULAR_STAFF.getLabel()
        };

        comboPosition = new JComboBox<>(positions);
        comboSupervisor = new JComboBox<>();
        comboStatus = new JComboBox<>(statusOptions);
        comboRole = new JComboBox<>(roleLabels);

        comboPosition.setEnabled(false);
        comboSupervisor.setEnabled(false);
        comboStatus.setEnabled(false);
        comboRole.setEnabled(false);

        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0; gbc.gridy = i; gbc.weightx = 0.3;
            JLabel lbl = new JLabel(labels[i] + ":");
            lbl.setFont(labelFont);
            lbl.setForeground(darkGray);
            mainPanel.add(lbl, gbc);

            gbc.gridx = 1; gbc.weightx = 0.7;
            String value = (i < displayData.length && displayData[i] != null) ? displayData[i] : "";

            if (i == 11) {
                comboStatus.setSelectedItem(value);
                mainPanel.add(comboStatus, gbc);
            }
            else if (i == 12) {
                comboPosition.setSelectedItem(value);
                mainPanel.add(comboPosition, gbc);
                comboPosition.addItemListener(e -> {
                    if (e.getStateChange() == ItemEvent.SELECTED) {
                        updateAutomaticFields((String) comboPosition.getSelectedItem());
                    }
                });
            } 
            else if (i == 13) {
                updateSupervisorList(value);
                mainPanel.add(comboSupervisor, gbc);
            }
            else if (i == 20) { 
               
                try {
                    Role r = Role.valueOf(value);
                    comboRole.setSelectedItem(r.getLabel());
                } catch (Exception e) {
                    comboRole.setSelectedItem(value);
                }
                mainPanel.add(comboRole, gbc);
            }
            else {
                fields[i] = new JTextField(value);
                fields[i].setFont(fieldFont);
                fields[i].setEditable(false);
                fields[i].setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(220, 220, 220)),
                    BorderFactory.createEmptyBorder(5, 8, 5, 8)
                ));
                
                applyFilters(i, fields[i]);
                
                if (i == 5 || i == 14 || i == 1 || i == 6) {
                    fields[i].setBackground(Color.WHITE);
                } else {
                    fields[i].setBackground(new Color(240, 240, 240));
                }
                mainPanel.add(fields[i], gbc);
            }
        }

        fields[14].getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) { updateRates(); }
            public void removeUpdate(DocumentEvent e) { updateRates(); }
            public void insertUpdate(DocumentEvent e) { updateRates(); }
            private void updateRates() {
                try {
                    String text = fields[14].getText().replaceAll("[^\\d.]", "");
                    if (!text.isEmpty()) {
                        double basic = Double.parseDouble(text);
                        fields[18].setText(String.format("%.2f", basic / 2));
                        fields[19].setText(String.format("%.2f", basic / 21 / 8));
                    }
                } catch (NumberFormatException ex) {}
            }
        });

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(topPanel, BorderLayout.NORTH);
        wrapper.add(mainPanel, BorderLayout.CENTER);

        JScrollPane scroll = new JScrollPane(wrapper);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);
        
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 15));
        footer.setBackground(bgColor);
        addActionButtons(footer);
        add(footer, BorderLayout.SOUTH);
        
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (btnSave.isVisible()) {
                    int option = JOptionPane.showConfirmDialog(EmployeeDetailForm.this, 
                        "Save changes before closing?", "Unsaved Changes", JOptionPane.YES_NO_CANCEL_OPTION);
                    if (option == JOptionPane.YES_OPTION) performSaveLogic();
                    else if (option == JOptionPane.NO_OPTION) dispose();
                } else dispose();
            }
        });
        setVisible(true);
    }

    private void updateAutomaticFields(String position) {
        updateSupervisorList(null);
        double basicSalary = service.getBasicSalaryForPosition(position);
        double[] allowances = service.getStandardAllowances(position);
        if (fields[14] != null) fields[14].setText(String.format("%.2f", basicSalary));
        if (fields[15] != null) fields[15].setText(String.valueOf((int)allowances[0]));
        if (fields[16] != null) fields[16].setText(String.valueOf((int)allowances[1]));
        if (fields[17] != null) fields[17].setText(String.valueOf((int)allowances[2]));
        if (fields[18] != null) fields[18].setText(String.format("%.2f", basicSalary / 2));
        if (fields[19] != null) fields[19].setText(String.format("%.2f", basicSalary / 21 / 8));
    }

    private void updateSupervisorList(String currentVal) {
        String pos = (String) comboPosition.getSelectedItem();
        comboSupervisor.removeAllItems();
        String[] supervisors = service.getSupervisorsForPosition(pos);
        for (String s : supervisors) comboSupervisor.addItem(s);
        if (currentVal != null) comboSupervisor.setSelectedItem(currentVal);
    }

    private void displayEmployeePhoto(JLabel lblPhoto) {
        int empId = viewedEmployee.getEmpNo();
        File imgFile = new File("resources/profile_pics/" + empId + ".png");
        if (!imgFile.exists()) imgFile = new File("resources/profile_pics/" + empId + ".jpg");
        if (!imgFile.exists()) imgFile = new File("resources/profile_pics/default.png");
        try {
            if (imgFile.exists()) {
                Image img = new ImageIcon(imgFile.getAbsolutePath()).getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
                lblPhoto.setIcon(new ImageIcon(img));
                lblPhoto.setText(""); 
            } else lblPhoto.setText("No Image");
        } catch (Exception e) { lblPhoto.setText("Error"); }
    }

    private void applyFilters(int index, JTextField field) {
        AbstractDocument doc = (AbstractDocument) field.getDocument();
        switch (index) {
            case 1: doc.setDocumentFilter(new StringOnlyFilter()); break; 
            case 6: doc.setDocumentFilter(new util.MaskFormatterFilter("###-###-###")); break;
            case 7: doc.setDocumentFilter(new util.MaskFormatterFilter("##-#######-#")); break;
            case 10: doc.setDocumentFilter(new NumericLimitFilter(12)); break;
            case 14: case 15: case 16: case 17: doc.setDocumentFilter(new NumericLimitFilter(10)); break;
        }
    }

    private void addActionButtons(JPanel buttonPanel) {
        btnEdit = new StyledButton("Edit Employee", darkGray);
        btnSave = new StyledButton("Save Changes", primaryMaroon);
        btnSave.setVisible(false);
        btnEdit.addActionListener(e -> { 
            originalValues[1] = fields[1].getText();
            originalValues[5] = fields[5].getText();
            originalValues[6] = fields[6].getText();
            originalValues[14] = fields[14].getText();
            originalValues[20] = (String) comboRole.getSelectedItem(); 
            
            setFieldsEditable(true); 
            btnEdit.setVisible(false); 
            btnSave.setVisible(true); 
        });
        btnSave.addActionListener(e -> performSaveLogic());
        buttonPanel.add(btnEdit); buttonPanel.add(btnSave);
    }

    private void performSaveLogic() {
        try {
            String salaryStr = fields[14].getText().replaceAll("[^\\d.]", "");
            double salary = Double.parseDouble(salaryStr);
            if (salary <= 20000) {
                JOptionPane.showMessageDialog(this, "Salary must be greater than 20,000.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid amount for Basic Salary.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            return;
        }

        List<String> changedDetails = new ArrayList<>();
        if (!fields[1].getText().equals(originalValues[1])) changedDetails.add("Last Name");
        if (!fields[5].getText().equals(originalValues[5])) changedDetails.add("Address");
        if (!fields[6].getText().equals(originalValues[6])) changedDetails.add("Phone #");
        if (!fields[14].getText().equals(originalValues[14])) changedDetails.add("Basic Salary");
        
        String selectedRoleLabel = (String) comboRole.getSelectedItem();
        if (originalValues[20] == null || !selectedRoleLabel.equals(originalValues[20])) {
            changedDetails.add("Access Role");
        }

        if (changedDetails.isEmpty()) {
            setFieldsEditable(false); btnSave.setVisible(false); btnEdit.setVisible(true);
            return;
        }

        String detailList = String.join(", ", changedDetails);
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to change: " + detailList + "?", 
            "Confirm Changes", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            // Update fields array from ComboBoxes before passing to service
            fields[11] = new JTextField((String) comboStatus.getSelectedItem());
            fields[12] = new JTextField((String) comboPosition.getSelectedItem());
            fields[13] = new JTextField((String) comboSupervisor.getSelectedItem());
            
            // Convert selected label ("IT Staff") back to Enum name ("IT_STAFF")
            Role roleEnum = Role.fromLabel(selectedRoleLabel);
            fields[20] = new JTextField(roleEnum.name()); 
            
            if (service.updateEmployeeFromForm(actor, fields)) {
                JOptionPane.showMessageDialog(this, "Changes Saved Successfully");
                setFieldsEditable(false); btnSave.setVisible(false); btnEdit.setVisible(true);
                
                originalValues[1] = fields[1].getText();
                originalValues[5] = fields[5].getText();
                originalValues[6] = fields[6].getText();
                originalValues[14] = fields[14].getText();
                originalValues[20] = selectedRoleLabel;
            } else {
                JOptionPane.showMessageDialog(this, "Update Failed.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void setFieldsEditable(boolean active) {
        comboPosition.setEnabled(active);
        comboSupervisor.setEnabled(active);
        comboStatus.setEnabled(active);
        comboRole.setEnabled(active); 
        for (int i = 0; i < fields.length; i++) {
            if (fields[i] == null) continue;
            boolean isEditable = (i == 1 || i == 5 || i == 6 || i == 14);
            if (isEditable) {
                fields[i].setEditable(active);
                fields[i].setBackground(active ? Color.WHITE : new Color(240, 240, 240));
                fields[i].setBorder(active ? BorderFactory.createLineBorder(primaryMaroon) : BorderFactory.createLineBorder(new Color(220, 220, 220)));
            }
        }
    }

    class StringOnlyFilter extends javax.swing.text.DocumentFilter {
        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, javax.swing.text.AttributeSet attrs) throws javax.swing.text.BadLocationException {
            if (text.matches("^[a-zA-Z\\s\\-]*$")) super.replace(fb, offset, length, text, attrs);
        }
    }

    class RoundedImageLabel extends JLabel {
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int size = Math.min(getWidth(), getHeight());
            g2.setClip(new java.awt.geom.Ellipse2D.Double(0, 0, size, size));
            super.paintComponent(g2);
            g2.setClip(null);
            g2.setColor(new Color(200, 200, 200));
            g2.setStroke(new BasicStroke(2));
            g2.drawOval(1, 1, size-2, size-2);
            g2.dispose();
        }
    }

    class StyledButton extends JButton {
        private Color bgColor;
        public StyledButton(String text, Color bg) {
            super(text); this.bgColor = bg; setContentAreaFilled(false);
            setBorderPainted(false); setForeground(Color.WHITE); setFont(labelFont);
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getModel().isPressed() ? bgColor.darker() : bgColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
            super.paintComponent(g2);
            g2.dispose();
        }
    }

    class NumericLimitFilter extends javax.swing.text.DocumentFilter {
        private final int limit;
        public NumericLimitFilter(int limit) { this.limit = limit; }
        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, javax.swing.text.AttributeSet attrs) throws javax.swing.text.BadLocationException {
            String next = fb.getDocument().getText(0, fb.getDocument().getLength()) + text;
            if (next.matches("\\d*\\.?\\d*") && next.length() <= limit) super.replace(fb, offset, length, text, attrs);
        }
    }
}