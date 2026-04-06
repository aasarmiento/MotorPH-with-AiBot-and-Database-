package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import model.ITTicket;

public class ITTicketDAOImpl implements ITTicketDao {


    @Override
    public List<ITTicket> getAllTickets() {
        List<ITTicket> tickets = new ArrayList<>();

        String sql = "SELECT * FROM public.it_tickets ORDER BY created_at DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {


                tickets.add(new ITTicket(
                    rs.getString("ticket_id"),    
                    rs.getInt("employee_id"), 
                    rs.getString("username"),
                    rs.getString("full_name"),
                    rs.getString("ticket_type"),  
                    rs.getString("description"),
                    rs.getString("status"),
                    rs.getString("created_at"),   
                    rs.getString("resolved_at"),  
                    rs.getString("resolved_by")
                ));
            }
        } catch (SQLException e) {
            System.err.println("DB Error in getAllTickets: " + e.getMessage());
        }
        return tickets;
    }

    @Override
    public boolean addTicket(ITTicket t) {
        // SQL with '::timestamp' cast to prevent the Character Varying error
        String sql = "INSERT INTO public.it_tickets (ticket_id, employee_id, username, full_name, ticket_type, description, status, created_at) " + 
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?::timestamp)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, t.getTicketId());
            pstmt.setInt(2, t.getEmployeeNo());
            pstmt.setString(3, t.getUsername());
            pstmt.setString(4, t.getFullName());
            pstmt.setString(5, t.getIssueType());
            pstmt.setString(6, t.getDescription());
            pstmt.setString(7, t.getStatus());
            pstmt.setString(8, t.getCreatedAt()); 
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("DB Error in addTicket: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean saveTicket(ITTicket ticket) {

        return addTicket(ticket);
    }

    @Override
    public void updateAllTickets(List<ITTicket> tickets) {
        
        System.out.println("System: IT Ticket storage is now handled exclusively by PostgreSQL.");
    }


}