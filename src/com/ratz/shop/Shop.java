package com.ratz.shop;

import com.ratz.shop.data.*;

import java.math.BigDecimal;
import java.util.*;

public class Shop {

    public static void main(String[] args) {

        ProductManager pm = new ProductManager(Locale.UK);
        ProductManager pm2 = new ProductManager("en-GB");

        Product p1 = pm.createNewProduct(100, "Tea", BigDecimal.valueOf(1.99), Rating.NOT_RATED);
        Product p2 = pm2.createNewProduct(122,"Cofe",BigDecimal.valueOf(5.99),Rating.FIVE_STAR);


        pm.printProductReport(p1);

        p1 = pm.reviewProduct(p1, Rating.FOUR_STAR, "Nice and hot!");
        p1 = pm.reviewProduct(p1, Rating.FIVE_STAR, "Best evah!!");
        p1 = pm.reviewProduct(p1, Rating.TWO_STAR, "Not bad and not good!");
        p1 = pm.reviewProduct(p1, Rating.TWO_STAR, "Not good and not bad!");

        pm.printProductReport(100);
        pm.printProductReport(p1);
        pm2.printProductReport(p2);
    }
}
