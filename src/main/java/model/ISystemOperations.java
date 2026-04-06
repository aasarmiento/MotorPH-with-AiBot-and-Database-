package model;



public interface ISystemOperations {
    
    
    boolean resetCredentials(String empID, String newPassword);
    
   
    
   
    String checkSystemHealth();
}