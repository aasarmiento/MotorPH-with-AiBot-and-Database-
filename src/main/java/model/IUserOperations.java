/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template


/**
 * @author abigail
 */

package model;

/**
 * @author abigail
 */
public interface IUserOperations {
    public boolean login(String user, String pass);
    
    public Role getRole(); 
    
    public boolean isPasswordValid(String pass);
    public int getPasswordStrength();
    public void resetPassword();
    public void logout();
} 
