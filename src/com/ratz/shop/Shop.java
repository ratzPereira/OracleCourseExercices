package com.ratz.shop;

import com.ratz.shop.data.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

public class Shop {


    public static void main(String[] args) {

        ProductManager pm = ProductManager.getInstance();
        //pm.printProductReport(103);
        pm.createNewProduct(101,"Cookie",BigDecimal.valueOf(2.99),Rating.NOT_RATED);
        pm.printProductReport(101, "en-GB");
        pm.createNewProduct(102,"Chicla",BigDecimal.valueOf(99.33),Rating.NOT_RATED);
        pm.reviewProduct(102, Rating.ONE_STAR, "Awwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwesome");
        pm.reviewProduct(102, Rating.THREE_STAR, "Awwwwwwwwwwwwwwwwwwwwwwwwwwwwesome");
        pm.reviewProduct(102, Rating.ONE_STAR, "Awwwwwwwwwwwwwesome");
        pm.reviewProduct(102, Rating.FIVE_STAR, "Awwwesome");
        pm.printProductReport(101,"en-GB");
        pm.printProductReport(102,"en-GB");


        //pm.getDiscounts().forEach((rating, discount) -> System.out.println(rating + "\t" + discount));


        pm.dumpData();
        pm.restoreData();

        pm.printProductReport(101,"en-GB");


    }
}
