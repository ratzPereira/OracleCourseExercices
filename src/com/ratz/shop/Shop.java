package com.ratz.shop;

import com.ratz.shop.data.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

public class Shop {


    public static void main(String[] args) {

        ProductManager pm = new ProductManager(Locale.UK);
        ProductManager pm2 = new ProductManager("en-GB");


//        pm.createNewProduct(103,"Cake", BigDecimal.valueOf(6.99), Rating.NOT_RATED, LocalDate.now().plusDays(2));
//        pm.reviewProduct(103, Rating.FIVE_STAR, "wow");
//        pm.reviewProduct(103, Rating.THREE_STAR, "Too much sugar");
//        pm.reviewProduct(103, Rating.THREE_STAR, "It makes me fat");
          pm.parseProduct("D,101,Tea,1.99,0,2021-02-12");
          pm.printProductReport(101);
        //pm.printProductReport(10);
//        pm.parseReview("103,4,Nice hot");
//        pm.printProductReport(103);
//
//        pm.createNewProduct(104,"Cookie", BigDecimal.valueOf(2.99), Rating.NOT_RATED, LocalDate.now().plusDays(2));
//        pm.reviewProduct(104, Rating.ONE_STAR, "No chocolate :(");
//        pm.reviewProduct(104, Rating.THREE_STAR, " Kinda good but not cheap ");
//        pm.reviewProduct(104, Rating.THREE_STAR, " Good but small ");
//
//        pm.printProductReport(104);
//
//        pm.createNewProduct(105,"Sopa", BigDecimal.valueOf(8.99), Rating.NOT_RATED, LocalDate.now().plusDays(2));
//        pm.reviewProduct(105, Rating.ONE_STAR, "I hate This");
//        pm.reviewProduct(105, Rating.ONE_STAR, "Worst ever");
//        pm.reviewProduct(105, Rating.TWO_STAR, "Very bad");
//
//        pm.printProductReport(105);


//
//        //using Comparator
//        Comparator<Product> ratingShorter = (p1, p2) -> p2.getRating().ordinal() - p1.getRating().ordinal();
//        pm.printProducts(ratingShorter);
//
//        //using Comparator
//        Comparator<Product> ratingCheaper = (p1, p2) -> p2.getPrice().compareTo(p1.getPrice());
//        pm.printProducts(ratingCheaper.thenComparing(ratingShorter));
//
//        //we can do the comparators at same time
//        pm.printProducts(ratingCheaper.thenComparing(ratingShorter));

        //Directly
//        pm.printProducts((p1,p2)-> p2.getPrice().compareTo(p1.getPrice()));
        pm.printProducts(p->p.getPrice().floatValue() < 2 ,(p1,p2)-> p2.getRating().ordinal() - p1.getRating().ordinal());

    }
}
