package dao;

import java.util.List;

import model.ITTicket;

public class TestITConnection {
    public static void main(String[] args) {
        ITTicketDAOImpl dao = new ITTicketDAOImpl();
        
        System.out.println("--- Testing IT Ticket Connection ---");
        
        // Test 1: Try to FETCH data
        List<ITTicket> tickets = dao.getAllTickets();
        
        if (tickets != null && !tickets.isEmpty()) {
            System.out.println("✅ Success! Found " + tickets.size() + " tickets in Database.");
            // Print the first ticket just to be sure
            System.out.println("First Ticket ID: " + tickets.get(0).getTicketId());
        } else {
            System.out.println("❌ Failed: No tickets found or connection error.");
        }
        
        // Test 2: Try to INSERT a test ticket
        ITTicket testTicket = new ITTicket(
            "TEST-999", 10001, "TestUser", "Test Name", 
            "Technical Support", "Checking connection...", 
            "PENDING", "2026-03-24", null, null
        );
        
        boolean saved = dao.addTicket(testTicket);
        if (saved) {
            System.out.println("✅ Success! Test ticket saved to PostgreSQL.");
        } else {
            System.out.println("❌ Failed: Could not save test ticket.");
        }
    }
}