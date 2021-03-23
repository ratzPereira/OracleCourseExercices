package com.ratz.shop.data;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.Format;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.*;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.zip.DataFormatException;

public class ProductManager {

    private ResourceBundle config = ResourceBundle.getBundle("com.ratz.shop.data/config");

    private Path reportsFolder = Path.of(config.getString("reports.folder"));
    private Path dataFolder = Path.of(config.getString("data.folder"));
    private Path tempFolder = Path.of(config.getString("temp.folder"));

    private static final Logger logger = Logger.getLogger(ProductManager.class.getName());

    private ResourceFormatter formatter;
    private Product product;

    private MessageFormat reviewFormat = new MessageFormat(config.getString("review.data.format"));
    private MessageFormat productFormat = new MessageFormat(config.getString("product.data.format"));

    private Map<Product, List<Review>> products = new HashMap<>();

    private static  Map<String, ResourceFormatter> formatters = Map.of(
            "en-GB", new ResourceFormatter(Locale.UK),
            "en-US", new ResourceFormatter(Locale.US),
            "fr-FR", new ResourceFormatter(Locale.FRANCE),
            "zh-CN", new ResourceFormatter(Locale.CHINA));


    public ProductManager(Locale locale) {
        this(locale.toLanguageTag());
    }

    public ProductManager(String languageTag) {

        changeLocale(languageTag);
        loadAllData();
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

            logger.log(Level.SEVERE, "Error printing product report" +  e.getMessage(), e);

        } catch (IOException e) {
            e.printStackTrace();
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


    public void printProductReport(Product product) throws IOException {

        List<Review> reviews = products.get(product);
        Collections.sort(reviews);


        Path productFile = reportsFolder.resolve(MessageFormat.format(config.getString("report.file"), product.getId()));

        try(PrintWriter out = new PrintWriter(new OutputStreamWriter(Files.newOutputStream(productFile, StandardOpenOption.CREATE),"UTF-8"))){

        out.append( formatter.formatProduct(product)+System.lineSeparator() );

        if(reviews.isEmpty()){

            out.append(formatter.getText("no.reviews") + System.lineSeparator());

        } else {

            out.append(reviews.stream().map(p -> formatter.formatReview(p)+  System.lineSeparator()).collect(Collectors.joining()));
            }

        }
    }

    private Review parseReview(String text) {
        Review review = null;
        try {
            Object[] values = reviewFormat.parse(text);

            //we just want the stars and comments
            review = new Review(Rateable.convert(Integer.parseInt((String)values[0])), (String)values[1]);

        } catch (ParseException | NumberFormatException e) {
            logger.log(Level.WARNING, "Error parsing review " + text);
        }

        return review;
    }

    private Product parseProduct(String text) {

        Product product = null;
        try {

            Object[] values = productFormat.parse(text);

            int id = Integer.parseInt((String) values[1]);
            String name = (String)values[2];
            BigDecimal price = BigDecimal.valueOf(Double.parseDouble((String)values[3]));
            Rating rating = Rateable.convert(Integer.parseInt((String)values[4]));

            switch ((String)values[0]){
                case "D":
                    product = new Drink(id,name,price,rating);
                    break;
                case "F":
                    LocalDate date = LocalDate.parse((String)values[5]);
                    product = new Food(id,name,price,rating,date);
            }

        } catch (ParseException | NumberFormatException e) {

            logger.log(Level.WARNING, "Error parsing product " + text + " " + e.getMessage());
        }

        return product;
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


    private List<Review> loadReviews (Product product) {

        List<Review> reviews = null;

        //we will read out reviews from here
        Path file = dataFolder.resolve(MessageFormat.format(config.getString("reviews.data.file"), product.getId()));

        if(Files.notExists(file)) {

            reviews = new ArrayList<>();
        } else {

            try {
                reviews= Files.lines(file, Charset.forName("UTF-8"))
                        .map(this::parseReview)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
            } catch (IOException e) {
                logger.log(Level.WARNING, "Error Loading the reviews "  + e.getMessage());
            }
        }
        return reviews;
    }


    private Product loadProduct(Path file) {

        Product product = null;

        try {
            product = parseProduct(Files.lines(dataFolder.resolve(file), Charset.forName("UTF-8")).findFirst().orElseThrow());

        } catch (Exception e) {

            logger.log(Level.WARNING, "Error Loading the Product " + e.getMessage());
        }

        return product;
    }


    private void loadAllData(){

        try {
            products =  Files.list(dataFolder)
                    .filter(file -> file.getFileName().toString().startsWith("product"))
                    .map(this::loadProduct)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toMap(product -> product, product -> loadReviews(product)));

        } catch (IOException e) {

            logger.log(Level.SEVERE, "Error Loading Data "  + e.getMessage());
        }
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
