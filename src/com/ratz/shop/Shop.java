package com.ratz.shop;

import com.ratz.shop.data.Drink;
import com.ratz.shop.data.Food;
import com.ratz.shop.data.Product;
import com.ratz.shop.data.Rating;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Shop {

    public static void main(String[] args) {


        Product p1 = new Product(100, "Tea", BigDecimal.valueOf(1.99));
        Product p2 = new Drink(102,"Coffe",BigDecimal.valueOf(1.99), Rating.TWO_STAR);
        Product p3 = new Food(104,"Cake",BigDecimal.valueOf(3.99), Rating.ONE_STAR, LocalDate.now().plusDays(2));
        Product p4 = new Product();
        Product p5 = p3.applyRating(Rating.THREE_STAR);



//        System.out.println("P1 data is : " + p1.getId() + "  " + p1.getName() + " " + p1.getPrice() + " " + p1.getDiscount() + " " + p1.getRating());
//        System.out.println("P2 data is : " + p2.getId() + "  " + p2.getName() + " " + p2.getPrice() + " " + p2.getDiscount() + " " + p2.getRating());
//        System.out.println("P3 data is : " + p3.getId() + "  " + p3.getName() + " " + p3.getPrice() + " " + p3.getDiscount() + " " + p3.getRating());
//        System.out.println("P4 data is : " + p4.getId() + "  " + p4.getName() + " " + p4.getPrice() + " " + p4.getDiscount() + " " + p4.getRating());

        System.out.println(p1);
        System.out.println(p2);
        System.out.println(p3);
        System.out.println(p4);
        System.out.println(p5);
        System.out.println(LocalDate.now().plusDays(2));
    }
}
