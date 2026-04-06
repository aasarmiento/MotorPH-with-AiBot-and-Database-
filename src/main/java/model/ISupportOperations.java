package model;

// Inside ISupportOperations.java
public interface ISupportOperations {
    void resolveTicket(int empNo); // Only IT fixes tickets
    boolean resetCredentials(int empNo, String newPassword); // Only IT resets passwords
}