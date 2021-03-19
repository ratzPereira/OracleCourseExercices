package com.ratz.shop;

import com.ratz.shop.data.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Locale;

public class Shop {

    public static void main(String[] args) {

        ProductManager pm = new ProductManager(Locale.UK);

        Product p1 = pm.createNewProduct(100, "Tea", BigDecimal.valueOf(1.99), Rating.NOT_RATED);
//        Product p2 = pm.createNewProduct(102,"Coffe",BigDecimal.valueOf(1.99), Rating.TWO_STAR);
//        Product p3 = pm.createNewProduct(104,"Cake",BigDecimal.valueOf(3.99), Rating.ONE_STAR, LocalDate.now().plusDays(2));
//        Product p4 = pm.createNewProduct(106, "Cookie", BigDecimal.valueOf(3.99), Rating.FOUR_STAR, LocalDate.now());
//        Product p6 = pm.createNewProduct(105,"Chocolate",BigDecimal.valueOf(2.99),Rating.FIVE_STAR);
//        Product p7 = pm.createNewProduct(105,"Chocolate",BigDecimal.valueOf(2.99),Rating.FIVE_STAR, LocalDate.now().plusDays(2));
//
//        Product p5 = p3.applyRating(Rating.THREE_STAR);
//        Product p8 = p4.applyRating(Rating.FIVE_STAR);
//        Product p9 = p1.applyRating(Rating.TWO_STAR);
//
//
//
////        System.out.println("P1 data is : " + p1.getId() + "  " + p1.getName() + " " + p1.getPrice() + " " + p1.getDiscount() + " " + p1.getRating());
////        System.out.println("P2 data is : " + p2.getId() + "  " + p2.getName() + " " + p2.getPrice() + " " + p2.getDiscount() + " " + p2.getRating());
////        System.out.println("P3 data is : " + p3.getId() + "  " + p3.getName() + " " + p3.getPrice() + " " + p3.getDiscount() + " " + p3.getRating());
////        System.out.println("P4 data is : " + p4.getId() + "  " + p4.getName() + " " + p4.getPrice() + " " + p4.getDiscount() + " " + p4.getRating());
//
//        System.out.println(p1);
//        System.out.println(p2);
//        System.out.println(p3);
//        System.out.println(p4);
//        System.out.println(p5);
//        System.out.println(LocalDate.now().plusDays(2));
//        System.out.println(p6.equals(p7));
//        System.out.println(p8);
//        System.out.println(p9);

        pm.printProductReport();

        p1 = pm.reviewProduct(p1, Rating.FOUR_STAR, "Nice and hot!");
        p1 = pm.reviewProduct(p1, Rating.FIVE_STAR, "Best evah!!");
        p1 = pm.reviewProduct(p1, Rating.TWO_STAR, "Not bad and not good!");
        p1 = pm.reviewProduct(p1, Rating.TWO_STAR, "Not good and not bad!");
        pm.printProductReport();
    }
}
