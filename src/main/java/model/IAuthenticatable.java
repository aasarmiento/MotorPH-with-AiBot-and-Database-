package model;

public interface IAuthenticatable {
    boolean isPasswordValid(String pass);
    void resetPassword();
    Role getRole();
}