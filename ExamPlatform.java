import java.util.*;

class Submission {
    String studentId;
    int estTime;
    long arrivalTime;

    public Submission(String studentId, int estTime, long arrivalTime) {
        this.studentId = studentId;
        this.estTime = estTime;
        this.arrivalTime = arrivalTime;
    }
}

public class ExamPlatform {
    private static long deadline = Long.MAX_VALUE;
    private static long currentTime = 0; // Simulated clock
    private static final int STARVATION_THRESHOLD = 300; 

    // Priority 1: Starvation Lane (FIFO)
    private static Queue<Submission> starvationQueue = new LinkedList<>();
    
    // Priority 2: Shortest Job First (Min-Heap)
    private static PriorityQueue<Submission> sjfHeap = new PriorityQueue<>(
        Comparator.comparingInt(s -> s.estTime)
    );

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("--- Online Exam Backend Initialized ---");

        while (true) {
            System.out.print("> ");
            String input = scanner.nextLine();
            if (input.equalsIgnoreCase("EXIT")) break;

            String[] parts = input.split(" ");
            String command = parts[0].toUpperCase();

            try {
                switch (command) {
                    case "SET_DEADLINE":
                        deadline = Long.parseLong(parts[1]);
                        System.out.println("Deadline set to: " + deadline);
                        break;

                    case "SUBMIT":
                        handleSubmit(parts);
                        break;

                    case "PROCESS":
                        processCycle();
                        break;

                    case "STATUS":
                        showStatus();
                        break;

                    default:
                        System.out.println("Unknown command.");
                }
            } catch (Exception e) {
                System.out.println("Invalid input format.");
            }
        }
    }
    private static void handleSubmit(String[] parts) {
        String id = parts[1];
        int est = Integer.parseInt(parts[2]);
        long time = Long.parseLong(parts[3]);

        if (time > deadline) {
            System.out.println("Error: Rejected. Past Deadline.");
            return;
        }

        currentTime = time; // Update system clock to last submission
        sjfHeap.add(new Submission(id, est, time));
        System.out.println("Submission accepted from " + id);
    }

    private static void processCycle() {
        checkStarvation();

        if (!starvationQueue.isEmpty()) {
            Submission s = starvationQueue.poll();
            System.out.println("Grading " + s.studentId + " (Priority: Starvation Lane)");
        } else if (!sjfHeap.isEmpty()) {
            Submission s = sjfHeap.poll();
            System.out.println("Grading " + s.studentId + " (Priority: Shortest Job)");
        } else {
            System.out.println("No submissions to process.");
        }
    }
    private static void checkStarvation() {
        // Move submissions that have waited > 5 mins from Heap to Starvation Queue
        Iterator<Submission> it = sjfHeap.iterator();
        List<Submission> toPromote = new ArrayList<>();

        while (it.hasNext()) {
            Submission s = it.next();
            if (currentTime - s.arrivalTime > STARVATION_THRESHOLD) {
                toPromote.add(s);
                it.remove();
            }
        }
        // Add to FIFO queue in order of arrival time to maintain fairness
        toPromote.sort(Comparator.comparingLong(s -> s.arrivalTime));
        starvationQueue.addAll(toPromote);
    }
    private static void showStatus() {
        System.out.println("--- Current Status ---");
        System.out.println("Current Time: " + currentTime);
        System.out.println("Starvation Queue: " + starvationQueue.size());
        System.out.println("SJF Heap: " + sjfHeap.size());
    }
}