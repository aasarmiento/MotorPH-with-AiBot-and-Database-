package service;

import dao.EmployeeDAO;
import dao.ITTicketDao;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import model.Employee;
import model.ITStaff;
import model.ITTicket;
import model.Role;

public class ITSupportService {
    private final ITTicketDao itTicketDao;
    private final EmployeeDAO employeeDao;

    public ITSupportService(ITTicketDao itTicketDao, EmployeeDAO employeeDao) {
        this.itTicketDao = itTicketDao;
        this.employeeDao = employeeDao;
    }

    public List<ITTicket> getOpenTickets() {
        return itTicketDao.getAllTickets().stream()
                .filter(t -> !"RESOLVED".equalsIgnoreCase(t.getStatus()))
                .collect(Collectors.toList());
    }

    public List<ITTicket> getAllTickets() {
        return itTicketDao.getAllTickets();
    }

    public List<ITTicket> getTicketsByEmployee(int empNo) {
        return itTicketDao.getAllTickets().stream()
                .filter(t -> t.getEmployeeNo() == empNo)
                .collect(Collectors.toList());
    }

    public boolean submitNewTicket(int empNo, String user, String name, String type, String desc) {
        List<ITTicket> currentTickets = itTicketDao.getAllTickets();

        long employeeTicketCount = currentTickets.stream()
                .filter(t -> t.getEmployeeNo() == empNo)
                .count();

        String formattedId = String.format("IT-%d-%02d", empNo, (int) employeeTicketCount + 1);

        String now = java.time.LocalDateTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        ITTicket newTicket = new ITTicket(
                formattedId, empNo, user, name, type, desc,
                "OPEN", now, "N/A", "N/A"
        );

        return itTicketDao.addTicket(newTicket);
    }

    public boolean submitForgotPasswordTicket(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }

        Employee emp = employeeDao.findByUsername(username.trim());
        if (emp == null) {
            return false;
        }

        boolean existingOpen = itTicketDao.getAllTickets().stream()
                .anyMatch(t ->
                        t.getEmployeeNo() == emp.getEmpNo()
                                && "Forgot Password".equalsIgnoreCase(t.getIssueType())
                                && !"RESOLVED".equalsIgnoreCase(t.getStatus())
                );

        if (existingOpen) {
            return false;
        }

        return submitNewTicket(
                emp.getEmpNo(),
                username.trim(),
                emp.getFirstName() + " " + emp.getLastName(),
                "Forgot Password",
                "Forgot password request from login screen."
        );
    }

    private void ensureAuthorized(Employee performer) {
        if (performer == null) {
            throw new SecurityException("Unauthorized: No user is logged in.");
        }

        boolean isAuthorized = false;

        if (performer instanceof ITStaff) {
            isAuthorized = ((ITStaff) performer).canApproveTechnicalTickets();
        } else if (performer.getRole() == Role.ADMIN) {
            isAuthorized = true;
        }

        if (!isAuthorized) {
            throw new SecurityException("Unauthorized: Only IT Staff or Admin can resolve tickets.");
        }
    }

    public void resolveTicket(String ticketId, Employee performer) {
        ensureAuthorized(performer);

        List<ITTicket> all = itTicketDao.getAllTickets();
        boolean found = false;

        for (ITTicket t : all) {
            if (t.getTicketId().equals(ticketId)) {

                if ("RESOLVED".equalsIgnoreCase(t.getStatus())) {
                    throw new IllegalStateException("This ticket is already resolved.");
                }

                String adminName = performer.getFirstName() + " " + performer.getLastName();
                t.markResolved(adminName);

                String issueType = t.getIssueType();
                if ("Account Locked".equalsIgnoreCase(issueType)
                        || "ACCOUNT_LOCKED".equalsIgnoreCase(issueType)
                        || "LOGIN_ISSUE".equalsIgnoreCase(issueType)) {
                    employeeDao.unlockAccount(t.getEmployeeNo());
                }

                found = true;
                break;
            }
        }

        if (!found) {
            throw new IllegalArgumentException("Ticket not found.");
        }

        itTicketDao.updateAllTickets(all);
    }

    private String buildTemporaryPassword() {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz23456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder("TMP");

        while (sb.length() < 8) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }

        return sb.toString();
    }

    public String generateTemporaryPasswordAndResolve(String ticketId, Employee performer) {
        ensureAuthorized(performer);

        List<ITTicket> all = itTicketDao.getAllTickets();

        for (ITTicket t : all) {
            if (t.getTicketId().equals(ticketId)) {

                if ("RESOLVED".equalsIgnoreCase(t.getStatus())) {
                    throw new IllegalStateException("This ticket is already resolved.");
                }

                if (!"Forgot Password".equalsIgnoreCase(t.getIssueType())) {
                    throw new IllegalArgumentException("Selected ticket is not a forgot password request.");
                }

                String tempPassword = buildTemporaryPassword();

                employeeDao.saveNewPassword(t.getEmployeeNo(), tempPassword);
                employeeDao.updateEmployeeStatus(t.getEmployeeNo(), "PASSWORD_RESET_REQUIRED");

                String resolverName = performer.getFirstName() + " " + performer.getLastName();
                t.markResolved(resolverName);
                itTicketDao.updateAllTickets(all);

                return tempPassword;
            }
        }

        throw new IllegalArgumentException("Ticket not found.");
    }
}