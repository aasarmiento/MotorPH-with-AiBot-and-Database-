package main;

import java.io.File;
import javax.swing.SwingUtilities;

import dao.AttendanceCSVHandler;
import dao.AttendanceDAO;
import dao.CSVHandler;          
import dao.EmployeeDAO;
import dao.ITTicketDAOImpl;
import dao.ITTicketDao;
import dao.UserLibrary;
import model.Employee;
import service.AiAssistantService;
import service.EmployeeManagementService;
import service.ITSupportService;
import service.LeaveService; // Added this import
import ui.LoginPanel;

public class Main {
    public static void main(String[] args) {

        try {
            // Initialize DAOs
            EmployeeDAO employeeDao = new CSVHandler(); 
            AttendanceDAO attendanceDao = new AttendanceCSVHandler(); 

            // Initialize Services
            EmployeeManagementService service = new EmployeeManagementService(employeeDao, attendanceDao);
            UserLibrary auth = new UserLibrary(employeeDao);
            ITTicketDao itTicketDao = new ITTicketDAOImpl();
            ITSupportService itSupportService = new ITSupportService(itTicketDao, employeeDao);
            
            // --- FIX START ---
            // 1. Initialize LeaveService (Required for the new AI constructor)
            LeaveService leaveService = new LeaveService(employeeDao, attendanceDao);
            
            // 2. Initialize AI Service with the 3 required arguments: DAO, Attendance, and LeaveService
            AiAssistantService aiService = new AiAssistantService(service.getEmployeeDao(), attendanceDao, leaveService);
            // --- FIX END ---

            // --- AUTO-PHOTO MIGRATOR ---
            // This pulls the REAL employee object so we don't have to instantiate an abstract class
            File resourceDir = new File("/Users/abigail/MotorPhF/src/main/resources/");
            if (resourceDir.exists() && resourceDir.isDirectory()) {
                File[] photos = resourceDir.listFiles((dir, name) -> name.matches("\\d+\\.png"));
                if (photos != null) {
                    for (File photo : photos) {
                        try {
                            // 1. Get the ID from the filename (e.g. 10001)
                            int id = Integer.parseInt(photo.getName().replace(".png", ""));
                            
                            // 2. Find the existing employee in your system
                            Employee existingEmp = service.getEmployeeDao().findById(id);
                            
                            // 3. Only update if the employee actually exists
                            if (existingEmp != null) {
                                service.updateEmployeePhoto(existingEmp, photo);
                            }
                            
                        } catch (Exception e) {
                            // Skip files that don't match or errors
                        }
                    }
                    System.out.println("MotorPH: All employee photos synced to Database.");
                }
            }

            // Launch UI
            SwingUtilities.invokeLater(() -> {
                try {
                    LoginPanel login = new LoginPanel(service, attendanceDao, auth, itSupportService, aiService);
                    login.setVisible(true);
                    login.toFront();
                    login.requestFocus();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}