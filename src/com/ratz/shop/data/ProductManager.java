package com.ratz.shop.data;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ProductManager {

    public Product createNewProduct(int id, String name, BigDecimal price, Rating  rating, LocalDate bestBefore){

        return new Food(id,name,price,rating,bestBefore);
    }

    public Product createNewProduct(int id, String name, BigDecimal price, Rating  rating){

        return new Drink(id,name,price,rating);
    }
}
