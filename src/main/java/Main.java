import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class Main {

    public static final int SIZE = 25;

    public static void main(String[] args) throws InterruptedException {

        List<Thread> threads = new ArrayList<>();
        long startTs = System.currentTimeMillis(); // start time

        for (int i = 0; i < SIZE; i++) {
            Thread thread = new Thread(() -> calcInterval(generateText("aab", 30_000)));
            thread.setName("Thread - " + i);
            thread.start();
            threads.add(thread);

        }

        for (Thread thread : threads) {
            thread.join();
        }

        long endTs = System.currentTimeMillis(); // end time
        System.out.println("Time: " + (endTs - startTs) + "ms");
    }

    public static void calcInterval(String text) {
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
        }
        System.out.println(text.substring(0, 100) + " -> " + maxSize);
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
