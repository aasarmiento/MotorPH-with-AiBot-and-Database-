package ui;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.RenderingHints;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.format.DateTimeFormatter;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import dao.AttendanceDAO;
import dao.DatabaseConnection;
import dao.ITTicketDAOImpl;
import dao.ITTicketDao;
import dao.UserLibrary;
import model.Attendance;
import model.Employee;
import model.Role;
import service.AiAssistantService;
import service.EmployeeManagementService;
import service.ITSupportService;
import service.LeaveService;
import service.PayrollCalculator; 

public class DashboardPanel extends JFrame {
    private final CardLayout cardLayout;
    private final JPanel cardPanel;
    
    private final EmployeeManagementService employeeService; 
    private final AttendanceDAO attendanceDao;
    private final UserLibrary authService;
    private final Employee currentUser; 

    private dao.EmployeeDAO employeeDao; 

    public LeaveService leaveService; 
    public EmployeeDatabase databasePanel;

    private final AiAssistantService aiService; 

    private final String userRole;
    private final String userEmpNo;
    private final String userLoggedIn;
    private final String userFirstname;
    private final String userLastname;

    private service.EmployeeManagementService employeeManagementService;

    private final Font titleFont = new Font("DM Sans Bold", Font.BOLD, 20);
    private final Font bodyFont = new Font("DM Sans Regular", Font.PLAIN, 14);
    private final Font bodyFontSmall = new Font("DM Sans Regular", Font.PLAIN, 11);
    private final Font cardTitleFont = new Font("DM Sans Bold", Font.BOLD, 12);
    private final Font cardValueFont = new Font("DM Sans Bold", Font.BOLD, 22);

    public JPanel personalInfoPanel;    
    public AddEmployeePanel addEmpPanel;
    public FullDetailsPanel fullEmpPanel;
    public LeaveRequestPanel leaveApp;
    public TimePanel timeEmpPanel;
    
    public JPanel itApprovalPanel;
    public ITSupportPanel itSupportPanel; 
    public LeaveApprovalPanel leaveApprovalPanel; 
    public PayrollFinances payrollFinancePanel; 
    public MyPayslip payslipPanel; 

    private JTextField txtEmpNo, txtLastName, txtFirstName, txtPosition, txtSupervisor;
    private JTextField txtBirthday, txtPhone, txtSss, txtPhilHealth, txtTin, txtPagibig;
    private JTextArea txtAddress;
    private JTextField txtStatus, txtTenured; 
    private JLabel lblProfilePic;

    private JButton btnMyProfile, btnMyPayslip, btnDatabase, btnAttendance, btnLeaveRequest;
    private JButton btnITApproval, btnITSupport, btnLeaveApproval, btnPayrollFinances, btnLogout;

    // FIXED: Removed the double comma and ensured aiService is saved
    public DashboardPanel(EmployeeManagementService empService, AttendanceDAO attDao, UserLibrary auth, Employee user, AiAssistantService aiService) {
        this.employeeService = empService; 
        this.attendanceDao = attDao;
        this.authService = auth;
        this.currentUser = user;
        this.aiService = aiService; 
        
        this.employeeManagementService = empService; 

        this.userRole = user.getRole().name();
        this.userEmpNo = String.valueOf(user.getEmpNo());
        this.userLoggedIn = userEmpNo; 
        this.userFirstname = user.getFirstName();
        this.userLastname = user.getLastName();

        this.employeeDao = new dao.CSVHandler(); 
        
        ITTicketDao itTicketDao = new ITTicketDAOImpl(); 
        ITSupportService itService = new ITSupportService(itTicketDao, employeeService.getEmployeeDao());
        this.leaveService = new LeaveService(employeeService.getEmployeeDao(), attendanceDao);

        service.HRSerbisyo hrSerbisyo = new service.HRSerbisyo(employeeService.getEmployeeDao());

        this.personalInfoPanel = createHomePanel(); 
        this.addEmpPanel = new AddEmployeePanel(employeeService);
        this.fullEmpPanel = new FullDetailsPanel(employeeService, currentUser);
        this.leaveApp = new LeaveRequestPanel(leaveService, currentUser);
        this.timeEmpPanel = new TimePanel(employeeService, currentUser);
        this.databasePanel = new EmployeeDatabase(employeeService, currentUser);
        this.itSupportPanel = new ITSupportPanel(itService, currentUser);
        this.itApprovalPanel = new ITApprovalPanel(itService, currentUser);
        this.leaveApprovalPanel = new LeaveApprovalPanel(leaveService, hrSerbisyo, currentUser);
        
        PayrollCalculator payrollCalc = new PayrollCalculator(); 
        service.PayrollService payrollService = new service.PayrollService(
            employeeService.getEmployeeDao(), 
            this.attendanceDao,           
            payrollCalc, 
            employeeService
        );

        this.payslipPanel = new MyPayslip(employeeService, payrollCalc, payrollService, currentUser);
        this.payrollFinancePanel = new PayrollFinances(employeeService, payrollService, payrollCalc, currentUser);

        setTitle("MotorPH Dashboard - " + userRole); 
        setDefaultCloseOperation(EXIT_ON_CLOSE);                                            
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        
        JPanel homeCenteringWrapper = new JPanel(new GridBagLayout());
        homeCenteringWrapper.setBackground(new Color(245, 245, 245));
        homeCenteringWrapper.add(personalInfoPanel, new GridBagConstraints());
        
        cardPanel.add(homeCenteringWrapper, "Home"); 
        cardPanel.add(payslipPanel, "My_Payslip");
        cardPanel.add(databasePanel, "Database"); 
        cardPanel.add(addEmpPanel, "AddEmployee");
        cardPanel.add(fullEmpPanel, "FullDetails");
        cardPanel.add(leaveApp, "Leave");
        cardPanel.add(timeEmpPanel, "Time");
        cardPanel.add(itApprovalPanel, "IT_Approval");
        cardPanel.add(itSupportPanel, "IT_Support");
        cardPanel.add(leaveApprovalPanel, "Leave_Approval");
        cardPanel.add(payrollFinancePanel, "Payroll_Finances");

        JPanel navPanel = new JPanel();
        navPanel.setBackground(new Color(248, 248, 248)); 
        navPanel.setPreferredSize(new Dimension(220, getHeight()));
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(230, 230, 230)));

        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 25)); 
        logoPanel.setOpaque(false);
        logoPanel.setMaximumSize(new Dimension(220, 80)); 

        try {
            ImageIcon logoIcon = new ImageIcon("resources/Vector.png");
            Image img = logoIcon.getImage();
            double ratio = (double) img.getWidth(null) / img.getHeight(null);
            int targetHeight = 24; 
            int targetWidth = (int) (targetHeight * ratio); 
            Image hdImage = img.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
            logoPanel.add(new JLabel(new ImageIcon(hdImage)));
        } catch (Exception e) {}

        JLabel lblBrand = new JLabel("MotorPH");
        lblBrand.setForeground(new Color(45, 45, 45));
        lblBrand.setFont(titleFont); 
        logoPanel.add(lblBrand);

        navPanel.add(logoPanel);
        navPanel.add(Box.createVerticalStrut(10));

        btnAttendance = new NavButton("Attendance", getDashboardIcon("clock.png"));
        btnMyProfile = new NavButton("My Profile", getDashboardIcon("dashboard.png"));
        btnMyPayslip = new NavButton("My Payslip", getDashboardIcon("MyPayslip.png"));
        btnDatabase = new NavButton("Employee Database", getDashboardIcon("employee_database.png"));
        btnLeaveRequest = new NavButton("Leave Request", getDashboardIcon("LeaveRequest.png"));
        btnITApproval = new NavButton("IT Approval", getDashboardIcon("ITApprroval.png"));
        btnITSupport = new NavButton("IT Support", getDashboardIcon("ITSupport.png"));
        btnLeaveApproval = new NavButton("Leave Approval", getDashboardIcon("LeaveApproval.png"));
        btnPayrollFinances = new NavButton("Payroll & Finances", getDashboardIcon("PayrollFinances.png"));
        btnLogout = new NavButton("Logout", getDashboardIcon("SignOut.png"));

        addNavComponent(navPanel, btnMyProfile);
        addNavComponent(navPanel, btnMyPayslip);
        addNavComponent(navPanel, btnDatabase);
        addNavComponent(navPanel, btnAttendance);
        addNavComponent(navPanel, btnLeaveRequest);
        addNavComponent(navPanel, btnITApproval);
        addNavComponent(navPanel, btnITSupport);
        addNavComponent(navPanel, btnLeaveApproval);
        addNavComponent(navPanel, btnPayrollFinances);
        
        navPanel.add(Box.createVerticalGlue()); 
        addNavComponent(navPanel, btnLogout);
        navPanel.add(Box.createVerticalStrut(40)); 

        btnMyProfile.addActionListener(e -> { refreshDashboardData(); switchPanel("Home"); });
        btnMyPayslip.addActionListener(e -> { if (payslipPanel instanceof BasePanel) ((BasePanel)payslipPanel).refreshData(); switchPanel("My_Payslip"); });
        btnDatabase.addActionListener(e -> { if (databasePanel != null) databasePanel.refreshData(); switchPanel("Database"); });
        btnAttendance.addActionListener(e -> { timeEmpPanel.setLoggedIn(userLoggedIn, userLastname, userFirstname); switchPanel("Time"); });
        btnLeaveRequest.addActionListener(e -> { if (leaveApp instanceof BasePanel) ((BasePanel)leaveApp).refreshData(); switchPanel("Leave"); });       
        
        btnITApproval.addActionListener(e -> { if (itApprovalPanel instanceof BasePanel) ((BasePanel)itApprovalPanel).refreshData(); switchPanel("IT_Approval"); });
        btnITSupport.addActionListener(e -> { if (itSupportPanel instanceof BasePanel) ((BasePanel)itSupportPanel).refreshData(); switchPanel("IT_Support"); });
        btnLeaveApproval.addActionListener(e -> { if (leaveApprovalPanel instanceof BasePanel) ((BasePanel)leaveApprovalPanel).refreshData(); switchPanel("Leave_Approval"); });
        btnPayrollFinances.addActionListener(e -> { if (payrollFinancePanel instanceof BasePanel) ((BasePanel)payrollFinancePanel).refreshData(); switchPanel("Payroll_Finances"); });

        btnLogout.addActionListener(e -> handleLogout());

        add(navPanel, BorderLayout.WEST);
        add(cardPanel, BorderLayout.CENTER);
        add(createFooterPanel(), BorderLayout.SOUTH);

        loadPersonalDetails(currentUser); 
        applyRolePermissions(); 
        switchPanel("Home"); 
        setVisible(true);
    }

    public ImageIcon getDashboardIcon(String name) {
        String sql = "SELECT icon_bytes FROM public.dashboard_assets WHERE icon_name = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    byte[] bt = rs.getBytes("icon_bytes");
                    if (bt != null) return new ImageIcon(bt);
                }
            }
        } catch (Exception e) { System.err.println("Icon missing: " + name); }
        return null;
    }

    private void handleLogout() {
        java.util.List<Attendance> logs = attendanceDao.getAttendanceByEmployee(currentUser.getEmpNo());
        boolean hasActiveSession = logs.stream().anyMatch(log -> log.getTimeIn() != null && log.getTimeOut() == null);
        if (hasActiveSession) {
            JOptionPane.showMessageDialog(this, "Please 'Time Out' before logging out.", "Active Session", JOptionPane.WARNING_MESSAGE);
            return; 
        }
        if (JOptionPane.showConfirmDialog(this, "Logout?", "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            dispose(); 
            // FIXED: Passing aiService back to LoginPanel for persistence
            new LoginPanel(employeeService, attendanceDao, authService, new ITSupportService(new ITTicketDAOImpl(), employeeService.getEmployeeDao()), aiService).setVisible(true);
        }
    }

    public void refreshDashboardData() {
        Employee updatedUser = employeeService.getEmployeeDao().findById(currentUser.getEmpNo());
        this.personalInfoPanel = createHomePanel(); 
        loadPersonalDetails(updatedUser);
        applyRolePermissions(); 
        revalidate();
        repaint();
    }

    private void applyRolePermissions() {
        Role role = currentUser.getRole();
        btnDatabase.setVisible(role == Role.ADMIN);
        btnITApproval.setVisible(role == Role.ADMIN || role == Role.IT_STAFF);
        btnLeaveApproval.setVisible(role == Role.ADMIN || role == Role.HR_STAFF);
        btnPayrollFinances.setVisible(role == Role.ADMIN || role == Role.ACCOUNTING);
    }

    private void addNavComponent(JPanel panel, JButton button) {
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(200, 38)); 
        panel.add(button);
        panel.add(Box.createVerticalStrut(4)); 
    }

    private void switchPanel(String cardName) { 
        cardLayout.show(cardPanel, cardName); 
        repaint(); 
    }

    private JPanel createFooterPanel() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 8));
        footer.setBackground(new Color(128, 0, 0)); 
        footer.setBorder(new EmptyBorder(5, 20, 5, 20));

        JLabel copy = new JLabel("<html><body>Copyright &copy; <b>2026 MotorPH</b></body></html>");
        copy.setFont(bodyFontSmall); 
        copy.setForeground(Color.WHITE); 
        
        JLabel privacy = new JLabel("Privacy Policy");
        privacy.setFont(bodyFontSmall); 
        privacy.setForeground(Color.WHITE);
        privacy.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel terms = new JLabel("Terms and conditions");
        terms.setFont(bodyFontSmall); 
        terms.setForeground(Color.WHITE);
        terms.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel contact = new JLabel("Contact");
        contact.setFont(bodyFontSmall); 
        contact.setForeground(Color.WHITE);
        contact.setCursor(new Cursor(Cursor.HAND_CURSOR));

        footer.add(copy);
        footer.add(Box.createHorizontalStrut(20)); 
        footer.add(privacy);
        footer.add(terms);
        footer.add(contact);
        
        return footer;
    }

    private JPanel createHomePanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBackground(new Color(245, 245, 245));
        mainPanel.setPreferredSize(new Dimension(950, 720));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        double totalHours = employeeService.getTotalHoursForCurrentMonth(currentUser.getEmpNo());
        int leaves = leaveService.getRemainingBalance(currentUser.getEmpNo(), "Vacation Leave") + 
                     leaveService.getRemainingBalance(currentUser.getEmpNo(), "Sick Leave");

        JPanel kpiRow = new JPanel(new GridLayout(1, 3, 20, 0));
        kpiRow.setOpaque(false);
        kpiRow.add(createCircularKPICard("Available Leave", leaves, 30, new Color(255, 173, 173)));
        kpiRow.add(createCircularKPICard("Hours This Month", (int)totalHours, 160, new Color(255, 173, 173)));
        kpiRow.add(createCircularKPICard("Attendance", totalHours > 0 ? 95 : 0, 100, new Color(255, 173, 173)));
        
        JPanel centerColumn = new JPanel(new BorderLayout(0, 20));
        centerColumn.setOpaque(false);

        JPanel profileHeader = createStyledTile();
        profileHeader.setLayout(new BorderLayout(20, 0));
        profileHeader.setPreferredSize(new Dimension(0, 160)); 
        
        lblProfilePic = new RoundedImageLabel(); 
        lblProfilePic.setPreferredSize(new Dimension(110, 110));
        lblProfilePic.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblProfilePic.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                JFileChooser fc = new JFileChooser();
                if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    employeeService.updateEmployeePhoto(currentUser, fc.getSelectedFile());
                    displayEmployeePhoto(lblProfilePic); 
                }
            }
        });

        JPanel photoWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));
        photoWrapper.setOpaque(false); photoWrapper.add(lblProfilePic);
        profileHeader.add(photoWrapper, BorderLayout.WEST);

        JPanel headerText = new JPanel(new GridLayout(2, 2, 20, 5));
        headerText.setOpaque(false);
        txtFirstName = addTransparentField(headerText, "Display Name:");
        txtStatus = addTransparentField(headerText, "Status:"); 
        txtPosition = addTransparentField(headerText, "Current Role:");
        txtTenured = addTransparentField(headerText, "Gender"); 
        profileHeader.add(headerText, BorderLayout.CENTER);

        JPanel gridContainer = new JPanel(new GridLayout(2, 2, 20, 20));
        gridContainer.setOpaque(false);
        
        JPanel pPersonal = createSection("Personal Details", 4);
        txtEmpNo = addTransparentField(pPersonal, "Employee ID:");
        txtLastName = addTransparentField(pPersonal, "Full Name:"); 
        txtBirthday = addTransparentField(pPersonal, "Birthday:");
        txtSupervisor = addTransparentField(pPersonal, "Supervisor:");
        
        JPanel pContact = createSection("Contact Information", 2);
        txtAddress = addTransparentTextArea(pContact, "Address:");
        txtPhone = addTransparentField(pContact, "Contact Number:");

        JPanel pGov = createSection("Government Details", 4);
        txtSss = addTransparentField(pGov, "SSS #:");
        txtPhilHealth = addTransparentField(pGov, "Philhealth #:");
        txtTin = addTransparentField(pGov, "TIN #:");
        txtPagibig = addTransparentField(pGov, "Pagibig #:");

        JPanel pAnnounce = createStyledTile();
        pAnnounce.setBorder(BorderFactory.createTitledBorder(null, "Announcements", 0, 0, cardTitleFont, new Color(128, 0, 0)));
        pAnnounce.add(new JLabel("🎄 Christmas Event - Dec 20th"));

        gridContainer.add(pPersonal); gridContainer.add(pContact);
        gridContainer.add(pGov); gridContainer.add(pAnnounce);
        
        centerColumn.add(profileHeader, BorderLayout.NORTH);
        centerColumn.add(gridContainer, BorderLayout.CENTER);
        mainPanel.add(kpiRow, BorderLayout.NORTH);
        mainPanel.add(centerColumn, BorderLayout.CENTER);


        JButton btnTestAI = new JButton("Chat with AI");
btnTestAI.setBackground(new Color(128, 0, 0));
btnTestAI.setForeground(Color.RED);
btnTestAI.setFocusPainted(false);

      btnTestAI.addActionListener(e -> {
    // 1. Small Input Dialog
    String input = JOptionPane.showInputDialog(this, "How can I help you today?", "MotorPH AI", JOptionPane.QUESTION_MESSAGE);
    
    if (input != null && !input.isEmpty()) {
        String response = aiService.askHrBot(currentUser.getEmpNo(), input);

        // 2. Create a custom SMALL window for the response
        JTextArea textArea = new JTextArea(response);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setEditable(false);
        textArea.setFont(new Font("DM Sans Regular", Font.PLAIN, 13));
        textArea.setBackground(new Color(245, 245, 245));

        // Set the size of the text area (Width: 350, Height: 150)
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(350, 150));
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        // Show the small formatted dialog
        JOptionPane.showMessageDialog(this, scrollPane, "AI Assistant", JOptionPane.INFORMATION_MESSAGE);
    }
});

mainPanel.add(btnTestAI, BorderLayout.SOUTH);

        return mainPanel;
    }

    private JPanel createSection(String title, int rows) {
        JPanel p = createStyledTile();
        p.setBorder(BorderFactory.createTitledBorder(null, title, 0, 0, cardTitleFont, new Color(128, 0, 0)));
        p.setLayout(new GridLayout(rows, 1, 0, 5));
        return p;
    }

    private void loadPersonalDetails(Employee emp) {
        if (emp == null || txtEmpNo == null) return; 
        DateTimeFormatter df = DateTimeFormatter.ofPattern("M/d/yyyy");
        txtFirstName.setText(emp.getFirstName() + " " + emp.getLastName());
        txtPosition.setText(emp.getPosition());
        txtEmpNo.setText(String.valueOf(emp.getEmpNo()));
        txtLastName.setText(emp.getFirstName() + " " + emp.getLastName());
        if (emp.getBirthday() != null) txtBirthday.setText(emp.getBirthday().format(df));
        txtSupervisor.setText(emp.getSupervisor());
        txtAddress.setText(emp.getAddress());
        txtPhone.setText(emp.getPhone());
        txtSss.setText(emp.getSss());
        txtPhilHealth.setText(emp.getPhilhealth());
        txtTin.setText(emp.getTin());
        txtPagibig.setText(emp.getPagibig());
        txtStatus.setText(emp.getStatus()); 
        txtTenured.setText(emp.getGender());
        displayEmployeePhoto(lblProfilePic);

        
    }

    private void displayEmployeePhoto(JLabel lblPhoto) {
        if (employeeManagementService == null) return;
        int empId = currentUser.getEmpNo();
        ImageIcon icon = employeeManagementService.getEmployeePhoto(empId, 110, 110);
        
        if (icon != null) {
            lblPhoto.setIcon(icon);
            lblPhoto.setText(""); 
        } else {
            lblPhoto.setText("No Image");
        }
    }

    private void loadDefaultPhoto(JLabel lblPhoto) {
        String defaultPath = "src/main/resources/profile_pics/default.png";
        ImageIcon defaultIcon = new ImageIcon(defaultPath);
        Image img = defaultIcon.getImage().getScaledInstance(110, 110, Image.SCALE_SMOOTH);
        lblPhoto.setIcon(new ImageIcon(img));
    }

    private JPanel createStyledTile() {
        JPanel panel = new JPanel() {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
                g2.setColor(new Color(225, 225, 225));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
                g2.dispose();
            }
        };
        panel.setBackground(new Color(248, 248, 248));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        return panel;
    }

    private JTextField addTransparentField(JPanel panel, String labelText) {
        JPanel container = new JPanel(new BorderLayout()); container.setOpaque(false);
        JLabel label = new JLabel(labelText); label.setFont(cardTitleFont); label.setForeground(Color.GRAY);
        JTextField field = new JTextField(); field.setEditable(false); field.setBorder(null); field.setOpaque(false);
        field.setFont(bodyFont); container.add(label, BorderLayout.NORTH); container.add(field, BorderLayout.CENTER);
        panel.add(container); return field;
    }

    private JTextArea addTransparentTextArea(JPanel panel, String labelText) {
        JPanel container = new JPanel(new BorderLayout()); container.setOpaque(false);
        JLabel label = new JLabel(labelText); label.setFont(cardTitleFont); label.setForeground(Color.GRAY);
        JTextArea area = new JTextArea(); area.setEditable(false); area.setLineWrap(true); area.setOpaque(false);
        area.setFont(bodyFont); container.add(label, BorderLayout.NORTH); container.add(area, BorderLayout.CENTER);
        panel.add(container); return area;
    }

    private JPanel createCircularKPICard(String title, int current, int total, Color bgColor) {
        JPanel card = createStyledTile(); card.setBackground(bgColor);
        card.setLayout(new BorderLayout(15, 0));
        JPanel textPanel = new JPanel(); textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        JLabel lblValue = new JLabel(title.equals("Attendance") ? current + "%" : current + "/" + total);
        lblValue.setFont(cardValueFont); lblValue.setForeground(new Color(128, 0, 0));
        textPanel.add(new JLabel(title)); textPanel.add(lblValue);
        card.add(textPanel, BorderLayout.CENTER);
        return card;
    }

    class NavButton extends JButton {
        private final Color hoverBg = new Color(128, 0, 0); 
        private final Color normalText = new Color(85, 85, 85); 
        private final Color hoverText = Color.WHITE;

        public NavButton(String text, ImageIcon icon) {
            setLayout(new FlowLayout(FlowLayout.LEFT, 15, 8));
            setContentAreaFilled(false); setBorderPainted(false); setFocusPainted(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR)); 
            
            if (icon != null) {
                JLabel iconLbl = new JLabel(new ImageIcon(icon.getImage().getScaledInstance(18, 18, Image.SCALE_SMOOTH)));
                add(iconLbl);
            }
            JLabel textLbl = new JLabel(text);
            textLbl.setForeground(normalText);
            add(textLbl);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            if (getModel().isRollover() || getModel().isPressed() || isFocusOwner()) {
                g2.setColor(hoverBg);
                g2.fillRoundRect(8, 2, getWidth() - 16, getHeight() - 4, 20, 20);
                updateChildColors(hoverText);
            } else { 
                updateChildColors(normalText); 
            }
            g2.dispose();
            super.paintComponent(g);
        }

        private void updateChildColors(Color color) {
            for (Component c : getComponents()) {
                if (c instanceof JLabel) c.setForeground(color);
            }
        }
    }

    class RoundedImageLabel extends JLabel {
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int d = Math.min(getWidth(), getHeight()) - 4;
            g2.setClip(new java.awt.geom.Ellipse2D.Double(2, 2, d, d));
            super.paintComponent(g2);
            g2.setClip(null);
            g2.setColor(new Color(128, 0, 0));
            g2.setStroke(new BasicStroke(2f));
            g2.drawOval(2, 2, d, d);
            g2.dispose();
        }
    }
}