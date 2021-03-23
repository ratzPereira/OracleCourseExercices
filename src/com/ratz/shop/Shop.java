package com.ratz.shop;

import com.ratz.shop.data.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

public class Shop {


    public static void main(String[] args) {

        ProductManager pm = new ProductManager("en-GB");
        //pm.printProductReport(103);
        pm.createNewProduct(101,"Cookie",BigDecimal.valueOf(2.99),Rating.NOT_RATED);
        pm.printProductReport(101);
        pm.createNewProduct(102,"Chicla",BigDecimal.valueOf(99.33),Rating.NOT_RATED);
        pm.reviewProduct(102, Rating.ONE_STAR, "Awwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwesome");
        pm.reviewProduct(102, Rating.THREE_STAR, "Awwwwwwwwwwwwwwwwwwwwwwwwwwwwesome");
        pm.reviewProduct(102, Rating.ONE_STAR, "Awwwwwwwwwwwwwesome");
        pm.reviewProduct(102, Rating.FIVE_STAR, "Awwwesome");
        pm.printProductReport(101);
        pm.printProductReport(102);

        pm.printProducts(p->p.getPrice().floatValue() < 111, (p1,p2) -> p2.getRating().ordinal() - p1.getRating().ordinal());
        //pm.getDiscounts().forEach((rating, discount) -> System.out.println(rating + "\t" + discount));


        pm.dumpData();
        pm.restoreData();

        pm.printProductReport(101);

        //pm.printProductReport(103);
    }
}
