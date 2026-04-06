package ui;

import java.awt.*;
import javax.swing.*;

public class DatePicker {
    int month = java.util.Calendar.getInstance().get(java.util.Calendar.MONTH);
    int year = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);
    JLabel l = new JLabel("", JLabel.CENTER);
    String day = "";
    int dayInt = -1;
    JDialog d;
    JButton[] button = new JButton[49];

    public DatePicker(Component parent) {
        Window parentWindow = SwingUtilities.getWindowAncestor(parent);
        d = new JDialog(parentWindow instanceof Frame ? (Frame)parentWindow : null); 
        d.setModal(true); 
        d.setTitle("Select Date");
        d.setLayout(new BorderLayout());

        java.util.Calendar now = java.util.Calendar.getInstance();
        month = now.get(java.util.Calendar.MONTH);
        year = now.get(java.util.Calendar.YEAR);

        JPanel p1 = new JPanel(new GridLayout(7, 7)); 
        String[] header = { "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat" };

        for (int x = 0; x < button.length; x++) {
            final int selection = x;
            button[x] = new JButton();
            button[x].setFocusPainted(false);
            button[x].setBackground(Color.WHITE);
            if (x < 7) {
                button[x].setText(header[x]);
                button[x].setForeground(Color.RED);
                button[x].setEnabled(false);
            } else {
                button[x].addActionListener(e -> {
                    String btnText = button[selection].getText();
                    if (!btnText.isEmpty()) {
                        dayInt = Integer.parseInt(btnText);
                        day = String.format("%02d/%02d/%d", month + 1, dayInt, year);
                        d.dispose();
                    }
                });
            }
            p1.add(button[x]);
        }

        JPanel p2 = new JPanel(new FlowLayout());
        JComboBox<String> monthCombo = new JComboBox<>(new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"});
        monthCombo.setSelectedIndex(month);
        
        // Year selection logic
        Integer[] yearsArr = new Integer[30]; 
        int currentYear = now.get(java.util.Calendar.YEAR);
        for (int i = 0; i < 30; i++) yearsArr[i] = (currentYear + 5) - i; // Shows future and past
        JComboBox<Integer> yearCombo = new JComboBox<>(yearsArr);
        yearCombo.setSelectedItem(year);

        monthCombo.addActionListener(e -> { month = monthCombo.getSelectedIndex(); displayDate(); });
        yearCombo.addActionListener(e -> { year = (Integer) yearCombo.getSelectedItem(); displayDate(); });

        p2.add(monthCombo); p2.add(yearCombo);
        d.add(p2, BorderLayout.NORTH); 
        d.add(p1, BorderLayout.CENTER);
        d.pack(); 
        d.setLocationRelativeTo(parent);
        displayDate();
        d.setVisible(true);
    }

    public void displayDate() {
        for (int x = 7; x < button.length; x++) button[x].setText("");
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.set(year, month, 1);
        int dayOfWeek = cal.get(java.util.Calendar.DAY_OF_WEEK);
        int daysInMonth = cal.getActualMaximum(java.util.Calendar.DAY_OF_MONTH);
        
        for (int x = 6 + dayOfWeek, dayNum = 1; dayNum <= daysInMonth; x++, dayNum++) {
            button[x].setText("" + dayNum);
        }
    }

    public String setPickedDate() {
        if (dayInt == -1) return "";
        return String.format("%02d/%02d/%d", month + 1, dayInt, year);
    }
}