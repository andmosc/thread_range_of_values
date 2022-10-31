import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.MoreExecutors;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;


public class Main {

    private static final int SIZE = 25;
    private static final int SIZE_STRING = 30_000;
    public static void main(String[] args) throws InterruptedException {

        //расчет оптимального пула
        int poolSize = Runtime.getRuntime().availableProcessors() + 1;
        ExecutorService threadPool = Executors.newFixedThreadPool(poolSize);
        List<Future<Integer>> futures = new ArrayList<>();

        long startTs = System.currentTimeMillis(); // start time

        for (int i = 0; i < SIZE; i++) {
            futures.add(threadPool.submit(new CalcMaxInterval(generateText("aab", SIZE_STRING))));
        }

        int max = 0;
        for (Future<Integer> future : futures) {
            try {
                int maxSize  = future.get();
                if (max < maxSize)
                    max = maxSize;
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

    public static class CalcMaxInterval implements Callable<Integer> {
        private final String text;

        public CalcMaxInterval(String text) {
            this.text = text;
        }

        @Override
        public Integer call() {
            int maxSize = 0;
            for (int i = 0; i < SIZE_STRING; i++) {
                for (int j = 0; j < SIZE_STRING; j++) {
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
            }
            System.out.println(text.substring(0, 100) + " -> " + maxSize);
            return maxSize;
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
