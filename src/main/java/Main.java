import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;


public class Main {

    private static final int SIZE = 500;

    public static void main(String[] args) throws InterruptedException {

        //расчет оптимального пула
        int poolSize = Runtime.getRuntime().availableProcessors() + 1;
        ExecutorService threadPool = Executors.newFixedThreadPool(poolSize);
        List<Future<Integer>> futures = new ArrayList<>();

        long startTs = System.currentTimeMillis(); // start time

        for (int i = 0; i < SIZE; i++) {
            futures.add(threadPool.submit(new CaclMaxInterval(generateText("aab", 30_000))));
        }

        int max = 0;
        for (Future<Integer> future : futures) {
            try {
                max = future.get();
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        }

        long endTs = System.currentTimeMillis(); // end time
        System.out.println("Time: " + (endTs - startTs) + "ms");

        System.out.println("Максимальный интервал значений: " + max);

        //One good way to shut down the ExecutorService (which is also recommended by Oracle)
        try {
            if (!threadPool.awaitTermination(100, TimeUnit.MILLISECONDS)) {
                threadPool.shutdownNow();
            }
        } catch (InterruptedException e) {
            threadPool.shutdownNow();
        }
    }

    public static class CaclMaxInterval implements Callable<Integer> {
        private final String text;
        private static int max;

        public CaclMaxInterval(String text) {
            this.text = text;
        }

        @Override
        public Integer call() throws Exception {
            int maxSize = 0;
            for (int i = 0; i < SIZE; i++) {
                for (int j = 0; j < SIZE; j++) {
                    if (i >= j) {
                        continue;
                    }
                    boolean bFound = false;
                    for (int k = i; k < j; k++) {
                        if (text.charAt(k) == 'b') {
                            bFound = true;
                            break;
                        }
                    }
                    if (!bFound && maxSize < j - i) {
                        maxSize = j - i;
                    }
                }
                if (max < maxSize)
                    max = maxSize;
            }
            System.out.println(text.substring(0, 100) + " -> " + maxSize);
            return max;
        }
    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }
}
