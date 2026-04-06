package ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Window;
import java.io.File;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import javax.swing.text.AbstractDocument;

import model.Employee;
import model.IAdminOperations;
import service.EmployeeManagementService;
import util.EmployeeDetailForm;

// Updated to extend BasePanel and leverage inherited styling methods
public class EmployeeDatabase extends BasePanel {
    private CardLayout masterlistLayout;
    private JPanel masterlistContainer;
    private DefaultTableModel empTableModel;
    private JTable empTable;
    private TableRowSorter<DefaultTableModel> rowSorter;
    
    private final EmployeeManagementService employeeManagementService;
    private final Employee currentUser;

    // Use values from UIUtils for consistency
    private final Color MOTORPH_MAROON = UIUtils.MOTORPH_MAROON;
    private final Font titleFont = new Font("DM Sans Bold", Font.BOLD, 20);
    private final String REQ_HEX = "#E57373"; 

    public EmployeeDatabase(EmployeeManagementService service, Employee user) {
        super(); // Initializes BasePanel layout, background, and padding
        this.employeeManagementService = service;
        this.currentUser = user;
        
        // Add the main masterlist container to this BasePanel
        add(createMasterlistPanel(), BorderLayout.CENTER);
        refreshData();
    }

    @Override
    public void refreshData() {
        refreshTable();
    }

    private JPanel createMasterlistPanel() {
        masterlistLayout = new CardLayout();
        masterlistContainer = new JPanel(masterlistLayout);
        masterlistContainer.setOpaque(false);

        JPanel tableView = new JPanel(new BorderLayout(20, 20));
        tableView.setOpaque(false);
        tableView.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        JLabel lblTitle = new JLabel("Employee Masterlist");
        lblTitle.setFont(titleFont);
        lblTitle.setForeground(new Color(45, 45, 45));

        JTextField searchField = new JTextField();
        searchField.setPreferredSize(new Dimension(300, 35));
        setupPlaceholder(searchField, "Search by Name, ID, or Position...");
        
        searchField.setBorder(BorderFactory.createCompoundBorder(
            new util.RoundedBorder(15, new Color(210, 210, 210)), 
            BorderFactory.createEmptyBorder(0, 10, 0, 10)
        ));

        topPanel.add(lblTitle, BorderLayout.WEST);
        topPanel.add(searchField, BorderLayout.EAST);

        JPanel tableContainer = createStyledTile();
        tableContainer.setLayout(new BorderLayout());

        String[] columns = {"ID", "Last Name", "First Name", "Status", "Position", "Supervisor"};
        empTableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        empTable = new JTable(empTableModel);
        
        empTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        empTable.setRowHeight(35);
        empTable.setFont(bodyFont);
        empTable.setShowGrid(false);
        empTable.setIntercellSpacing(new Dimension(0, 0));
        empTable.setSelectionBackground(MOTORPH_MAROON);

        JTableHeader tableHeader = empTable.getTableHeader();
        tableHeader.setFont(cardTitleFont); 
        tableHeader.setBackground(Color.WHITE);
        tableHeader.setForeground(Color.GRAY);
        tableHeader.setPreferredSize(new Dimension(0, 40));
        ((DefaultTableCellRenderer)tableHeader.getDefaultRenderer()).setHorizontalAlignment(JLabel.LEFT);

        JScrollPane scrollPane = new JScrollPane(empTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        tableContainer.add(scrollPane, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        btnPanel.setOpaque(false);

        JButton btnAddNew = new JButton("Add New");
        JButton btnView = new JButton("View Details");
        JButton btnDelete = new JButton("Delete");
        
        styleButton(btnAddNew, new Color(45, 45, 45)); 
        styleButton(btnView, MOTORPH_MAROON);        
        styleButton(btnDelete, new Color(180, 0, 0));  

        btnPanel.add(btnAddNew); 
        btnPanel.add(btnView);  
        btnPanel.add(btnDelete);

        tableView.add(topPanel, BorderLayout.NORTH);
        tableView.add(tableContainer, BorderLayout.CENTER);
        tableView.add(btnPanel, BorderLayout.SOUTH);

        rowSorter = new TableRowSorter<>(empTableModel);
        empTable.setRowSorter(rowSorter);

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { filter(); }
            @Override public void removeUpdate(DocumentEvent e) { filter(); }
            @Override public void changedUpdate(DocumentEvent e) { filter(); }
            private void filter() {
                String text = searchField.getText();
                if (text.trim().isEmpty() || text.equals("Search by Name, ID, or Position...")) {
                    rowSorter.setRowFilter(null);
                } else {
                    rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
                }
            }
        });

        btnAddNew.addActionListener(e -> masterlistLayout.show(masterlistContainer, "FORM"));

        btnView.addActionListener(e -> {
            int row = empTable.getSelectedRow();
            if (row != -1) {
                int modelRow = empTable.convertRowIndexToModel(row); 
                try {
                    int empId = Integer.parseInt(empTableModel.getValueAt(modelRow, 0).toString());
                    Object[] rawDetails = employeeManagementService.getEmployeeDetailsForForm(empId);

                    if (rawDetails != null) {
                        Employee selectedEmployee = employeeManagementService.getEmployeeDao().findById(empId);
                        new EmployeeDetailForm(rawDetails, employeeManagementService, currentUser, selectedEmployee).setVisible(true);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error opening details.", "System Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select an employee first to view details.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            }
        });

        btnDelete.addActionListener(e -> {
            int row = empTable.getSelectedRow();
            if (row != -1) {
                int modelRow = empTable.convertRowIndexToModel(row);
                int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this employee record?", "Security Verification", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    int empId = Integer.parseInt(empTableModel.getValueAt(modelRow, 0).toString());
                    if (employeeManagementService.deleteEmployee((IAdminOperations)currentUser, empId)) { 
                        refreshTable(); 
                        JOptionPane.showMessageDialog(this, "Record successfully removed."); 
                    } else {
                        JOptionPane.showMessageDialog(this, "Delete failed: Record is protected or access denied.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select an employee first to delete.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            }
        });

        masterlistContainer.add(tableView, "TABLE");
        masterlistContainer.add(createNewHireFormPanel(), "FORM");
        
        return masterlistContainer;
    }

    // 1. ADD THIS AT THE TOP OF YOUR CLASS (outside the method)
    private byte[] selectedPhotoBytes = null;

    private JPanel createNewHireFormPanel() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(new EmptyBorder(10, 20, 10, 20));

        JLabel formTitle = new JLabel("New Employee Registration");
        formTitle.setFont(titleFont);
        formTitle.setBorder(new EmptyBorder(0, 0, 20, 0));

        JPanel formScrollContainer = new JPanel();
        formScrollContainer.setLayout(new BoxLayout(formScrollContainer, BoxLayout.Y_AXIS));
        formScrollContainer.setOpaque(false);

        JTextField basicSalaryInput = createStyledTextField("0"); applyFilters(13, basicSalaryInput);
        JTextField rice = createStyledTextField("0"); applyFilters(14, rice);
        JTextField pallow = createStyledTextField("0"); applyFilters(15, pallow);
        JTextField cloth = createStyledTextField("0"); applyFilters(16, cloth);

        JPanel personalPanel = createSection("Personal Information", 3, 2);
        JTextField fName = createStyledTextField("Juan"); applyFilters(100, fName); 
        JTextField lName = createStyledTextField("Delacruz"); applyFilters(100, lName); 
        JTextField bday = createStyledTextField("MM/dd/yyyy"); bday.setEditable(false); bday.setBackground(Color.WHITE);

        JButton btnPickBday = new JButton("📅");
        btnPickBday.addActionListener(e -> {
            DatePicker picker = new DatePicker(this); 
            String pickedValue = picker.setPickedDate(); 
            if (pickedValue != null && !pickedValue.isEmpty()) { bday.setText(pickedValue); bday.setForeground(Color.BLACK); }
        });
        JPanel bdayWrapper = new JPanel(new BorderLayout(5, 0)); bdayWrapper.setOpaque(false);
        bdayWrapper.add(bday, BorderLayout.CENTER); bdayWrapper.add(btnPickBday, BorderLayout.EAST);

        JTextField address = createStyledTextField("Address line...");
        JComboBox<String> genderCombo = new JComboBox<>(new String[]{"Male", "Female"});
        JTextField phone = createStyledTextField("0917-123-4567"); applyFilters(5, phone);

        // --- PHOTO UPLOAD SECTION ---
        JButton btnUpload = new JButton("Select Photo");
        JLabel lblPhotoPreview = new JLabel("No Photo Selected");
        btnUpload.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Images", "jpg", "png", "jpeg"));
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
                try {
                    selectedPhotoBytes = java.nio.file.Files.readAllBytes(file.toPath());
                    lblPhotoPreview.setText("Selected: " + file.getName());
                } catch (java.io.IOException ex) {
                    JOptionPane.showMessageDialog(this, "Error reading image file.");
                }
            }
        });

        String ast = " <font color='" + REQ_HEX + "'>*</font>";
        addFormField(personalPanel, "<html>First Name" + ast + "</html>", fName);
        addFormField(personalPanel, "<html>Last Name" + ast + "</html>", lName);
        addFormField(personalPanel, "<html>Birthday" + ast + "</html>", bdayWrapper);
        addFormField(personalPanel, "<html>Address" + ast + "</html>", address);
        addFormField(personalPanel, "Gender:", genderCombo); 
        addFormField(personalPanel, "<html>Phone Number" + ast + "</html>", phone);
        addFormField(personalPanel, "Profile Photo:", btnUpload); // Added Photo Button
        addFormField(personalPanel, "Status:", lblPhotoPreview); // Added Photo Preview

        JPanel govPanel = createSection("Identification & Status", 3, 2);
        JTextField sss = createStyledTextField("00-0000000-0"); applyFilters(6, sss);
        JTextField phil = createStyledTextField("000000000000"); applyFilters(7, phil);
        JTextField tin = createStyledTextField("000-000-000-000"); applyFilters(8, tin);
        JTextField pagibig = createStyledTextField("000000000000"); applyFilters(9, pagibig);
        JComboBox<String> status = new JComboBox<>(new String[]{"Regular", "Probationary"});
        
        addFormField(govPanel, "<html>SSS #" + ast + "</html>", sss);
        addFormField(govPanel, "<html>Philhealth #" + ast + "</html>", phil);
        addFormField(govPanel, "<html>TIN #" + ast + "</html>", tin);
        addFormField(govPanel, "<html>Pag-ibig #" + ast + "</html>", pagibig);
        addFormField(govPanel, "Status:", status);

        JPanel jobPanel = createSection("Employment Details", 2, 2);
        String[] positions = {"Chief Operating Officer", "Chief Finance Officer", "Chief Marketing Officer", "IT Operations and Systems", "HR Manager", "Accounting Head", "Payroll Manager", "Account Manager", "Sales & Marketing", "HR Team Leader", "Payroll Team Leader", "Account Team Leader"};
        JComboBox<String> posCombo = new JComboBox<>(positions);
        JComboBox<String> supervCombo = new JComboBox<>(new String[]{"N/A"});
        
        String[] roles = { 
            model.Role.ADMIN.getLabel(), 
            model.Role.HR_STAFF.getLabel(), 
            model.Role.IT_STAFF.getLabel(), 
            model.Role.ACCOUNTING.getLabel(),
            model.Role.REGULAR_STAFF.getLabel() 
        };
        JComboBox<String> roleCombo = new JComboBox<>(roles);

        posCombo.addActionListener(e -> {
            updatePositionDefaults(posCombo, status, supervCombo, rice, pallow, cloth);
            updateRoleOptionsForPosition(posCombo, roleCombo);
        });

        status.addActionListener(e -> 
            updatePositionDefaults(posCombo, status, supervCombo, rice, pallow, cloth)
        );

        if (posCombo.getItemCount() > 0) {
            posCombo.setSelectedIndex(0);
            updatePositionDefaults(posCombo, status, supervCombo, rice, pallow, cloth);
            updateRoleOptionsForPosition(posCombo, roleCombo);
        }

        addFormField(jobPanel, "Position:", posCombo);
        addFormField(jobPanel, "Supervisor:", supervCombo);
        addFormField(jobPanel, "Access Role:", roleCombo); 

        JPanel financePanel = createSection("Financial Information", 2, 2);
        addFormField(financePanel, "<html>Monthly Basic Salary" + ast + "</html>", basicSalaryInput);
        addFormField(financePanel, "Rice Subsidy:", rice);
        addFormField(financePanel, "Phone Allowance:", pallow);
        addFormField(financePanel, "Clothing Allowance:", cloth);

        formScrollContainer.add(personalPanel);
        formScrollContainer.add(Box.createVerticalStrut(15));
        formScrollContainer.add(govPanel);
        formScrollContainer.add(Box.createVerticalStrut(15));
        formScrollContainer.add(jobPanel);
        formScrollContainer.add(Box.createVerticalStrut(15));
        formScrollContainer.add(financePanel);

        JScrollPane scroll = new JScrollPane(formScrollContainer);
        scroll.setBorder(null); scroll.setOpaque(false); scroll.getViewport().setOpaque(false);

        JPanel bp = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 20));
        bp.setOpaque(false);
        JButton btnBack = new JButton("Cancel"); 
        JButton btnSave = new JButton("Confirm Hire");
        styleButton(btnSave, new Color(0, 102, 51));
        
        btnBack.addActionListener(e -> {
            selectedPhotoBytes = null; // Clear photo on cancel
            lblPhotoPreview.setText("No Photo Selected");
            clearFormFields(fName, lName, bday, address, phone, sss, phil, tin, pagibig, basicSalaryInput, rice, pallow, cloth, genderCombo, status, posCombo, supervCombo, roleCombo);
            masterlistLayout.show(masterlistContainer, "TABLE");
        });

        btnSave.addActionListener(e -> {
            String salaryStr = basicSalaryInput.getText().replaceAll("[^\\d.]", "");
            double salaryVal = salaryStr.isEmpty() ? 0 : Double.parseDouble(salaryStr);

            String validationMessage = validateRegistrationFields(fName, lName, bday, address, phone, sss, phil, tin, pagibig, salaryVal);

            if (validationMessage != null) {
                JOptionPane.showMessageDialog(this, validationMessage, "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String selectedRoleLabel = (String) roleCombo.getSelectedItem();
            Employee newEmp = employeeManagementService.createEmployeeInstance(selectedRoleLabel);
            newEmp.setFirstName(fName.getText().trim());
            newEmp.setLastName(lName.getText().trim());
            newEmp.setGender(genderCombo.getSelectedItem().toString());
            newEmp.setAddress(address.getText().trim());
            newEmp.setPhone(phone.getText().trim());
            newEmp.setSss(sss.getText().trim());
            newEmp.setPhilhealth(phil.getText().trim());
            newEmp.setTin(tin.getText().trim());
            newEmp.setPagibig(pagibig.getText().trim());
            newEmp.setStatus(status.getSelectedItem().toString());
            newEmp.setPosition(posCombo.getSelectedItem().toString());
            newEmp.setSupervisor(supervCombo.getSelectedItem().toString());
            newEmp.setRole(model.Role.fromString(selectedRoleLabel));

            try {
                newEmp.setBirthday(java.time.LocalDate.parse(bday.getText().trim(), java.time.format.DateTimeFormatter.ofPattern("MM/dd/yyyy")));
                newEmp.setBasicSalary(salaryVal);
                newEmp.setRiceSubsidy(Double.parseDouble(rice.getText().replaceAll("[^\\d.]", "")));
                newEmp.setPhoneAllowance(Double.parseDouble(pallow.getText().replaceAll("[^\\d.]", "")));
                newEmp.setClothingAllowance(Double.parseDouble(cloth.getText().replaceAll("[^\\d.]", "")));

                // PASSED selectedPhotoBytes to the service
                if (employeeManagementService.registerEmployee((IAdminOperations) currentUser, newEmp, selectedPhotoBytes)) {
                    String userGen = newEmp.getFirstName().substring(0, 1).toUpperCase() + newEmp.getLastName().substring(0, 1).toUpperCase() + newEmp.getLastName().substring(1).toLowerCase().replaceAll("\\s+", "");
                    JOptionPane.showMessageDialog(this, "Registration Successful!\nID: " + newEmp.getEmpNo() + "\nUsername: " + userGen + "\nPassword: 1234", "Success", JOptionPane.INFORMATION_MESSAGE);
                    selectedPhotoBytes = null; // Clear after success
                    lblPhotoPreview.setText("No Photo Selected");
                    clearFormFields(fName, lName, bday, address, phone, sss, phil, tin, pagibig, basicSalaryInput, rice, pallow, cloth, genderCombo, status, posCombo, supervCombo, roleCombo);
                    refreshTable();
                    masterlistLayout.show(masterlistContainer, "TABLE");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Input Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        bp.add(btnBack);
        bp.add(btnSave);
        wrapper.add(formTitle, BorderLayout.NORTH);
        wrapper.add(scroll, BorderLayout.CENTER);
        wrapper.add(bp, BorderLayout.SOUTH);
        return wrapper;
    }
    private JTextField createStyledTextField(String hint) {
        JTextField f = new JTextField();
        f.setFont(bodyFont);
        setupPlaceholder(f, hint);
        return f;
    }

    private void styleButton(JButton btn, Color bg) {
        btn.setFont(cardTitleFont);
        btn.setForeground(Color.WHITE);
        btn.setBackground(bg);
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void setupPlaceholder(JTextField field, String hint) {
        field.setText(hint);
        field.setForeground(Color.GRAY);
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override public void focusGained(java.awt.event.FocusEvent e) {
                if (field.getText().equals(hint)) { field.setText(""); field.setForeground(Color.BLACK); }
            }
            @Override public void focusLost(java.awt.event.FocusEvent e) {
                if (field.getText().trim().isEmpty()) { field.setForeground(Color.GRAY); field.setText(hint); }
            }
        });
    }

    private void addFormField(JPanel p, String label, JComponent f) {
        JPanel container = new JPanel(new BorderLayout(0, 5)); container.setOpaque(false);
        JLabel lbl = new JLabel(label); lbl.setFont(new Font("DM Sans Medium", Font.PLAIN, 11)); lbl.setForeground(Color.GRAY);
        container.add(lbl, BorderLayout.NORTH); container.add(f, BorderLayout.CENTER); p.add(container);
    }

    public final void refreshTable() {
        if (empTableModel == null) return;
        SwingUtilities.invokeLater(() -> {
            empTableModel.setRowCount(0);
            List<Employee> list = employeeManagementService.getAll();
            if (list != null) for (Employee emp : list) empTableModel.addRow(new Object[]{ emp.getEmpNo(), emp.getLastName(), emp.getFirstName(), emp.getStatus(), emp.getPosition(), emp.getSupervisor() });
        });
    }

    private void applyFilters(int index, JTextField field) {
        AbstractDocument doc = (AbstractDocument) field.getDocument();
        switch (index) {
            case 5: doc.setDocumentFilter(new util.MaskFormatterFilter("####-###-####")); break;
            case 6: doc.setDocumentFilter(new util.MaskFormatterFilter("##-#######-#")); break;
            case 7: case 9: doc.setDocumentFilter(new util.NumericLimitFilter(12)); break;
            case 8: doc.setDocumentFilter(new util.MaskFormatterFilter("###-###-###-###")); break;
            case 13: case 14: case 15: case 16: doc.setDocumentFilter(new util.NumericLimitFilter(7)); break;
            case 100: doc.setDocumentFilter(new javax.swing.text.DocumentFilter() {
                @Override
                public void replace(FilterBypass fb, int offset, int length, String text, javax.swing.text.AttributeSet attrs) throws javax.swing.text.BadLocationException {
                    if (text.matches("[a-zA-Z\\s]*")) super.replace(fb, offset, length, text, attrs);
                }
            }); break;
        }
    }

    private String digitsOnly(String value) { return value == null ? "" : value.replaceAll("\\D", ""); }
    private boolean isValidPhone(String phone) { String digits = digitsOnly(phone); return digits.matches("09\\d{9}"); }
    private boolean isValidSSS(String sss) { return sss != null && sss.matches("\\d{2}-\\d{7}-\\d"); }
    private boolean isValidPhilhealth(String philhealth) { String digits = digitsOnly(philhealth); return digits.matches("\\d{12}"); }
    private boolean isValidTIN(String tin) { String digits = digitsOnly(tin); return digits.matches("\\d{12}"); }
    private boolean isValidPagibig(String pagibig) { String digits = digitsOnly(pagibig); return digits.matches("\\d{12}"); }

    private String validateRegistrationFields(JTextField fName, JTextField lName, JTextField bday, JTextField address, JTextField phone, JTextField sss, JTextField phil, JTextField tin, JTextField pagibig, double salaryVal) {
        boolean isMissingRequired = fName.getText().trim().isEmpty() || fName.getText().equals("Juan") || lName.getText().trim().isEmpty() || lName.getText().equals("Delacruz") || bday.getText().equals("MM/dd/yyyy") || address.getText().trim().isEmpty() || address.getText().equals("Address line...") || phone.getText().trim().isEmpty() || phone.getText().equals("0917-123-4567");
        boolean isMissingGov = sss.getText().equals("00-0000000-0") || phil.getText().equals("000000000000") || tin.getText().equals("000-000-000-000") || pagibig.getText().equals("000000000000");
        if (isMissingRequired || isMissingGov || salaryVal <= 0) return "Registration Failed:\n- All required fields (*) must be filled.\n- Monthly Basic Salary must be greater than 0.";
        if (!isValidPhone(phone.getText().trim())) return "Invalid phone number.\nUse a valid PH mobile format like 0917-123-4567.";
        if (!isValidSSS(sss.getText().trim())) return "Invalid SSS number.\nExpected format: 12-1234567-1";
        if (!isValidPhilhealth(phil.getText().trim())) return "Invalid PhilHealth number.\nIt must contain exactly 12 digits.";
        if (!isValidTIN(tin.getText().trim())) return "Invalid TIN.\nExpected 12 digits, e.g. 123-456-789-000";
        if (!isValidPagibig(pagibig.getText().trim())) return "Invalid Pag-IBIG number.\nIt must contain exactly 12 digits.";
        return null;
    }

    private void clearFormFields(JTextField fName, JTextField lName, JTextField bday, JTextField address, JTextField phone, JTextField sss, JTextField phil, JTextField tin, JTextField pagibig, JTextField basicSalaryInput, JTextField rice, JTextField pallow, JTextField cloth, JComboBox<String> genderCombo, JComboBox<String> statusCombo, JComboBox<String> posCombo, JComboBox<String> supervCombo, JComboBox<String> roleCombo) {
        fName.setText("Juan"); fName.setForeground(Color.GRAY);
        lName.setText("Delacruz"); lName.setForeground(Color.GRAY);
        bday.setText("MM/dd/yyyy"); bday.setForeground(Color.GRAY);
        address.setText("Address line..."); address.setForeground(Color.GRAY);
        phone.setText("0917-123-4567"); phone.setForeground(Color.GRAY);
        sss.setText("00-0000000-0"); sss.setForeground(Color.GRAY);
        phil.setText("000000000000"); phil.setForeground(Color.GRAY);
        tin.setText("000-000-000-000"); tin.setForeground(Color.GRAY);
        pagibig.setText("000000000000"); pagibig.setForeground(Color.GRAY);
        basicSalaryInput.setText("0"); basicSalaryInput.setForeground(Color.GRAY);
        genderCombo.setSelectedIndex(0);
        statusCombo.setSelectedIndex(0);
        posCombo.setSelectedIndex(0);
        updatePositionDefaults(posCombo, statusCombo, supervCombo, rice, pallow, cloth);
        updateRoleOptionsForPosition(posCombo, roleCombo);
    }

    private void updatePositionDefaults(JComboBox<String> posCombo, JComboBox<String> statusCombo, JComboBox<String> supervCombo, JTextField rice, JTextField pallow, JTextField cloth) {
        String selectedPos = (String) posCombo.getSelectedItem();
        String selectedStatus = (String) statusCombo.getSelectedItem();
        if (selectedPos == null) return;
        String[] available = employeeManagementService.getSupervisorsForPosition(selectedPos);
        supervCombo.removeAllItems();
        for (String s : available) supervCombo.addItem(s);
        if ("Probationary".equalsIgnoreCase(selectedStatus)) {
            rice.setText("0"); pallow.setText("0"); cloth.setText("0");
        } else {
            double[] allowances = employeeManagementService.getStandardAllowances(selectedPos);
            rice.setText(String.format("%.0f", allowances[0]));
            pallow.setText(String.format("%.0f", allowances[1]));
            cloth.setText(String.format("%.0f", allowances[2]));
        }
        rice.setForeground(Color.BLACK); pallow.setForeground(Color.BLACK); cloth.setForeground(Color.BLACK);
    }

    private void updateRoleOptionsForPosition(JComboBox<String> posCombo, JComboBox<String> roleCombo) {
        String selectedPos = (String) posCombo.getSelectedItem();
        if (selectedPos == null) return;
        model.Role matchedRole = model.Role.fromString(selectedPos);
        roleCombo.removeAllItems();
        roleCombo.addItem(matchedRole.getLabel());
        roleCombo.setSelectedItem(matchedRole.getLabel());
        roleCombo.setEnabled(false);
    }

    public class DatePicker {
        private int month, year, dayInt = 0; private JDialog d; private JButton[] button = new JButton[42];
        public DatePicker(Component parent) {
            Window pw = SwingUtilities.getWindowAncestor(parent); d = new JDialog(pw instanceof Frame ? (Frame)pw : null, true); d.setTitle("Select Birthdate");
            java.util.Calendar now = java.util.Calendar.getInstance(); month = now.get(java.util.Calendar.MONTH); year = now.get(java.util.Calendar.YEAR) - 18;
            JPanel p1 = new JPanel(new GridLayout(6, 7));
            for (int x = 0; x < button.length; x++) {
                final int s = x; button[x] = new JButton();
                if (x < 7) { String[] h = {"Sun","Mon","Tue","Wed","Thu","Fri","Sat"}; button[x].setText(h[x]); button[x].setEnabled(false); }
                else button[x].addActionListener(e -> { if (!button[s].getText().isEmpty()) { dayInt = Integer.parseInt(button[s].getText()); d.dispose(); } });
                p1.add(button[x]);
            }
            JComboBox<String> mc = new JComboBox<>(new String[]{"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"}); mc.setSelectedIndex(month);
            Integer[] ya = new Integer[80]; for (int i = 0; i < 80; i++) ya[i] = (now.get(java.util.Calendar.YEAR) - 18) - i;
            JComboBox<Integer> yc = new JComboBox<>(ya);
            mc.addActionListener(e -> { month = mc.getSelectedIndex(); displayDate(); }); yc.addActionListener(e -> { year = (Integer)yc.getSelectedItem(); displayDate(); });
            JPanel p2 = new JPanel(); p2.add(mc); p2.add(yc); d.add(p2, BorderLayout.NORTH); d.add(p1, BorderLayout.CENTER); d.pack(); d.setLocationRelativeTo(parent);
            displayDate(); d.setVisible(true);
        }
        public void displayDate() {
            for (int x = 7; x < button.length; x++) button[x].setText("");
            java.util.Calendar cal = java.util.Calendar.getInstance(); cal.set(year, month, 1);
            int dw = cal.get(java.util.Calendar.DAY_OF_WEEK), dim = cal.getActualMaximum(java.util.Calendar.DAY_OF_MONTH);
            for (int x = 6 + dw, dn = 1; dn <= dim; x++, dn++) if (x < button.length) button[x].setText("" + dn);
        }
        public String setPickedDate() { if (dayInt == 0) return ""; java.util.Calendar cal = java.util.Calendar.getInstance(); cal.set(year, month, dayInt); return new java.text.SimpleDateFormat("MM/dd/yyyy").format(cal.getTime()); }
    }
}
