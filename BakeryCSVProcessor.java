// takes one by one query
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

public class BakeryCSVProcessor {

    private static final String CSV_FILE = "./operations.csv";
    private static final Map<String, Integer> HEADER_MAP = new HashMap<>();

    public static void main(String[] args) {
        long startTime = System.nanoTime(); // Start timing
        int queryCount = 0; // Initialize query counter

        try (BufferedReader reader = new BufferedReader(new FileReader(CSV_FILE))) {
            System.out.println("File Read !");
            // Parse CSV header
            String headerLine = reader.readLine();
            if (headerLine != null) {
                String[] headers = headerLine.split(",");
                for (int i = 0; i < headers.length; i++) {
                    HEADER_MAP.put(headers[i], i);
                }
                // Process each operation line
                String operationLine;
                while ((operationLine = reader.readLine()) != null) {
                    processOperation(operationLine.split(",", -1));
                    queryCount++; // Increment for each operation processed
                }
            }
            System.out.println("All operations processed successfully");
        } catch (Exception e) {
            System.err.println("Error processing CSV: " + e.getMessage());
            e.printStackTrace();
        }

        long endTime = System.nanoTime(); // End timing
        double durationMs = (endTime - startTime) / 1_000_000.0;
        System.out.printf("Total queries processed: %d%n", queryCount);
        System.out.printf("Total processing time: %.3f ms%n", durationMs);
    }

    private static void processOperation(String[] fields) {
        String operation = getField(fields, "Operation");
        switch (operation) {
            case "Create":
                handleCreate(fields);
                break;
            case "Read":
                handleRead(fields);
                break;
            case "Update":
                handleUpdate(fields);
                break;
            case "Delete":
                handleDelete(fields);
                break;
            default:
                System.out.println("Skipping unknown operation: " + operation);
        }
    }

    private static void handleCreate(String[] fields) {
        int userId = getIntField(fields, "UserID");
        String fullName = getField(fields, "FullName");
        String email = getField(fields, "Email");
        String phone = getField(fields, "PhoneNumber");
        int visits = getIntField(fields, "VisitCount");
        double totalSpent = getDoubleField(fields, "TotalSpent");
        System.out.println("Creating-------------------------------------------------------");
        new Create().createUser(userId, fullName, email, phone, visits, totalSpent);
    }

    private static void handleRead(String[] fields) {
        int userId = getIntField(fields, "UserID");
        String fullName = getField(fields, "FullName");
        new Read().readUser(userId, fullName);
    }

    private static void handleUpdate(String[] fields) {
        int userId = getIntField(fields, "UserID");
        int choice = getIntField(fields, "Choice");
        String newValue = getField(fields, "NewValue");
        Object value = (choice == 4) ? Double.parseDouble(newValue) : newValue;
        new Update().updateUser(userId, choice, value);
    }

    private static void handleDelete(String[] fields) {
        int userId = getIntField(fields, "UserID");
        new Delete().deleteUser(userId);
    }

    // Helper methods for field access
    private static String getField(String[] fields, String column) {
        Integer index = HEADER_MAP.get(column);
        return (index != null && index < fields.length) ? fields[index] : "";
    }

    private static int getIntField(String[] fields, String column) {
        String value = getField(fields, column);
        return value.isEmpty() ? 0 : Integer.parseInt(value);
    }
                
    private static double getDoubleField(String[] fields, String column) {
        String value = getField(fields, column);
        return value.isEmpty() ? 0.0 : Double.parseDouble(value);
    }
}







