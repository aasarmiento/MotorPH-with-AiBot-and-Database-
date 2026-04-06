package ui;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.Timer;

import dao.AttendanceDAO;
import dao.ITTicketDAOImpl;
import dao.UserLibrary;
import model.Employee;
import service.AiAssistantService;
import service.EmployeeManagementService;
import service.ITSupportService;
import service.LeaveService; // <--- ADDED THIS IMPORT

public class LoginPanel extends JFrame {

    private AiAssistantService aiService; 
    private final EmployeeManagementService employeeService; 
    private final AttendanceDAO attendanceDao; 
    private final UserLibrary authService;
    private final ITSupportService itSupportService;

    private JTextField empField;
    private JPasswordField passField;
    private int loginAttempts = 0;

    private final Color BRAND_MAROON = new Color(88, 16, 16);
    private final Color INPUT_BG = new Color(248, 249, 250);
    
    private float leftPanelOpacity = 0.0f;
    private int verticalOffset = 40; 

    // PRIMARY CONSTRUCTOR
    public LoginPanel(EmployeeManagementService service, AttendanceDAO dao, UserLibrary auth, ITSupportService itSupportService, AiAssistantService aiService) {
        this.employeeService = service;
        this.attendanceDao = dao;
        this.authService = auth;
        this.itSupportService = itSupportService;
        this.aiService = aiService;
        initializeUI();
    }

    // SECONDARY CONSTRUCTOR - FIXED TO MATCH NEW AiAssistantService REQUIREMENTS
    public LoginPanel(EmployeeManagementService service, AttendanceDAO dao, UserLibrary auth) {
        this(
            service,
            dao,
            auth,
            new ITSupportService(new ITTicketDAOImpl(), service.getEmployeeDao()),
            new AiAssistantService(
                service.getEmployeeDao(), 
                dao, 
                new LeaveService(service.getEmployeeDao(), dao) // <--- FIXED: Added LeaveService here
            )
        );
    }

    private void initializeUI() {
        setTitle("MotorPH Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 550); 
        setLocationRelativeTo(null);
        setLayout(new GridLayout(1, 2)); 

        add(createLeftPanel());
        add(createRightPanel());
        
        if (empField != null) {
            empField.requestFocusInWindow();
        }
    }

    private JPanel createLeftPanel() {
        JPanel leftPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, leftPanelOpacity));
                super.paintComponent(g2d);
                g2d.dispose();
            }
        };
        leftPanel.setBackground(BRAND_MAROON);
        startEntryAnimation(leftPanel);
        return leftPanel;
    }

    private void updateLeftPanelContent(JPanel panel) {
        panel.removeAll();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.CENTER;

        try {
            // Pulls the logo you uploaded (the 9KB file) from the DB
            ImageIcon logoIcon = employeeService.getEmployeeDao().getDashboardIcon("logo.png");

            if (logoIcon != null) {
                Image scaled = logoIcon.getImage().getScaledInstance(160, -1, Image.SCALE_SMOOTH);
                gbc.gridy = 0;
                gbc.insets = new Insets(verticalOffset, 0, 25, 0); 
                panel.add(new JLabel(new ImageIcon(scaled)), gbc);
            } else {
                JLabel fallback = new JLabel("MotorPH");
                fallback.setFont(new Font("SansSerif", Font.BOLD, 40));
                fallback.setForeground(Color.WHITE);
                gbc.gridy = 0;
                gbc.insets = new Insets(verticalOffset, 0, 25, 0);
                panel.add(fallback, gbc);
            }
        } catch (Exception e) {
            System.err.println("Logo loading error: " + e.getMessage());
        }

        JLabel welcomeLabel = new JLabel("Welcome to MotorPH");
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel.add(welcomeLabel, gbc);

        JLabel subLabel = new JLabel("The Filipino's Choice");
        subLabel.setForeground(new Color(220, 220, 220));
        subLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        gbc.gridy = 2;
        panel.add(subLabel, gbc);
        panel.revalidate();
    }

    private JPanel createRightPanel() {
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 60, 5, 60); 
        gbc.gridx = 0;

        JLabel header = new JLabel("Welcome Back");
        header.setFont(new Font("SansSerif", Font.BOLD, 26));
        gbc.gridy = 0;
        rightPanel.add(header, gbc);

        JLabel subHeader = new JLabel("Log in to your MotorPH dashboard");
        subHeader.setForeground(Color.GRAY);
        subHeader.setFont(new Font("SansSerif", Font.PLAIN, 14));
        gbc.gridy = 1;
        gbc.insets = new Insets(5, 60, 35, 60); 
        rightPanel.add(subHeader, gbc);

        gbc.insets = new Insets(5, 60, 5, 60);
        gbc.gridy = 2;
        JLabel userLabel = new JLabel("Username");
        userLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        rightPanel.add(userLabel, gbc);
        
        empField = new JTextField();
        styleField(empField);
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 60, 20, 60); 
        rightPanel.add(empField, gbc);

        gbc.insets = new Insets(5, 60, 5, 60);
        gbc.gridy = 4;
        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        rightPanel.add(passLabel, gbc);

        passField = new JPasswordField();
        styleField(passField);
        gbc.gridy = 5;
        gbc.insets = new Insets(0, 60, 8, 60);
        rightPanel.add(passField, gbc);

        JPanel options = new JPanel(new BorderLayout());
        options.setBackground(Color.WHITE);
        JCheckBox showPass = new JCheckBox("Show Password");
        showPass.setBackground(Color.WHITE);
        showPass.setFont(new Font("SansSerif", Font.PLAIN, 12));
        showPass.addActionListener(e -> passField.setEchoChar(showPass.isSelected() ? (char)0 : '•'));
        
        JButton forgotBtn = new JButton("Forgot Password?");
        forgotBtn.setFont(new Font("SansSerif", Font.BOLD, 12));
        forgotBtn.setForeground(BRAND_MAROON);
        forgotBtn.setBorderPainted(false);
        forgotBtn.setContentAreaFilled(false);
        forgotBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        forgotBtn.addActionListener(e -> handleForgotPassword());
        
        options.add(showPass, BorderLayout.WEST);
        options.add(forgotBtn, BorderLayout.EAST);
        gbc.gridy = 6;
        gbc.insets = new Insets(0, 60, 30, 60); 
        rightPanel.add(options, gbc);

        JButton loginBtn = new JButton("Log In");
        loginBtn.setBackground(BRAND_MAROON);
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFont(new Font("SansSerif", Font.BOLD, 15));
        loginBtn.setOpaque(true);
        loginBtn.setBorderPainted(false);
        loginBtn.setPreferredSize(new Dimension(0, 50)); 
        loginBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        Color HOVER_COLOR = BRAND_MAROON.brighter();
        loginBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { animateHover(loginBtn, HOVER_COLOR, true); }
            @Override
            public void mouseExited(MouseEvent e) { animateHover(loginBtn, BRAND_MAROON, false); }
            @Override
            public void mousePressed(MouseEvent e) { loginBtn.setBackground(BRAND_MAROON.darker()); }
        });

        loginBtn.addActionListener(e -> handleLogin());
        gbc.gridy = 7;
        gbc.insets = new Insets(10, 60, 20, 60);
        rightPanel.add(loginBtn, gbc);
        this.getRootPane().setDefaultButton(loginBtn);

        return rightPanel;
    }

    private void styleField(JTextField field) {
        field.setPreferredSize(new Dimension(0, 42)); 
        field.setBackground(INPUT_BG);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(210, 210, 210)),
            BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
    }

    private void handleLogin() {
        String username = empField.getText().trim();
        String password = new String(passField.getPassword()).trim();

        if (authService.authenticate(username, password)) {
            Employee user = authService.getLoggedInEmployee();
            if (user != null) {
                if ("PASSWORD_RESET_REQUIRED".equalsIgnoreCase(user.getStatus())) {
                    if (!promptForNewPassword(user)) return;
                }
                navigateToDashboard(user);
            } else {
                handleFailedAttempt();
            }
        } else {
            handleFailedAttempt();
        }
    }

    private void navigateToDashboard(Employee user) {
        this.dispose(); 
        new DashboardPanel(employeeService, attendanceDao, authService, user, aiService).setVisible(true);
    }

    private void handleFailedAttempt() {
        loginAttempts++;
        shakeComponent(empField);
        shakeComponent(passField);
        passField.setBorder(BorderFactory.createLineBorder(Color.RED));
        passField.setText(""); 
        if (loginAttempts >= 3) {
            JOptionPane.showMessageDialog(this, "Too many failed attempts. Closing.");
            System.exit(0);
        }
        JOptionPane.showMessageDialog(this, "Invalid credentials. Attempts left: " + (3 - loginAttempts));
        styleField(passField); 
    }

    private void handleForgotPassword() {
        String username = empField.getText().trim();
        if (username.isEmpty()) {
            username = JOptionPane.showInputDialog(this, "Enter your username:");
        }
        if (username == null || username.trim().isEmpty()) return;

        boolean created = itSupportService.submitForgotPasswordTicket(username.trim());
        if (created) {
            JOptionPane.showMessageDialog(this, "Request submitted. IT will email you shortly.");
        } else {
            JOptionPane.showMessageDialog(this, "Request failed. Check username or existing tickets.", "Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    private boolean promptForNewPassword(Employee user) {
        while (true) {
            JPasswordField newPassField = new JPasswordField();
            JPasswordField confirmField = new JPasswordField();
            JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
            panel.add(new JLabel("Enter a new password (min 6 chars):"));
            panel.add(newPassField);
            panel.add(new JLabel("Confirm new password:"));
            panel.add(confirmField);

            int result = JOptionPane.showConfirmDialog(this, panel, "Set New Password", JOptionPane.OK_CANCEL_OPTION);
            if (result != JOptionPane.OK_OPTION) return false;

            String newPass = new String(newPassField.getPassword()).trim();
            if (newPass.length() < 6) {
                JOptionPane.showMessageDialog(this, "Too short.");
                continue;
            }
            if (!newPass.equals(new String(confirmField.getPassword()).trim())) {
                JOptionPane.showMessageDialog(this, "Mismatch.");
                continue;
            }

            employeeService.getEmployeeDao().saveNewPassword(user.getEmpNo(), newPass);
            employeeService.getEmployeeDao().updateEmployeeStatus(user.getEmpNo(), "Active");
            user.setStatus("Active");
            return true;
        }
    }

    private void startEntryAnimation(JPanel panel) {
        Timer entryTimer = new Timer(20, null);
        entryTimer.addActionListener(e -> {
            verticalOffset -= 2;
            leftPanelOpacity = Math.min(1.0f, leftPanelOpacity + 0.05f);
            updateLeftPanelContent(panel);
            panel.repaint();
            if (verticalOffset <= 0) ((Timer)e.getSource()).stop();
        });
        entryTimer.start();
    }

    private void shakeComponent(JComponent component) {
        final Point point = component.getLocation();
        Timer timer = new Timer(20, null);
        timer.addActionListener(new java.awt.event.ActionListener() {
            private int count = 0;
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                if (count >= 10) {
                    component.setLocation(point);
                    timer.stop();
                } else {
                    int offset = (count % 2 == 0) ? 10 : -10;
                    component.setLocation(point.x + offset, point.y);
                    count++;
                }
            }
        });
        timer.start();
    }

    private void animateHover(JButton button, Color targetColor, boolean entering) {
        button.setBackground(targetColor);
    }
}