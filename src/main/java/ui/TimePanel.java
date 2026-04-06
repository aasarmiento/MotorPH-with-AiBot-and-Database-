package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import model.Employee;
import service.EmployeeManagementService;

public class TimePanel extends BasePanel {
    private DefaultTableModel model;
    private JComboBox<String> monthPicker, yearPicker;
    private JButton btnIn, btnOut;
    
    private final EmployeeManagementService service;
    private final Employee currentUser;

    private final Color MOTORPH_GREEN = new Color(40, 167, 69); 

    public TimePanel(EmployeeManagementService service, Employee user) {
        super(); 
        this.service = service;
        this.currentUser = user;
        
        
        add(createTimeTrackingPanel(), BorderLayout.CENTER);
        
        refreshData(); 
    }

   
    @Override
    public void refreshData() {
        refreshUI();
    }

    public void setLoggedIn(String id, String lastName, String firstName) {
        refreshUI();
    }

    private JPanel createTimeTrackingPanel() {
        JPanel mainWrapper = new JPanel(new BorderLayout(20, 20));
        mainWrapper.setOpaque(false);
        mainWrapper.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel headerActions = new JPanel(new BorderLayout());
        headerActions.setOpaque(false);

        JLabel lblTitle = new JLabel("Attendance Logs");
        lblTitle.setFont(new Font("DM Sans Bold", Font.BOLD, 20));
        lblTitle.setForeground(new Color(45, 45, 45));

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        controls.setOpaque(false);

        monthPicker = new JComboBox<>(new String[]{"All", "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"});
        yearPicker = new JComboBox<>(new String[]{"All", "2024", "2025", "2026"});
        yearPicker.setSelectedItem("2026");
        
        btnIn = new JButton("Check In");
        btnOut = new JButton("Check Out");
        
        styleActionButton(btnIn, MOTORPH_GREEN); 
        styleActionButton(btnOut, motorPHRed); // Using inherited motorPHRed

        controls.add(new JLabel("Month:"));
        controls.add(monthPicker);
        controls.add(new JLabel("Year:"));
        controls.add(yearPicker);
        controls.add(Box.createHorizontalStrut(10));
        controls.add(btnIn); 
        controls.add(btnOut);

        headerActions.add(lblTitle, BorderLayout.WEST);
        headerActions.add(controls, BorderLayout.EAST);

        JPanel tableContainer = createStyledTile();
        tableContainer.setLayout(new BorderLayout());

        model = new DefaultTableModel(new String[]{"Date", "Log In", "Log Out"}, 0);
        JTable table = new JTable(model);
        
        table.setRowHeight(35);
        table.setFont(bodyFont); 
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(motorPHRed);

        JTableHeader tableHeader = table.getTableHeader();
        tableHeader.setFont(cardTitleFont); 
        tableHeader.setBackground(Color.WHITE);
        tableHeader.setForeground(Color.GRAY);
        tableHeader.setPreferredSize(new Dimension(0, 40));
        
        ((DefaultTableCellRenderer)tableHeader.getDefaultRenderer()).setHorizontalAlignment(JLabel.LEFT);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        
        tableContainer.add(scrollPane, BorderLayout.CENTER);

        btnIn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Proceed with Check In?", "MotorPH Attendance", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                service.recordTimeLog(currentUser.getEmpNo(), "Check-in");
                refreshUI();
            }
        });

        btnOut.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Proceed with Check Out?", "MotorPH Attendance", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                service.recordTimeLog(currentUser.getEmpNo(), "Check-out");
                refreshUI(); 
            }
        });

        monthPicker.addActionListener(e -> refreshUI());
        yearPicker.addActionListener(e -> refreshUI());

        mainWrapper.add(headerActions, BorderLayout.NORTH);
        mainWrapper.add(tableContainer, BorderLayout.CENTER);
        
        return mainWrapper;
    }

    private void refreshUI() {
        if (monthPicker == null || yearPicker == null) return;
        
        model.setRowCount(0);
        String month = (String) monthPicker.getSelectedItem();
        String year = (String) yearPicker.getSelectedItem();
        
        Object[][] data = service.getAttendanceLogs(currentUser.getEmpNo(), month, year);
        if (data != null) {
            for (Object[] row : data) {
                model.addRow(row);
            }
        }

        boolean[] states = service.getButtonStates(currentUser.getEmpNo());
        if (states != null && states.length >= 2) {
            btnIn.setEnabled(states[0]);
            btnOut.setEnabled(states[1]);
        }
        
        this.revalidate();
        this.repaint();
    }

    private void styleActionButton(JButton btn, Color bg) {
        btn.setFont(cardTitleFont); 
        btn.setForeground(Color.WHITE);
        btn.setBackground(bg);
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setFocusPainted(false);
        
        btn.setMargin(new Insets(8, 20, 8, 20));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) { 
                if(btn.isEnabled()) btn.setBackground(bg.brighter()); 
            }
            public void mouseExited(java.awt.event.MouseEvent evt) { 
                btn.setBackground(bg); 
            }
        });
    }
}