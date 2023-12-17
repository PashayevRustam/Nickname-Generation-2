package ru.netology;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {

    private static final AtomicInteger countLength3 = new AtomicInteger(0);
    private static final AtomicInteger countLength4 = new AtomicInteger(0);
    private static final AtomicInteger countLength5 = new AtomicInteger(0);

    private static final BlockingQueue<String> queueA = new ArrayBlockingQueue<>(100);
    private static final BlockingQueue<String> queueB = new ArrayBlockingQueue<>(100);
    private static final BlockingQueue<String> queueC = new ArrayBlockingQueue<>(100);

    public static void main(String[] args) {
        Random random = new Random();

        Thread generatorThread = new Thread(() -> {
            for (int i = 0; i < 100_000; i++) {
                String text = generateText("abc", 3 + random.nextInt(3));
                try {
                    queueA.put(text);
                    queueB.put(text);
                    queueC.put(text);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        Thread threadA = new Thread(() -> countBeautifulWords(3, queueA, countLength3));
        Thread threadB = new Thread(() -> countBeautifulWords(4, queueB, countLength4));
        Thread threadC = new Thread(() -> countBeautifulWords(5, queueC, countLength5));

        generatorThread.start();
        threadA.start();
        threadB.start();
        threadC.start();

        try {
            generatorThread.join();
            threadA.join();
            threadB.join();
            threadC.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Красивых слов с длиной 3: " + countLength3 + " шт");
        System.out.println("Красивых слов с длиной 4: " + countLength4 + " шт");
        System.out.println("Красивых слов с длиной 5: " + countLength5 + " шт");
    }

    private static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }

    private static void countBeautifulWords(int length, BlockingQueue<String> queue, AtomicInteger counter) {
        while (true) {
            try {
                String text = queue.take();
                if (text.length() == length) {
                    switch (length) {
                        case 3:
                            // Проверка на палиндром
                            boolean isPalindrome3 = true;
                            for (int i = 0; i <= length / 2; i++) {
                                if (text.charAt(i) != text.charAt(length - i - 1)) {
                                    isPalindrome3 = false;
                                    break;
                                }
                            }
                            if (isPalindrome3) {
                                counter.incrementAndGet();
                            }
                            break;
                        case 4:
                            // Проверка на одинаковые буквы
                            boolean isSameChar4 = true;
                            char firstChar = text.charAt(0);
                            for (int i = 1; i < length; i++) {
                                if (text.charAt(i) != firstChar) {
                                    isSameChar4 = false;
                                    break;
                                }
                            }
                            if (isSameChar4) {
                                counter.incrementAndGet();
                            }
                            break;
                        case 5:
                            // Проверка на возрастающий порядок букв
                            boolean isIncreasingOrder5 = true;
                            for (char c = 'a'; c <= 'z'; c++) {
                                int count = 0;
                                for (int i = 0; i < length; i++) {
                                    if (text.charAt(i) == c) {
                                        count++;
                                    }
                                }
                                if (count > 0 && text.indexOf(c) != -1 && text.indexOf(c) + count != text.lastIndexOf(c)) {
                                    isIncreasingOrder5 = false;
                                    break;
                                }
                            }
                            if (isIncreasingOrder5) {
                                counter.incrementAndGet();
                            }
                            break;
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }
    }
}
