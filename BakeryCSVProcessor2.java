// make different threads for each user_id and for each thread run all its queries sequencially
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class BakeryCSVProcessor2 {

       private static final String CSV_FILE = "./operations.csv";
    private static final Map<String, Integer> HEADER_MAP = new HashMap<>();
    private static final Map<Integer, BlockingQueue<Runnable>> userQueues = new ConcurrentHashMap<>();
    private static final ExecutorService executor = Executors.newCachedThreadPool();
    private static final AtomicInteger threadCounter = new AtomicInteger(0);
    private static final Map<Integer, Integer> userToThreadMap = new ConcurrentHashMap<>();
    private static final Map<Integer, List<String>> threadExecutionLog = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        long startTime = System.nanoTime();
        int queryCount = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(CSV_FILE))) {
            System.out.println("File Read!");
            
            // Parse CSV header
            String headerLine = reader.readLine();
            if (headerLine != null) {
                String[] headers = headerLine.split(",");
                for (int i = 0; i < headers.length; i++) {
                    HEADER_MAP.put(headers[i], i);
                }
                
                // First pass: collect all operations
                String operationLine;
                while ((operationLine = reader.readLine()) != null) {
                    String[] fields = operationLine.split(",", -1);
                    int userId = getIntField(fields, "UserID");
                    
                    userQueues.computeIfAbsent(userId, k -> new LinkedBlockingQueue<>());
                    userQueues.get(userId).add(() -> processOperation(userId, fields));
                    queryCount++;
                }
                
                // Second pass: submit tasks to executor
                userQueues.forEach((userId, queue) -> {
                    final int threadId = threadCounter.getAndIncrement();
                    userToThreadMap.put(userId, threadId);
                    threadExecutionLog.put(threadId, new ArrayList<>());
                    
                    executor.submit(() -> {
                        String assignmentMsg = String.format("Thread %d assigned to User %d", threadId, userId);
                        threadExecutionLog.get(threadId).add(assignmentMsg);
                        
                        while (!queue.isEmpty()) {
                            try {
                                Runnable task = queue.take();
                                task.run();
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                                System.err.println("Thread interrupted for user " + userId);
                            }
                        }
                    });
                });
            }
            
            System.out.println("All operations submitted for processing");
        } catch (Exception e) {
            System.err.println("Error processing CSV: " + e.getMessage());
            e.printStackTrace();
        } finally {
            executor.shutdown();
            try {
                if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }

        // Print thread-wise execution summary
     //   System.out.println("\n=== Thread-wise Execution Summary ===");
       // threadExecutionLog.entrySet().stream()
         //   .sorted(Map.Entry.comparingByKey())
           // .forEach(entry -> {
             //   System.out.printf("\nThread %d executed:\n", entry.getKey());
               // entry.getValue().forEach(System.out::println);
           // });

        long endTime = System.nanoTime();
        double durationMs = (endTime - startTime) / 1_000_000.0;
        System.out.printf("\nTotal queries processed: %d%n", queryCount);
        System.out.printf("Total processing time: %.3f ms%n", durationMs);
    }

    private static void processOperation(int userId, String[] fields) {
        String operation = getField(fields, "Operation");
        int threadId = userToThreadMap.get(userId);
        
        String logMessage = String.format("%s for User %d", operation, userId);
        threadExecutionLog.get(threadId).add(logMessage);
        
        System.out.printf("Thread %d executing %s\n", threadId, logMessage);
        
        switch (operation) {
            case "Create":
                handleCreate(threadId, userId, fields);
                break;
            case "Read":
                handleRead(threadId, userId, fields);
                break;
            case "Update":
                handleUpdate(threadId, userId, fields);
                break;
            case "Delete":
                handleDelete(threadId, userId, fields);
                break;
            default:
                String unknownMsg = String.format("Unknown operation: %s", operation);
                threadExecutionLog.get(threadId).add(unknownMsg);
        }
    }

    private static void handleCreate(int threadId, int userId, String[] fields) {
        String fullName = getField(fields, "FullName");
        String email = getField(fields, "Email");
        String phone = getField(fields, "PhoneNumber");
        int visits = getIntField(fields, "VisitCount");
        double totalSpent = getDoubleField(fields, "TotalSpent");
        System.out.printf("Thread %d creating user %d: %s\n", threadId, userId, fullName);
        new Create().createUser(userId, fullName, email, phone, visits, totalSpent);
    }

    private static void handleRead(int threadId, int userId, String[] fields) {
        String fullName = getField(fields, "FullName");
        System.out.printf("Thread %d reading user %d: %s\n", threadId, userId, fullName);
        new Read().readUser(userId, fullName);
    }

    private static void handleUpdate(int threadId, int userId, String[] fields) {
        int choice = getIntField(fields, "Choice");
        String newValue = getField(fields, "NewValue");
        Object value = (choice == 4) ? Double.parseDouble(newValue) : newValue;
        System.out.printf("Thread %d updating user %d (field %d)\n", threadId, userId, choice);
        new Update().updateUser(userId, choice, value);
    }

    private static void handleDelete(int threadId, int userId, String[] fields) {
        System.out.printf("Thread %d deleting user %d\n", threadId, userId);
        new Delete().deleteUser(userId);
    }

    // Helper methods remain the same
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