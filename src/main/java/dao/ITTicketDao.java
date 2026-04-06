package dao;

import java.util.List;
import model.ITTicket;

public interface ITTicketDao {
    List<ITTicket> getAllTickets();
    boolean saveTicket(ITTicket ticket);
    void updateAllTickets(List<ITTicket> tickets);
    boolean addTicket(ITTicket ticket);
}