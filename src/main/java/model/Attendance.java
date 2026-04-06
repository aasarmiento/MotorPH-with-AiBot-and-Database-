package model;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PostLoad;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity 
@Table(name = "attendance", schema = "public") 
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int attendance_id;

    @Column(name = "employee_id")
    private int empNo;

    @Column(name = "date_logged")
    private LocalDate date;

    @Column(name = "time_in")
    private LocalTime timeIn;

    @Column(name = "time_out")
    private LocalTime timeOut;
    
    @Transient
    private double hoursWorked;
    @Transient
    private boolean isLate;
    @Transient
    private int lateMinutes;

    private static final LocalTime SHIFT_START = LocalTime.of(9, 0);

    public Attendance() {}

    public Attendance(int empNo, LocalDate date, LocalTime timeIn, LocalTime timeOut) {
        this.empNo = empNo;
        this.date = date;
        this.timeIn = timeIn;
        this.timeOut = timeOut;
        calculateStats(); 
    }

    @PostLoad
    public void calculateStats() {
        if (timeIn == null || timeOut == null) {
            this.hoursWorked = 0;
            this.lateMinutes = 0;
            this.isLate = false;
            return;
        }

        if (timeIn.isAfter(SHIFT_START)) {
            this.lateMinutes = (int) Duration.between(SHIFT_START, timeIn).toMinutes();
            this.isLate = true;
        } else {
            this.lateMinutes = 0;
            this.isLate = false;
        }

        long minutes = Duration.between(timeIn, timeOut).toMinutes();
        if (minutes > 300) { minutes -= 60; }
        this.hoursWorked = Math.max(0, minutes / 60.0);
    }

    // --- Getters ---
    public int getAttendance_id() { return attendance_id; }
    public int getEmpNo() { return empNo; }
    public LocalDate getDate() { return date; }
    public LocalTime getTimeIn() { return timeIn; }
    public LocalTime getTimeOut() { return timeOut; }
    public double getHoursWorked() { return hoursWorked; }
    public boolean isLate() { return isLate; }
    public int getLateMinutes() { return lateMinutes; }

    // --- ADDED SETTERS TO FIX THE ERRORS ---
    public void setAttendance_id(int attendance_id) { this.attendance_id = attendance_id; }
    public void setEmpNo(int empNo) { this.empNo = empNo; }
    public void setDate(LocalDate date) { this.date = date; }
    public void setTimeIn(LocalTime timeIn) { this.timeIn = timeIn; }
    public void setTimeOut(LocalTime timeOut) { this.timeOut = timeOut; }
    public void setHoursWorked(double hoursWorked) { this.hoursWorked = hoursWorked; }
    public void setLate(boolean late) { isLate = late; }
    public void setLateMinutes(int lateMinutes) { this.lateMinutes = lateMinutes; }
}