package dao;

public class CSVUtils {
    
public static String[] splitCSVLine(String line) {

    return line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
}

    public static String clean(String s) {
        return (s == null) ? "" : s.trim().replace("\"", "");
    }

   public static double parseCurrency(String val) {
    if (val == null || val.trim().isEmpty() || val.equalsIgnoreCase("N/A")) return 0.0;
    return Double.parseDouble(val.replace(",", "").trim());
}
}