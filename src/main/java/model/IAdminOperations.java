
package model;


public interface IAdminOperations {
  
    public void createEmployee(Employee emp); 
    
    
    public int generateNextEmpNo(); 
    
    
    public boolean isEmployeeValid(Employee emp); 
    
   
    
    
  
    public void removeEmployee(int empNo);
    
    public void updateEmployee(int empNo);
}


