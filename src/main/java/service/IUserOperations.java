package service;

public interface IUserOperations {
    boolean login(String user, String pass);
    void logout();
    int getPasswordStrength();
   
}