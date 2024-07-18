import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Main {
    public static final int COUNT_GENERATED_TEXT = 1000;
    public static final Map<Integer, Integer> sizeToFreq = new HashMap<>();

    public static void main(String[] args) throws InterruptedException {
            Thread thread = new Thread(() -> {
                while (!Thread.interrupted()) {
                    synchronized (sizeToFreq) {
                        try {
                            sizeToFreq.wait();
                        } catch (InterruptedException e) {
                            break;
                        }
                        printResult();
                    }
                }
            });
        thread.start();

        for (int i = 0; i < COUNT_GENERATED_TEXT; i++) {
            Thread thread1 = new Thread(() -> {
                String text = generateText("abaca", 100);
                int num = 0;
                for (int j = 0; j < text.length(); j++) {
                    if (text.charAt(j) == 'a') {
                        num++;
                    }
                }
                synchronized (sizeToFreq) {
                    updateMap(num);
                    sizeToFreq.notify();
                }
            });
            thread1.start();
            thread1.join();
        }
        thread.interrupt();
        thread.join();
    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }

    public static void updateMap(int num) {
        sizeToFreq.put(num, sizeToFreq.getOrDefault(num, 0) + 1);
    }

    public static void printResult() {
        int count = 0;
        int maxFreqSize = 0;
        for (Map.Entry<Integer, Integer> entry : sizeToFreq.entrySet()) {
            if (entry.getValue() > count) {
                count = entry.getValue();
                maxFreqSize = entry.getKey();
            }
        }
        System.out.println("Самая частая частота " + maxFreqSize + " (встретился " + count + " раз)");
        System.out.println("Другие размеры:");
        for (Map.Entry<Integer, Integer> entry : sizeToFreq.entrySet()) {
            if (entry.getKey() != maxFreqSize) {
                System.out.println("- " + entry.getKey() + " (" + entry.getValue() + " раз)");
            }
        }
    }
}