package dao;

import java.io.File;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class IconMigrator {
    public static void main(String[] args) {
        // Double check this path in Finder! 
        // If your icons are directly in resources, remove "Icons/"
        String folderPath = "/Users/abigail/MotorPhF/src/main/resources/Icons/";
        
        String sql = "INSERT INTO public.dashboard_assets (icon_name, icon_bytes, category) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection()) {
            File folder = new File(folderPath);
            File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".png"));

            if (files == null || files.length == 0) {
                System.out.println("❌ ERROR: No PNGs found in " + folderPath);
                return;
            }

            for (File f : files) {
                // This is the critical part: reading the actual data
                byte[] data = Files.readAllBytes(f.toPath());
                
                if (data.length > 0) {
                    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                        pstmt.setString(1, f.getName());
                        pstmt.setBytes(2, data);
                        pstmt.setString(3, "Dashboard");
                        pstmt.executeUpdate();
                        System.out.println("✅ Uploaded: " + f.getName() + " [" + data.length + " bytes]");
                    }
                } else {
                    System.out.println("⚠️ Skipping " + f.getName() + " - File is 0 bytes on your Mac!");
                }
            }
            System.out.println("\n--- DONE! Check pgAdmin now ---");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}