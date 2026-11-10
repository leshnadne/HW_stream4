package hw4;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Main {
    private static final int TEXT_COUNT = 10_000;
    private static final int TEXT_LENGTH = 100_000;
    private static final int QUEUE_CAPACITY = 100;

    public static BlockingQueue<String> queueA = new ArrayBlockingQueue<>(QUEUE_CAPACITY);
    public static BlockingQueue<String> queueB = new ArrayBlockingQueue<>(QUEUE_CAPACITY);
    public static BlockingQueue<String> queueC = new ArrayBlockingQueue<>(QUEUE_CAPACITY);

    public static void main(String[] args) throws InterruptedException {

        Thread generatorThread = new Thread(() -> {
            for (int i = 0; i < TEXT_COUNT; i++) {
                String text = generateText("abc", TEXT_LENGTH);
                try {
                    queueA.put(text);
                    queueB.put(text);
                    queueC.put(text);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        });

        Thread threadA = createAnalyzerThread(queueA, 'a');
        Thread threadB = createAnalyzerThread(queueB, 'b');
        Thread threadC = createAnalyzerThread(queueC, 'c');

        generatorThread.start();
        threadA.start();
        threadB.start();
        threadC.start();

        generatorThread.join();
        threadA.join();
        threadB.join();
        threadC.join();
    }

    private static Thread createAnalyzerThread(BlockingQueue<String> queue, char targetChar) {
        return new Thread(() -> {
            int maxCount = 0;
            String maxText = "";

            for (int i = 0; i < TEXT_COUNT; i++) {
                try {
                    String text = queue.take();
                    int count = countChar(text, targetChar);
                    if (count > maxCount) {
                        maxCount = count;
                        maxText = text;
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
            System.out.printf("Максимум символов '%c : %d (начало текста: %s...)%n",
                    targetChar, maxCount, maxText.substring(0, 50));
        });
    }

    private static int countChar(String text, char target) {
        int count = 0;
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == target) {
                count++;
            }
        }
        return count;
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
