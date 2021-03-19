package com.ratz.shop.data;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Arrays;
import java.util.Locale;
import java.util.ResourceBundle;

public class ProductManager {

    private Locale locale;
    private ResourceBundle resources;
    private DateTimeFormatter dateFormat;
    private NumberFormat moneyFormat;

    private Product product;
    private Review[] reviews = new Review[5];



    public ProductManager(Locale locale) {
        this.locale = locale;
        resources = ResourceBundle.getBundle("com.ratz.shop.data/resources", locale);
        dateFormat = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).localizedBy(locale);
        moneyFormat = NumberFormat.getCurrencyInstance(locale);
    }

    public Product createNewProduct(int id, String name, BigDecimal price, Rating  rating, LocalDate bestBefore){

        product = new Food(id,name,price,rating,bestBefore);
        return product;
    }

    public Product createNewProduct(int id, String name, BigDecimal price, Rating  rating){

        product = new Drink(id,name,price,rating);
        return product;
    }

    public Product reviewProduct(Product product, Rating rating, String comments) {

        //if the last element of the array is not null, that means that the array is full, so we copy it to another bigger array!
        if(reviews[reviews.length - 1] != null) {
            reviews = Arrays.copyOf(reviews, reviews.length + 5);
        }

        int sum = 0;
        int i = 0;
        boolean reviewed = false;

        while (i < reviews.length && !reviewed){

            if(reviews[i] == null) {
                reviews[i] = new Review(rating,comments);
                reviewed = true;
            }
            //ordinal gives an int, so we can calculate the total
            sum += reviews[i].getRating().ordinal();
            i++;
        }


        this.product = product.applyRating(Rateable.convert(Math.round((float)sum/i)));

        return this.product;
    }

    public void printProductReport() {

        StringBuilder txt = new StringBuilder();
        txt.append(MessageFormat.format(resources.getString("product"),
                                        product.getName(),
                                        moneyFormat.format(product.getPrice()),
                                        product.getRating().getStars(),
                                        dateFormat.format(product.getBestBefore())));
        txt.append("\n");

        for (Review review: reviews) {

            if (review == null) {
                break;
            }

            txt.append(MessageFormat.format(resources.getString("review"), review.getRating().getStars(), review.getComments()));
            txt.append("\n");

        }
            if(reviews[0] == null) {

                txt.append(resources.getString("no.reviews"));
                txt.append("\n");

        }
            System.out.println(txt);
    }
}
