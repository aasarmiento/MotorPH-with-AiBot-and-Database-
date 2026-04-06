package dao;

import java.util.ArrayList;
import java.util.List;

public class AnnouncementDAO {
    public List<String> getAllAnnouncements() {
        List<String> announcements = new ArrayList<>();
        announcements.add(" Christmas Event: MotorPH is celebrating Christmas! Annual event on Dec 20th. Tayo ay magdiwang");
        announcements.add(" Team Building: Upcoming event on March 23rd 2026!");
        return announcements;
    }

    public String getLatestAnnouncement() {
        List<String> all = getAllAnnouncements();
        return all.isEmpty() ? "No new announcements." : all.get(0);
    }
}