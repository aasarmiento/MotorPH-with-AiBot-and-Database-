package util;

import dao.AttendanceDAO;
import dao.EmployeeDAO;
import dao.UserLibrary;
import java.awt.*;
import javax.swing.*;
import model.Employee;
import service.EmployeeManagementService;
import ui.LoginPanel;

public abstract class BaseDashboard extends JFrame {
    protected CardLayout cardLayout = new CardLayout();
    protected JPanel cardPanel = new JPanel(cardLayout);
    protected JPanel navPanel = new JPanel();
    
    protected final EmployeeDAO dao;
    protected final Employee user;
    
    protected EmployeeManagementService employeeService;
    protected AttendanceDAO attendanceDao;
    protected UserLibrary authService;

    protected JButton btnDatabase, btnAddEmployee, btnAttendance, btnProfile, btnLeave, btnFullDetails;

    public BaseDashboard(EmployeeDAO dao, Employee user) {
        this.dao = dao;
        this.user = user;
        
        setTitle("MotorPH Admin Dashboard");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        initializeNavButtons();
        setupNavigationPanel();
        
        add(navPanel, BorderLayout.WEST);
        add(cardPanel, BorderLayout.CENTER);
    }

    private void initializeNavButtons() {
      
        btnDatabase = new JButton("Employee Database");
        btnFullDetails = new JButton("View Full Details"); 
        btnAddEmployee = new JButton("Add Employee");
        btnAttendance = new JButton("Time");
        btnProfile = new JButton("My Profile");
        btnLeave = new JButton("Leave Application");
        
       
        JButton[] allButtons = {
            btnDatabase, 
            btnFullDetails, 
            btnAddEmployee, 
            btnAttendance, 
            btnProfile, 
            btnLeave
        };
        
        
        for (JButton btn : allButtons) {
            if (btn != null) { 
                btn.setAlignmentX(Component.CENTER_ALIGNMENT);
                btn.setMaximumSize(new Dimension(190, 40));
                btn.setFocusable(false);
            }
        }
    }

    private void setupNavigationPanel() {
        navPanel.setBackground(new Color(128, 0, 0)); // Dark Red
        navPanel.setPreferredSize(new Dimension(220, getHeight()));
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));

        navPanel.add(Box.createVerticalStrut(30));
        
        JLabel welcomeLabel = new JLabel("Welcome to MOTORPH,");
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setFont(new Font("Tahoma", Font.PLAIN, 12));
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        navPanel.add(welcomeLabel);

        JLabel nameLabel = new JLabel(user.getFirstName() + "!");
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        navPanel.add(nameLabel);

        navPanel.add(Box.createVerticalStrut(10));

        JLabel adminTitle = new JLabel("Admin Dashboard");
        adminTitle.setForeground(Color.WHITE);
        adminTitle.setFont(new Font("Tahoma", Font.BOLD, 20));
        adminTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        navPanel.add(adminTitle);

        navPanel.add(Box.createVerticalStrut(40));

        addRoleSpecificComponents();

        navPanel.add(Box.createVerticalGlue());
        
        JButton btnLogout = new JButton("Log out");
        btnLogout.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogout.setMaximumSize(new Dimension(190, 40));
        btnLogout.addActionListener(e -> {
            dispose();
            // FIX: Pass the required services back to the LoginPanel
            new LoginPanel(employeeService, attendanceDao, authService).setVisible(true); 
        });
        
        navPanel.add(btnLogout);
        navPanel.add(Box.createVerticalStrut(20));
    }

    protected final void switchPanel(JPanel panel) {
        cardPanel.removeAll();
        cardPanel.add(panel, "Current");
        cardLayout.show(cardPanel, "Current");
        cardPanel.revalidate();
        cardPanel.repaint();
    }

    protected abstract void addRoleSpecificComponents();
    
    public void render() {
        addRoleSpecificComponents(); 
        revalidate();
        repaint();
        setVisible(true); 
    }
}