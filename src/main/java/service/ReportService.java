package service;

import java.io.File;
import java.io.PrintWriter;
import model.Employee;

public class ReportService {
    
    public void generateLeaveReport(Employee emp, String[] leaveDetails) {
        String fileName = "reports/Employee_Record_" + emp.getEmpNo() + ".txt";
        
        try {
            File directory = new File("reports");
            if (!directory.exists()) directory.mkdir();

            PrintWriter writer = new PrintWriter(fileName);
            
            writer.println("MOTORPH PORTAL - EMPLOYEE RECORD");
            writer.println("--------------------------------");
            writer.println("ID: " + emp.getEmpNo());
            writer.println("Name: " + emp.getFirstName() + " " + emp.getLastName());
            writer.println("Position: " + emp.getPosition());
            writer.println("Address: " + emp.getAddress());
            writer.println("Phone: " + emp.getPhone());
            writer.println("Gross Rate: " + emp.getGrossRate());
            writer.println("--------------------------------");
            writer.println("LEAVE DETAILS");
            writer.println("Type: " + leaveDetails[4]);
            writer.println("Period: " + leaveDetails[5] + " to " + leaveDetails[6]);
            writer.println("Status: " + leaveDetails[8]);
            writer.println("Reason: " + leaveDetails[7]);
            
            writer.close();
            System.out.println("Report generated: " + fileName);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}