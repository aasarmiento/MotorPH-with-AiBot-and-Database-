package ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.LayoutManager;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class UIUtils {
    public static final Color MOTORPH_MAROON = new Color(128, 0, 0);
    public static final Color MAROON = new Color(128, 0, 0); // From second version
    public static final Color SUCCESS_GREEN = new Color(0, 102, 51);
    public static final Color BG_LIGHT = new Color(245, 245, 245);
    
    public static final Font FONT_HEADER = new Font("Arial", Font.BOLD, 20);
    public static final Font FONT_LABEL = new Font("SansSerif", Font.BOLD, 12);
    public static final Font FONT_BODY = new Font("DM Sans Regular", Font.PLAIN, 14);
    public static final Font BODY_FONT = new Font("DM Sans Regular", Font.PLAIN, 14); // From second version
    public static final Font BUTTON_FONT = new Font("DM Sans Bold", Font.BOLD, 12); // From second version

    
   
    public static JButton createPrimaryButton(String text) {
        return formatButton(new JButton(text), MOTORPH_MAROON);
    }

    public static JButton createSuccessButton(String text) {
        return formatButton(new JButton(text), SUCCESS_GREEN);
    }

    public static JButton createNavButton(String text, Color foreground, Color background) {
        JButton button = new JButton(text);
        button.setForeground(foreground);
        button.setBackground(new Color(128, 0, 0)); 
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setFont(new Font("SansSerif", Font.BOLD, 13));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    public static JButton createButton(String text, Color bgColor, Color fgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setMaximumSize(new Dimension(180, 30));
        return button;
    }

    private static JButton formatButton(JButton btn, Color bg) {
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(BUTTON_FONT);
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    public static JTextField createTextField(boolean editable) {
        JTextField textField = new JTextField();
        textField.setEditable(editable);
        textField.setFont(FONT_BODY);
        return textField;
    }

    public static JLabel createHeaderLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_HEADER);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        label.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 0));
        return label;
    }

    public static JPanel createTitledPanel(String title, LayoutManager layout) {
        JPanel panel = new JPanel(layout);
        panel.setBackground(Color.WHITE); 
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)), title));
        return panel;
    }

    public static JPanel createEmployeeInfoPanel(JTextField empNo, JTextField lastName, JTextField firstName,
                                                JTextField status, JTextField position, JTextField supervisor) {
        JPanel empInfo = createTitledPanel("Employee Information", new GridLayout(3, 2, 10, 10));
        addLabel(empInfo, "Employee No:"); empInfo.add(empNo);
        addLabel(empInfo, "Last Name:");   empInfo.add(lastName);
        addLabel(empInfo, "First Name:");  empInfo.add(firstName);
        addLabel(empInfo, "Status:");      empInfo.add(status);
        addLabel(empInfo, "Position:");    empInfo.add(position);
        addLabel(empInfo, "Supervisor:");  empInfo.add(supervisor);
        return empInfo;
    }

    public static JPanel createPersonalInfoPanel(JTextField birthday, JTextField address, JTextField phone,
                                                 JTextField sss, JTextField philhealth, JTextField tin,
                                                 JTextField pagibig) {
        JPanel personalInfo = createTitledPanel("Personal Information", new GridLayout(7, 2, 5, 5));
        addLabel(personalInfo, "Birthday:");    personalInfo.add(birthday);
        addLabel(personalInfo, "Address:");     personalInfo.add(address);
        addLabel(personalInfo, "Phone:");       personalInfo.add(phone);
        addLabel(personalInfo, "SSS:");         personalInfo.add(sss);
        addLabel(personalInfo, "PhilHealth:"); personalInfo.add(philhealth);
        addLabel(personalInfo, "TIN:");         personalInfo.add(tin);
        addLabel(personalInfo, "Pagibig:");     personalInfo.add(pagibig);
        return personalInfo;
    }

    public static JPanel createFinancialInfoPanel(JTextField basicSalary, JTextField riceSubsidy,
                                                  JTextField phoneAllowance, JTextField clothingAllowance,
                                                  JTextField grossRate, JTextField hourlyRate) {
        JPanel financialInfo = createTitledPanel("Financial Information", new GridLayout(6, 2, 5, 5));
        addLabel(financialInfo, "Basic Salary:");        financialInfo.add(basicSalary);
        addLabel(financialInfo, "Rice Subsidy:");        financialInfo.add(riceSubsidy);
        addLabel(financialInfo, "Phone Allowance:");     financialInfo.add(phoneAllowance);
        addLabel(financialInfo, "Clothing Allowance:");  financialInfo.add(clothingAllowance);
        addLabel(financialInfo, "Gross Rate:");          financialInfo.add(grossRate);
        addLabel(financialInfo, "Hourly Rate:");         financialInfo.add(hourlyRate);
        return financialInfo;
    }

    private static void addLabel(JPanel panel, String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_LABEL);
        panel.add(label);
    }
}