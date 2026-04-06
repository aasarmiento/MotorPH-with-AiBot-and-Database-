package ui;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

public abstract class BasePanel extends JPanel {
    
    
    protected final Font cardTitleFont = new Font("DM Sans Bold", Font.BOLD, 12);
    protected final Font bodyFont = new Font("DM Sans Regular", Font.PLAIN, 14);
    protected final Color motorPHRed = new Color(128, 0, 0);

    public BasePanel() {
        setBackground(new Color(245, 245, 245)); 
        setLayout(new BorderLayout());
       
        setBorder(new EmptyBorder(20, 20, 20, 20));
    }

    
    public abstract void refreshData();

   
    protected JPanel createStyledTile() {
        JPanel panel = new JPanel() {
            @Override
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

    
    protected JTextField addField(JPanel container, String labelText) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(cardTitleFont);
        lbl.setForeground(Color.GRAY);
        
        JTextField field = new JTextField();
        field.setEditable(false);
        field.setBorder(null);
        field.setOpaque(false);
        field.setFont(bodyFont);
        field.setForeground(Color.BLACK);
        
        wrapper.add(lbl, BorderLayout.NORTH);
        wrapper.add(field, BorderLayout.CENTER);
        container.add(wrapper);
        return field;
    }

    
    protected JPanel createSection(String title, int rows, int cols) {
        JPanel p = createStyledTile();
        TitledBorder border = BorderFactory.createTitledBorder(null, title, 
                             TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, 
                             cardTitleFont, motorPHRed);
        p.setBorder(BorderFactory.createCompoundBorder(border, new EmptyBorder(5, 5, 5, 5)));
        p.setLayout(new GridLayout(rows, cols, 10, 10));
        return p;
    }
}