import java.util.*;
import java.util.concurrent.*;

public class Main {

    public static final int THREADS = 30;
    public static final int ITERATIONS = 100000;
    public static final double NSEC = 1000_000_000.0;
    public static final int MAP_SIZE = 50;
    public static final int SAMPLES = 5;

    public static Map<String, Integer> hashMap = new HashMap<>();
    public static Map<String, Integer> hashTable = new Hashtable<>();
    public static Map<String, Integer> syncMap = Collections.synchronizedMap(new HashMap<>());
    public static Map<String, Integer> cHashMap = new ConcurrentHashMap<>();

    public static void main(String[] args) {

        System.out.println("Collections:");
        double hashMapTime = compute(hashMap) / NSEC;
        double hashTableTime = compute(hashTable) / NSEC;
        double syncMapTime = compute(syncMap) / NSEC;
        double cHashMapTime = compute(cHashMap) / NSEC;

        System.out.println("Execution times:");
        System.out.printf("\tHashMap: %.3f s,\n\tHashTable: %.3f s,\n\tSyncMap: %.3f s,\n\tConcurrentHashMap: %.3f s.%n",
                hashMapTime, hashTableTime, syncMapTime, cHashMapTime);
    }

    private static long compute(Map<String, Integer> map) {

        System.out.printf("\t%s", map.getClass().getName());

        long start;
        long stop;
        long totalTime = 0;

        for (int k = 0; k < SAMPLES; k++) {
            map.clear();
            start = System.nanoTime();

            List<Callable<String>> tasks = new ArrayList<>();
            List<Future<String>> results;

            // create a list of tasks
            for (int i = 0; i < THREADS; i++) {
                tasks.add(() -> {
                    String threadName = Thread.currentThread().getName();

                    for (int j = 0; j < ITERATIONS; j++) {
                        String keyStr = String.valueOf(ThreadLocalRandom.current().nextInt(MAP_SIZE));

                        try {
                            map.merge(keyStr, 1, Integer::sum);
                        }
                        catch (ConcurrentModificationException exception) {
                            System.out.println("Error during access in collection:" + map.getClass().getName() + " by " + threadName);
                        }
                    }

                    return "Thread " + threadName + " done";
                });
            }

            try (ExecutorService executorService = Executors.newFixedThreadPool(THREADS)) {
                // invoke all the tasks
                try {
                    results = executorService.invokeAll(tasks);
                    for (Future<String> result : results) {
                        result.get(); //ожидается завершение потоков
                    }
                } catch (InterruptedException | ExecutionException ie) {
                    ie.printStackTrace();
                    Thread.currentThread().interrupt();
                }
            }

            stop = System.nanoTime();
            totalTime += (stop-start);

            int totalSum = 0;
            for (Integer value : map.values()) {
                if (value != null) {
                    totalSum += value;
                }
            }

        }

        System.out.println("...done.");

        return totalTime;
    }
}