package com.ratz.shop.data;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.*;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ProductManager {

    private static final Logger logger = Logger.getLogger(ProductManager.class.getName());

    private ResourceFormatter formatter;
    private Product product;

    private Map<Product, List<Review>> products = new HashMap<>();
    private static  Map<String, ResourceFormatter> formatters = Map.of("en-GB", new ResourceFormatter(Locale.UK),
            "en-US", new ResourceFormatter(Locale.US),"fr-FR", new ResourceFormatter(Locale.FRANCE),"zh-CN", new ResourceFormatter(Locale.CHINA));


    public ProductManager(Locale locale) {
        this(locale.toLanguageTag());
    }

    public ProductManager(String languageTag) {
        changeLocale(languageTag);
    }


    public Product createNewProduct(int id, String name, BigDecimal price, Rating  rating, LocalDate bestBefore){

        Product product = new Food(id,name,price,rating,bestBefore);

        //will only add if the product is new! we already have the method equals overiden to check if the product is the same
        products.putIfAbsent(product, new ArrayList<>());

        return product;
    }

    public Product createNewProduct(int id, String name, BigDecimal price, Rating  rating){

        Product product = new Drink(id,name,price,rating);

        //will only add if the product is new! we already have the method equals overiden to check if the product is the same
        products.putIfAbsent(product, new ArrayList<>());

        return product;
    }

    public Product reviewProduct(Product product, Rating rating, String comments) {

        List<Review> reviews = products.get(product);
        products.remove(product, reviews);

        reviews.add(new Review(rating,comments));

        product = product.applyRating(Rateable.convert((int)Math.round(reviews.stream().mapToInt(r -> r.getRating().ordinal()).average().orElse(0))));


//        int sum = 0;
//        for(Review review: reviews) {
//            sum += review.getRating().ordinal();
//        }
//        product = product.applyRating(Rateable.convert(Math.round((float)sum / reviews.size())));


        products.put(product,reviews);

        return product;
    }

    public Product reviewProduct(int id, Rating rating, String comments) {

        try {

            return reviewProduct(searchProduct(id), rating, comments);
        } catch (ProductManagerException e) {

            logger.log(Level.INFO, e.getMessage());
        }

        return null;
    }


    public void printProductReport(int id) {

        try {

            printProductReport(searchProduct(id));
        } catch (ProductManagerException e) {

            logger.log(Level.INFO,  e.getMessage());
        }
    }


    public void printProducts(Predicate<Product> filter, Comparator<Product> sorter) {

//        List<Product> productList = new ArrayList<>(products.keySet());
//        productList.sort(sorter);

        StringBuilder txt = new StringBuilder();

        products.keySet().stream().sorted(sorter).filter(filter).forEach( p -> txt.append(formatter.formatProduct(p) + '\n'));
//        for(Product product: productList) {
//            txt.append(formatter.formatProduct(product));
//            txt.append("\n");
//        }

        System.out.println(txt);
    }


    public void printProductReport(Product product) {

        StringBuilder txt = new StringBuilder();
        txt.append( formatter.formatProduct(product) );
        txt.append("\n");

        List<Review> reviews = products.get(product);

        Collections.sort(reviews);


        if(reviews.isEmpty()){

            txt.append(formatter.getText("no.reviews"));
            txt.append("\n");
        } else {

            txt.append(reviews.stream().map(p -> formatter.formatReview(p)+ '\n').collect(Collectors.joining()));
        }

        System.out.println(txt);
//        for (Review review: reviews) {
//
//            txt.append( formatter.formatReview(review) );
//            txt.append("\n");
//
//
//        }
//            if(reviews.isEmpty()) {
//
//                txt.append(formatter.getText("no.reviews"));
//                txt.append("\n");
//
//        }


    }


    public void changeLocale(String languageTag) {

        formatter = formatters.getOrDefault(languageTag,formatters.get("en-GB"));
    }


    public static Set<String> getSupportedLocales() {
        return formatters.keySet();
    }


    public Product searchProduct(int id) throws ProductManagerException {

        return products.keySet().stream().filter(p -> p.getId() == id).findFirst().orElseThrow(() -> new ProductManagerException("Product with id " + id + " not found"));

//        Product result = null;
//
//        for(Product product: products.keySet()) {
//
//            if(product.getId() == id) {
//                result = product;
//                break;
//            }
//        }
//        return result;
    }

    public Map<String,String> getDiscounts() {

        return products.keySet().stream().collect(Collectors.groupingBy(product -> product.getRating().getStars(),
                Collectors.collectingAndThen(Collectors.summarizingDouble( product -> product.getDiscount().doubleValue())
                        , discount -> formatter.moneyFormat.format(discount))));
    }

    private static class ResourceFormatter {

        private Locale locale;
        private ResourceBundle resources;
        private DateTimeFormatter dateFormat;
        private NumberFormat moneyFormat;


        private ResourceFormatter(Locale locale) {

            this.locale = locale;
            resources = ResourceBundle.getBundle("com.ratz.shop.data/resources", locale);
            dateFormat = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).localizedBy(locale);
            moneyFormat = NumberFormat.getCurrencyInstance(locale);
        }

        private String formatProduct(Product product) {

            return MessageFormat.format(resources.getString("product"),
                    product.getName(),
                    moneyFormat.format(product.getPrice()),
                    product.getRating().getStars(),
                    dateFormat.format(product.getBestBefore()));
        }

        private String formatReview( Review review) {

            return  MessageFormat.format(resources.getString("review"), review.getRating().getStars(), review.getComments());
        }

        private String getText(String key) {

            return  resources.getString(key);
        }

    }

}
