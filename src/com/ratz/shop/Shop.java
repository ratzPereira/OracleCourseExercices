package com.ratz.shop;

import com.ratz.shop.data.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

public class Shop {


    public static void main(String[] args) {

        ProductManager pm = new ProductManager("en-GB");

        pm.createNewProduct(101,"Cookie",BigDecimal.valueOf(2.99),Rating.NOT_RATED);
        pm.printProductReport(101);
        pm.createNewProduct(102,"Chicla",BigDecimal.valueOf(99.33),Rating.NOT_RATED);
        pm.reviewProduct(102, Rating.ONE_STAR, "Awesome");
        pm.reviewProduct(102, Rating.THREE_STAR, "Awesome");
        pm.reviewProduct(102, Rating.ONE_STAR, "Awesome");
        pm.reviewProduct(102, Rating.FIVE_STAR, "Awesome");
        pm.printProductReport(102);
    }
}
