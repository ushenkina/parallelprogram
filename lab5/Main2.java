import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;


public class Main2 {

    public static final int THREADS = 4;
    public static final int COUNT = 2;
    public static MySemaphore mySemaphore = new MySemaphore(COUNT);
    public static Semaphore regularSemaphore = new Semaphore(COUNT);
    private static final AtomicInteger activeThreads = new AtomicInteger(0);
    private static final AtomicInteger maxActiveThreads = new AtomicInteger(0);

    public static void main(String[] args) {
        System.out.println("-------------------\nRegular semaphore:\n-------------------");
        runTask(regularSemaphore);
        System.out.println("--------------\nMy semaphore:\n--------------");
        runTask(mySemaphore);
    }

    private static void runTask(Semaphore semaphore) {
        ExecutorService es = Executors.newFixedThreadPool(THREADS);

        List<Callable<String>> tasks = new ArrayList<>();
        List<Future<String>> results = new ArrayList<>();

        for (int i = 0; i < THREADS; i++) {
            tasks.add(() -> {
                String threadName = Thread.currentThread().getName();

                semaphore.acquire();
                try {
                    int currentActive = activeThreads.incrementAndGet();
                    if (currentActive > maxActiveThreads.get()) {
                        maxActiveThreads.set(currentActive);
                    }
                    System.out.println("Поток " + threadName + " работает. Активных потоков: " + currentActive);
                    Thread.sleep(1000); // Симуляция работы
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                } finally {
                    activeThreads.decrementAndGet();
                    semaphore.release();
                }


                return "Thread " + threadName + " done";
            });
        }

        // invoke all the tasks
        try {
            results = es.invokeAll(tasks);
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }

        // shutdown executor service
        es.shutdown();
    }
}