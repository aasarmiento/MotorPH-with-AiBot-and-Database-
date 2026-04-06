package ui;

import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import model.Employee;
import model.ITStaff; 
import model.ITTicket;
import model.Role;
import service.ITSupportService;


public class ITApprovalPanel extends BasePanel {
    private final ITSupportService itService;
    private final Employee currentUser;
    private JTable table;
    private DefaultTableModel model;
    private JButton btnResolve;
    private JButton btnGenerateTempPassword;

    private final Color primaryMaroon = UIUtils.MOTORPH_MAROON;
    private final Color bgColor = UIUtils.BG_LIGHT;
    private final Color tileBg = Color.WHITE;
    private final Font titleFont = new Font("DM Sans Bold", Font.BOLD, 20);
    private final Font cardTitleFont = UIUtils.FONT_LABEL;
    private final Font bodyFont = UIUtils.FONT_BODY;

    public ITApprovalPanel(ITSupportService service, Employee user) {
        super(); 
        this.itService = service;
        this.currentUser = user;
        
        setBorder(new EmptyBorder(30, 40, 30, 40));

        String role = currentUser.getRole().name();
        if (!role.equalsIgnoreCase("IT_STAFF") && !role.equalsIgnoreCase("ADMIN")) {
            showAccessDenied();
            return; 
        }

        initFullUI();
        refreshData();
    }

    
    @Override
    public void refreshData() {
        refreshUI();
    }

    private void initFullUI() {
        setLayout(new BorderLayout(20, 20));
        
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        JLabel lblTitle = new JLabel("IT Ticket Approval & Management");
        lblTitle.setFont(titleFont);
        lblTitle.setForeground(new Color(45, 45, 45));
        headerPanel.add(lblTitle, BorderLayout.WEST);
        
        headerPanel.add(createStatusLegend(), BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        JPanel mainCard = createStyledTile("");
        mainCard.setLayout(new BorderLayout(0, 15));

        String[] columns = {"ID", "Emp No", "User", "Issue Type", "Status", "Created At"};
        model = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                String issueType = table.getValueAt(table.getSelectedRow(), 3).toString();
                String status = table.getValueAt(table.getSelectedRow(), 4).toString();

                boolean isForgotPassword = "Forgot Password".equalsIgnoreCase(issueType);
                boolean isResolved = "RESOLVED".equalsIgnoreCase(status);

                boolean canResolve = (currentUser instanceof ITStaff)
                        || currentUser.getRole() == Role.ADMIN;

                btnGenerateTempPassword.setEnabled(canResolve && isForgotPassword && !isResolved);
                btnResolve.setEnabled(canResolve && !isForgotPassword && !isResolved);
            }
        });

        
        styleTable();

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        mainCard.add(scrollPane, BorderLayout.CENTER);

        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footerPanel.setOpaque(false);

        btnGenerateTempPassword = new StyledButton("Generate Temp Password", new Color(102, 51, 153));
        btnGenerateTempPassword.setPreferredSize(new Dimension(220, 40));
        btnGenerateTempPassword.addActionListener(e -> handleGenerateTempPassword());

        btnResolve = new StyledButton("Mark as Resolved", new Color(0, 102, 51));
        btnResolve.setPreferredSize(new Dimension(180, 40));
        btnResolve.addActionListener(e -> handleResolve());

        footerPanel.add(btnGenerateTempPassword);
        footerPanel.add(btnResolve);

        mainCard.add(footerPanel, BorderLayout.SOUTH);
        add(mainCard, BorderLayout.CENTER);
    }
    
    public void refreshUI() {
        if (model == null) return;
        model.setRowCount(0);
        
        List<ITTicket> tickets = itService.getAllTickets(); 
        for (ITTicket t : tickets) {
            model.addRow(new Object[]{
                t.getTicketId(),
                t.getEmployeeNo(),
                t.getFullName(),
                t.getIssueType(),
                t.getStatus(),
                t.getCreatedAt()
            });
        }
                
        boolean canResolve = (currentUser instanceof ITStaff)
        || currentUser.getRole() == Role.ADMIN;

            if (btnResolve != null) {
                btnResolve.setEnabled(false);
                if (!canResolve) {
                    btnResolve.setToolTipText("Access Restricted: Only IT Staff or Admin can resolve tickets.");
                } else {
                    btnResolve.setToolTipText(null);
                }
            }

            if (btnGenerateTempPassword != null) {
                btnGenerateTempPassword.setEnabled(false);
                if (!canResolve) {
                    btnGenerateTempPassword.setToolTipText("Access Restricted: Only IT Staff or Admin can generate temporary passwords.");
                } else {
                    btnGenerateTempPassword.setToolTipText(null);
                }
            }
    }

    private void handleResolve() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a ticket to resolve.");
            return;
        }

        String ticketId = table.getValueAt(row, 0).toString();
        String issueType = table.getValueAt(row, 3).toString();
        String status = table.getValueAt(row, 4).toString();

        if ("Forgot Password".equalsIgnoreCase(issueType)) {
            JOptionPane.showMessageDialog(this, "Use the Generate Temp Password button for forgot password tickets.");
            return;
        }

        if ("RESOLVED".equalsIgnoreCase(status)) {
            JOptionPane.showMessageDialog(this, "This ticket is already resolved.");
            return;
        }

        try {
            int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to resolve Ticket #" + ticketId + "?",
                "Confirm Resolution",
                JOptionPane.YES_NO_OPTION
            );

            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }

            itService.resolveTicket(ticketId, currentUser);
            JOptionPane.showMessageDialog(this, "Ticket " + ticketId + " has been marked as Resolved.");
            refreshUI();
        } catch (SecurityException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Access Denied", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showAccessDenied() {
        removeAll();
        JPanel deniedPanel = new JPanel(new GridBagLayout());
        deniedPanel.setBackground(bgColor);
        JLabel lblMessage = new JLabel("ACCESS DENIED: IT AUTHORIZATION REQUIRED");
        lblMessage.setFont(titleFont);
        lblMessage.setForeground(primaryMaroon);
        deniedPanel.add(lblMessage);
        add(deniedPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private void styleTable() {
        table.setRowHeight(40);
        table.setFont(bodyFont);
        table.getTableHeader().setFont(cardTitleFont);
        table.getTableHeader().setBackground(Color.WHITE);
        table.getTableHeader().setReorderingAllowed(false);
        table.setSelectionBackground(primaryMaroon);
        table.setSelectionForeground(Color.WHITE); 
        table.setShowVerticalLines(false);
        table.setGridColor(new Color(230, 230, 230));

        table.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean isS, boolean hasF, int r, int c) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, v, isS, hasF, r, c);
                l.setHorizontalAlignment(SwingConstants.CENTER);
                String val = String.valueOf(v);
                
                if (isS) {
                    l.setForeground(Color.WHITE);
                } else {
                    if (val.equalsIgnoreCase("RESOLVED")) {
                        l.setForeground(new Color(0, 153, 51));
                    } else {
                        l.setForeground(primaryMaroon);
                    }
                }
                l.setFont(new Font("DM Sans Bold", Font.BOLD, 12));
                return l;
            }
        });
    }

    private JPanel createStatusLegend() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        p.setOpaque(false);
        String roleDisplay = currentUser.getRole().name().replace("_", " ");
        JLabel lbl = new JLabel("Role: " + roleDisplay);
        lbl.setFont(bodyFont);
        lbl.setForeground(Color.GRAY);
        p.add(lbl);
        return p;
    }

    private JPanel createStyledTile(String title) {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(tileBg);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
                g2.setColor(new Color(225, 225, 225));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
                g2.dispose();
            }
        };
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        return panel;
    }

    class StyledButton extends JButton {
        private Color color;
        public StyledButton(String text, Color bg) {
            super(text);
            this.color = bg;
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setForeground(Color.WHITE);
            setFont(cardTitleFont);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (!isEnabled()) {
                g2.setColor(Color.LIGHT_GRAY);
            } else {
                g2.setColor(getModel().isPressed() ? color.darker() : color);
            }
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
            super.paintComponent(g);
            g2.dispose();
        }
    }


    private void handleGenerateTempPassword() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a forgot password ticket first.");
            return;
        }

        String ticketId = table.getValueAt(row, 0).toString();
        String issueType = table.getValueAt(row, 3).toString();
        String status = table.getValueAt(row, 4).toString();

        if (!"Forgot Password".equalsIgnoreCase(issueType)) {
            JOptionPane.showMessageDialog(this, "Selected ticket is not a forgot password request.");
            return;
        }

        if ("RESOLVED".equalsIgnoreCase(status)) {
            JOptionPane.showMessageDialog(this, "This ticket is already resolved.");
            return;
        }

        try {
            String tempPassword = itService.generateTemporaryPasswordAndResolve(ticketId, currentUser);

            JOptionPane.showMessageDialog(
                this,
                "Temporary password generated:\n" + tempPassword +
                "\n\nPlease email this temporary password to the user.",
                "Temporary Password Generated",
                JOptionPane.INFORMATION_MESSAGE
            );

            refreshUI();
        } catch (SecurityException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Access Denied", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}