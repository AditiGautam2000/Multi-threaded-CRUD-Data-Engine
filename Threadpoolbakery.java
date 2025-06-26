import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;
import java.util.concurrent.*;

public class Threadpoolbakery {

    private static final String CSV_FILE = "./operations.csv";
    private static final Map<String, Integer> HEADER_MAP = new HashMap<>();
    private static final int THREAD_POOL_SIZE = 6; // You can tune this (e.g., 4, 6, 8)

    public static void main(String[] args) {
        long startTime = System.nanoTime();

        List<String[]> operations = new ArrayList<>();

        // 1. Read all queries into a list
        try (BufferedReader reader = new BufferedReader(new FileReader(CSV_FILE))) {
            System.out.println("File Read !");
            String headerLine = reader.readLine();
            if (headerLine != null) {
                String[] headers = headerLine.split(",");
                for (int i = 0; i < headers.length; i++) {
                    HEADER_MAP.put(headers[i], i);
                } // id->0, name->1, email->2........
                String operationLine;
                while ((operationLine = reader.readLine()) != null) {    //operationLine=each row in csv file
                    operations.add(operationLine.split(",", -1));  // {[row1],[row2],[row3],row[4],......}
                }
            }
        } catch (Exception e) {
            System.err.println("Error reading CSV: " + e.getMessage());
            e.printStackTrace();
            return;
        }

        // 2. Create thread pool and submit tasks
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        List<Future<?>> futures = new ArrayList<>();

        // === MODIFIED SECTION START ===
        int seq = 0; // Sequence number for each query
        for (String[] fields : operations) {
            final int queryId = seq++; // Assign a unique ID to each query
            futures.add(executor.submit(() -> {
                // Print when the query starts, with its ID and thread name
                System.out.println("Starting query #" + queryId + " on thread " + Thread.currentThread().getName());
                processOperation(fields);
                // Print when the query finishes, with its ID and thread name
                System.out.println("Finished query #" + queryId + " on thread " + Thread.currentThread().getName());
            }));
        }
        // === MODIFIED SECTION END ===

        // 3. Wait for all threads to finish
        executor.shutdown();
        try {
            if (!executor.awaitTermination(10, TimeUnit.MINUTES)) {
                System.err.println("Timeout waiting for tasks to finish.");
            }
        } catch (InterruptedException e) {
            System.err.println("Thread pool interrupted.");
        }

        long endTime = System.nanoTime();
        double durationMs = (endTime - startTime) / 1_000_000.0;
        System.out.println("All operations processed successfully");
        System.out.printf("Total queries processed: %d%n", operations.size());
        System.out.printf("Total processing time: %.3f ms%n", durationMs);
    }

    // Your existing processOperation, handleCreate, handleRead, handleUpdate, handleDelete, etc.

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
        System.out.println("Reading Starts-------------------------------------------------------");
        new Read().readUser(userId, fullName);
    }

    private static void handleUpdate(String[] fields) {
        int userId = getIntField(fields, "UserID");
        int choice = getIntField(fields, "Choice");
        String newValue = getField(fields, "NewValue");
        Object value = (choice == 4) ? Double.parseDouble(newValue) : newValue;
        System.out.println("Updating-------------------------------------------------------");
        new Update().updateUser(userId, choice, value);
    }

    private static void handleDelete(String[] fields) {
        int userId = getIntField(fields, "UserID");
        System.out.println("Delete-------------------------------------------------------");
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
