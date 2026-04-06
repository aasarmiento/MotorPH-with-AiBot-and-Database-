package service;

import dao.AttendanceDAO;
import dao.EmployeeDAO;
import model.Employee;
import java.util.List;

import java.util.ArrayList;


public class AiAssistantService {

    private final EmployeeDAO employeeDAO;
    private final AttendanceDAO attendanceDao;
    private final LeaveService leaveService;

    private final AiClient aiClient = new AiClient("gsk_OcKtSbWms5tX88NWX054YyfnXfKul6BhQwTAH4YlHL8Je");

    public AiAssistantService(EmployeeDAO employeeDao, AttendanceDAO attendanceDao, LeaveService leaveService) {
        this.employeeDAO = employeeDao;
        this.attendanceDao = attendanceDao;
        this.leaveService = leaveService;
    }

    public String askHrBot(int empId, String userQuestion) {
        Employee currentUser = employeeDAO.findById(empId);
        if (currentUser == null) {
            return "Error: User session not found in database.";
        }

        
        List<Employee> allEmployees = employeeDAO.findAll(); 
        
        StringBuilder companyKnowledge = new StringBuilder();
        for (Employee e : allEmployees) {
            companyKnowledge.append(String.format(
                "- Employee: %s %s | Position: %s | Supervisor: %s | Status: %s\n",
                e.getFirstName(), 
                e.getLastName(), 
                e.getPosition(), 
                e.getSupervisor(), 
                e.getStatus()
            ));
        }

        String context = String.format(
            "### SYSTEM ROLE\n" +
            "You are the MotorPH Smart Assistant. You have full access to the company employee directory provided below.\n\n" +
            
            "### COMPANY DIRECTORY (LIVE DATABASE)\n" +
            "%s\n\n" +
            
            "### USER CONTEXT\n" +
            "Current User: %s %s (ID: %d)\n\n" +
            
            "### INSTRUCTIONS\n" +
            "1. ALWAYS GREET: 'Good day, %s! I hope you're having a great day.'\n" +
            "2. If asked about a coworker (like Beatriz Santos), look them up in the directory above.\n" +
            "3. If asked for an immediate supervisor, report the name listed in the 'Supervisor' column for that employee.\n" +
            "4. Be professional and concise.\n" +
            "5. ALWAYS END: 'Is there anything else you'd like me to do?'\n\n" +
            
            "USER QUESTION: %s",
            
            companyKnowledge.toString(),
            currentUser.getFirstName(), currentUser.getLastName(), currentUser.getEmpNo(),
            currentUser.getFirstName(),
            userQuestion
        );

        return aiClient.generate(context);
    }
}
