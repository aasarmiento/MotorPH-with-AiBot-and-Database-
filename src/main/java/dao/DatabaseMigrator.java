package dao;
import java.io.File;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class DatabaseMigrator {
    public static void main(String[] args) {
        migrate();
    }

    public static void migrate() {
        String folderPath = "src/main/resources/profile_pics/";
        String sql = "UPDATE public.employees SET profile_picture = ? WHERE employee_id = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            File folder = new File(folderPath);
            File[] listOfFiles = folder.listFiles();

            if (listOfFiles != null) {
                for (File file : listOfFiles) {
                    // This logic skips the .csv files in your folder and only takes images
                    if (file.isFile() && (file.getName().endsWith(".png") || file.getName().endsWith(".jpg"))) {
                        try {
                            String idStr = file.getName().split("\\.")[0];
                            int empId = Integer.parseInt(idStr);

                            byte[] fileContent = Files.readAllBytes(file.toPath());

                            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                                pstmt.setBytes(1, fileContent);
                                pstmt.setInt(2, empId);
                                int rows = pstmt.executeUpdate();
                                if (rows > 0) {
                                    System.out.println("✅ Uploaded: " + file.getName() + " to ID " + empId);
                                }
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("⏭️ Skipping non-ID file: " + file.getName());
                        }
                    }
                }
                System.out.println("--- MIGRATION FINISHED ---");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}