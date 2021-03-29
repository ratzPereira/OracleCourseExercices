package com.ratz.shop;

import com.ratz.shop.data.*;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Shop {


    public static void main(String[] args) {
        ProductManager pm = ProductManager.getInstance();

        AtomicInteger clientCount = new AtomicInteger();


        Callable<String> client = () -> {
            String clientId = "Client" + clientCount.incrementAndGet();
            String threadName = Thread.currentThread().getName();
            int productId = ThreadLocalRandom.current().nextInt(63)+101;

            String languageTag = ProductManager.getSupportedLocales()
                    .stream()
                    .skip(ThreadLocalRandom.current().nextInt(4))
                    .findFirst().get();

            StringBuilder log = new StringBuilder();
            log.append(clientId+threadName + "\n- \tstart of log \t -\n");


            Product product = pm.reviewProduct(productId,Rating.FOUR_STAR, "Oh yeah Boy");
            log.append((product != null) ? "\nProduct " + productId + " Reviewed" : "\n Product " + productId + " not reviewed ");

            pm.printProductReport(productId,clientId,languageTag);
            log.append(clientId + " generated report for " + productId + " product");
            log.append("\n- \tEnd of log \t -\n");

            return log.toString();
        };

        List<Callable<String>> clients = Stream.generate(() -> client).limit(5).collect(Collectors.toList());
        ExecutorService executorService = Executors.newFixedThreadPool(3);

        try {

            List<Future<String>> results = executorService.invokeAll(clients);
            executorService.shutdown();
            results.forEach(result -> {
                try {
                    System.out.println(result.get());

                } catch (InterruptedException | ExecutionException e){
                    Logger.getLogger(Shop.class.getName()).log(Level.SEVERE, "error getting result", e);
                }
            });
        } catch (InterruptedException e) {
            Logger.getLogger(Shop.class.getName()).log(Level.SEVERE, "error invoking clients", e);
        }
    }
}
