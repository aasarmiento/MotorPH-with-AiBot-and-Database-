package ui;

import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import model.Employee;
import model.ITTicket;
import service.ITSupportService;

// Updated to extend BasePanel
public class ITSupportPanel extends BasePanel {
    private final ITSupportService itService;
    private final Employee currentUser;
    
    private DefaultTableModel model;
    private JTable table;
    private JLabel lblActiveTicketsCount; 

    private final Color primaryMaroon = UIUtils.MOTORPH_MAROON;
    private final Color bgColor = UIUtils.BG_LIGHT;
    private final Color tileBg = Color.WHITE;
    private final Font titleFont = new Font("DM Sans Bold", Font.BOLD, 18);
    private final Font bodyFont = UIUtils.FONT_BODY;
    private final Font cardTitleFont = UIUtils.FONT_LABEL;
    
    private JComboBox<String> cbIssueType;
    private JTextArea txtDescription, txtDetailView;
    private JLabel lblDetailID, lblDetailStatus, lblDetailDate;
    
    private final String[] cols = {"ID", "Emp No", "Username", "Type", "Status", "Created", "Resolved By", "Description"};

    public ITSupportPanel(ITSupportService itService, Employee user) {
        super(); // Initializes BasePanel background and layout
        this.itService = itService;
        this.currentUser = user;
        
        // BasePanel already sets BorderLayout and Background
        setBorder(new EmptyBorder(25, 40, 25, 40));
        
        model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        table = new JTable(model);
        
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        styleTable();
        
        add(createTopKPIDashboard(), BorderLayout.NORTH);
        add(createMainContentArea(), BorderLayout.CENTER);
        
        refreshData();
    }

   
    @Override
    public void refreshData() {
        refreshUI();
    }

    private JPanel createMainContentArea() {
        JPanel mainContent = new JPanel(new GridBagLayout());
        mainContent.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();

        JPanel tableCard = createStyledTile("Your Ticket History");
        tableCard.setLayout(new BorderLayout());
        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setBorder(BorderFactory.createEmptyBorder());
        tableScroll.getViewport().setBackground(Color.WHITE);
        tableScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        tableCard.add(tableScroll, BorderLayout.CENTER);

        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 2; 
        gbc.weightx = 1.0; gbc.weighty = 0.7; 
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 0, 15, 0);
        mainContent.add(tableCard, gbc);

        JPanel formCard = createTicketForm();
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.35; gbc.weighty = 0.3; 
        gbc.insets = new Insets(0, 0, 0, 10);
        mainContent.add(formCard, gbc);

        JPanel detailsCard = createDetailsPanel();
        gbc.gridx = 1; gbc.gridy = 1;
        gbc.weightx = 0.65;
        gbc.insets = new Insets(0, 10, 0, 0);
        mainContent.add(detailsCard, gbc);

        return mainContent;
    }

    private JPanel createTicketForm() {
        JPanel formCard = createStyledTile("Submit New Ticket");
        formCard.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10); 
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        cbIssueType = new JComboBox<>(new String[]{"Forgot Password", "Account Locked", "Technical Support", "UI Error"});
        cbIssueType.setFont(bodyFont);
        
        txtDescription = new JTextArea(3, 20); 
        txtDescription.setFont(bodyFont);
        txtDescription.setLineWrap(true);
        txtDescription.setWrapStyleWord(true);
        txtDescription.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        JButton btnSubmit = new StyledButton("SUBMIT TICKET", primaryMaroon);
        btnSubmit.setPreferredSize(new Dimension(0, 35));

        gbc.gridy = 0; formCard.add(new JLabel("Issue Category"), gbc);
        gbc.gridy = 1; formCard.add(cbIssueType, gbc);
        gbc.gridy = 2; formCard.add(new JLabel("Detailed Description"), gbc);
        gbc.gridy = 3; gbc.weighty = 1.0; gbc.fill = GridBagConstraints.BOTH;
        formCard.add(new JScrollPane(txtDescription), gbc);
        gbc.gridy = 4; gbc.weighty = 0; gbc.fill = GridBagConstraints.HORIZONTAL;
        formCard.add(btnSubmit, gbc);

        btnSubmit.addActionListener(e -> handleSubmit());
        return formCard;
    }

    private JPanel createDetailsPanel() {
        JPanel detailsCard = createStyledTile("Ticket Information");
        detailsCard.setLayout(new BorderLayout(15, 10));

        JPanel infoHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 5));
        infoHeader.setOpaque(false);
        
        lblDetailID = new JLabel("ID: --");
        lblDetailStatus = new JLabel("Status: --");
        lblDetailDate = new JLabel("Created: --");
        
        for(JLabel lbl : new JLabel[]{lblDetailID, lblDetailStatus, lblDetailDate}) {
            lbl.setFont(cardTitleFont);
            infoHeader.add(lbl);
        }
        
        txtDetailView = new JTextArea("Select a record to view details...");
        txtDetailView.setEditable(false);
        txtDetailView.setFont(bodyFont);
        txtDetailView.setLineWrap(true);
        txtDetailView.setBackground(new Color(242, 242, 242));
        txtDetailView.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        detailsCard.add(infoHeader, BorderLayout.NORTH);
        detailsCard.add(new JScrollPane(txtDetailView), BorderLayout.CENTER);

        table.getSelectionModel().addListSelectionListener(e -> {
            int r = table.getSelectedRow();
            if (!e.getValueIsAdjusting() && r != -1) {
                lblDetailID.setText("ID: " + table.getValueAt(r, 0).toString());
                lblDetailStatus.setText("Status: " + table.getValueAt(r, 4).toString());
                lblDetailDate.setText("Created: " + table.getValueAt(r, 5).toString());
                txtDetailView.setText("DESCRIPTION:\n" + table.getValueAt(r, 7).toString());
            }
        });

        return detailsCard;
    }

    private JPanel createTopKPIDashboard() {
        JPanel header = new JPanel(new GridLayout(1, 3, 20, 0));
        header.setOpaque(false);
        header.setPreferredSize(new Dimension(0, 80));
        header.add(createKPICard("Your Tickets", "0", primaryMaroon)); 
        header.add(createKPICard("Logged User", currentUser.getFirstName(), Color.DARK_GRAY));
        header.add(createKPICard("Support Module", "IT Helpdesk", new Color(45, 45, 45)));
        return header;
    }

    private JPanel createKPICard(String title, String value, Color themeColor) {
        JPanel card = createStyledTile("");
        card.setLayout(new GridLayout(2, 1));
        JLabel lblT = new JLabel(title); lblT.setFont(cardTitleFont); lblT.setForeground(Color.GRAY);
        JLabel lblV = new JLabel(value); lblV.setFont(new Font("DM Sans Bold", Font.BOLD, 22)); lblV.setForeground(themeColor);
        card.add(lblT); card.add(lblV);
        if(title.equals("Your Tickets")) lblActiveTicketsCount = lblV;
        return card;
    }

    private JPanel createStyledTile(String title) {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(tileBg);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
                g2.setColor(new Color(230, 230, 230));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
                g2.dispose();
            }
        };
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        if(!title.isEmpty()) {
            panel.setBorder(BorderFactory.createCompoundBorder(
                panel.getBorder(), 
                BorderFactory.createTitledBorder(null, title, 0, 0, cardTitleFont, primaryMaroon)
            ));
        }
        return panel;
    }

    private void styleTable() {
        table.setRowHeight(35);
        table.setFont(bodyFont);
        table.getTableHeader().setFont(cardTitleFont);
        table.getTableHeader().setBackground(Color.WHITE);
        table.setSelectionBackground(primaryMaroon);
        table.setSelectionForeground(Color.WHITE); 
        table.setShowVerticalLines(false);
        table.setGridColor(new Color(235, 235, 235));
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        
        table.getColumnModel().getColumn(0).setPreferredWidth(120); // ID
        table.getColumnModel().getColumn(1).setPreferredWidth(80);  // Emp No
        table.getColumnModel().getColumn(2).setPreferredWidth(100); // Username
        table.getColumnModel().getColumn(3).setPreferredWidth(150); // Type
        table.getColumnModel().getColumn(4).setPreferredWidth(100); // Status
        table.getColumnModel().getColumn(5).setPreferredWidth(150); // Created
        table.getColumnModel().getColumn(6).setPreferredWidth(120); // Resolved By
        table.getColumnModel().getColumn(7).setPreferredWidth(400); // Description
        
        setupStatusRenderer();
    }

    private void setupStatusRenderer() {
        table.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean isS, boolean hasF, int r, int c) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, v, isS, hasF, r, c);
                l.setHorizontalAlignment(SwingConstants.CENTER);
                if (!isS) {
                    if (v != null) {
                        l.setForeground("OPEN".equalsIgnoreCase(v.toString()) ? Color.BLUE : new Color(0, 128, 0));
                    }
                } else {
                    l.setForeground(Color.WHITE);
                }
                l.setFont(new Font("DM Sans Bold", Font.BOLD, 12));
                return l;
            }
        });
    }

    public void refreshUI() {
        model.setRowCount(0);
        List<ITTicket> myTickets = itService.getTicketsByEmployee(currentUser.getEmpNo());
        for (ITTicket t : myTickets) {
            model.addRow(new Object[]{
                t.getTicketId(), t.getEmployeeNo(), t.getUsername(), 
                t.getIssueType(), t.getStatus(), t.getCreatedAt(), 
                t.getResolvedBy(), t.getDescription()
            });
        }
        if (lblActiveTicketsCount != null) {
            lblActiveTicketsCount.setText(String.valueOf(myTickets.size()));
        }
    }

    private void handleSubmit() {
        String type = (String) cbIssueType.getSelectedItem(); 
        String desc = txtDescription.getText().trim();
        if (desc.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please describe the issue.");
            return;
        }
        boolean success = itService.submitNewTicket(
            currentUser.getEmpNo(), currentUser.getFirstName(), 
            currentUser.getFirstName() + " " + currentUser.getLastName(), type, desc
        );
        if (success) {
            JOptionPane.showMessageDialog(this, "Ticket Submitted!");
            txtDescription.setText("");
            refreshUI(); 
        }
    }

    class StyledButton extends JButton {
        private Color bgColor;
        public StyledButton(String text, Color bg) {
            super(text);
            this.bgColor = bg;
            setContentAreaFilled(false); setBorderPainted(false); setFocusPainted(false);
            setForeground(Color.WHITE); setFont(cardTitleFont);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getModel().isPressed() ? bgColor.darker() : bgColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
            super.paintComponent(g);
            g2.dispose();
        }
    }
}