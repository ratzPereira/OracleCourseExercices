package com.ratz.shop.data;

import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.sql.Date;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;


public class ProductManager {

    private final ResourceBundle config = ResourceBundle.getBundle("com.ratz.shop.data/config");

    private final Path reportsFolder = Path.of(config.getString("reports.folder"));
    private final Path dataFolder = Path.of(config.getString("data.folder"));
    private final Path tempFolder = Path.of(config.getString("temp.folder"));

    private static final Logger logger = Logger.getLogger(ProductManager.class.getName());

    //private ResourceFormatter formatter;
    //private Product product;

    private final MessageFormat reviewFormat = new MessageFormat(config.getString("review.data.format"));
    private final MessageFormat productFormat = new MessageFormat(config.getString("product.data.format"));

    private Map<Product, List<Review>> products = new HashMap<>();

    private static final Map<String, ResourceFormatter> formatters = Map.of(
            "en-GB", new ResourceFormatter(Locale.UK),
            "en-US", new ResourceFormatter(Locale.US),
            "fr-FR", new ResourceFormatter(Locale.FRANCE),
            "zh-CN", new ResourceFormatter(Locale.CHINA));

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final Lock writeLock = lock.writeLock();
    private final Lock readLock = lock.readLock();


    private static final ProductManager pm = new ProductManager();


    private ProductManager() {
        loadAllData();
    }

    //we create one single instance of productManager and we share that same instance with this public method
    public static ProductManager getInstance(){
        return pm;
    }

    public Product createNewProduct(int id, String name, BigDecimal price, Rating  rating, LocalDate bestBefore){

        Product product = null;

        try {
            writeLock.lock();
            product = new Food(id,name,price,rating,bestBefore);
            //will only add if the product is new! we already have the method equals overiden to check if the product is the same
            products.putIfAbsent(product, new ArrayList<>());

        } catch (Exception e) {
            logger.log(Level.INFO, "error adding product " + e.getMessage());
            return null;

        } finally {
            writeLock.unlock();
        }

        return product;
    }

    public Product createNewProduct(int id, String name, BigDecimal price, Rating  rating){

        Product product = null;
        try {

            writeLock.lock();
            product = new Drink(id,name,price,rating);
            //will only add if the product is new! we already have the method equals overiden to check if the product is the same
            products.putIfAbsent(product, new ArrayList<>());

        } catch (Exception e) {
            logger.log(Level.INFO, "Error adding product " + e.getMessage());
        } finally {
            writeLock.unlock();
        }

        return product;
    }

    private Product reviewProduct(Product product, Rating rating, String comments) {

        List<Review> reviews = products.get(product);
        products.remove(product, reviews);

        reviews.add(new Review(rating,comments));

        product = product.applyRating(Rateable.convert((int)Math.round(reviews.stream().mapToInt(r -> r.getRating().ordinal()).average().orElse(0))));

        products.put(product,reviews);

        return product;
    }


    public Product reviewProduct(int id, Rating rating, String comments) {

        try {

            writeLock.lock();
            return reviewProduct(searchProduct(id), rating, comments);
        } catch (ProductManagerException e) {

            logger.log(Level.INFO, e.getMessage());
        } finally {
            writeLock.unlock();
        }

        return null;
    }


    public void printProductReport(int id, String languageTag, String client) {

        try {
            readLock.lock();
            printProductReport(searchProduct(id), languageTag, client);
        } catch (ProductManagerException e) {

            logger.log(Level.SEVERE, "Error printing product report " +  e.getMessage(), e);

        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            readLock.unlock();
        }
    }


    public void printProducts(Predicate<Product> filter, Comparator<Product> sorter, String languageTag) {

        try {
            readLock.lock();
            ResourceFormatter formatter = formatters.getOrDefault(languageTag,formatters.get("en-GB"));

            StringBuilder txt = new StringBuilder();

            products.keySet().stream().sorted(sorter).filter(filter).forEach( p -> txt.append(formatter.formatProduct(p) + '\n'));

            System.out.println(txt);
        } finally {

            readLock.unlock();
        }

    }


    private void printProductReport(Product product, String languageTag, String client) throws IOException {

        List<Review> reviews = products.get(product);
        Collections.sort(reviews);

        ResourceFormatter formatter = formatters.getOrDefault(languageTag,formatters.get("en-GB"));


        Path productFile = reportsFolder.resolve(MessageFormat.format(config.getString("report.file"), product.getId(), client));

        try(PrintWriter out = new PrintWriter(new OutputStreamWriter(Files.newOutputStream(productFile, StandardOpenOption.CREATE),"UTF-8"))){

        out.append( formatter.formatProduct(product)+System.lineSeparator() );

        if(reviews.isEmpty()){

            out.append(formatter.getText("no.reviews") + System.lineSeparator());

        } else {

            out.append(reviews.stream().map(p -> formatter.formatReview(p)+  System.lineSeparator()).collect(Collectors.joining()));
            }

        }
    }

    public Review parseReview(String text) {
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

    public Product parseProduct(String text) {

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


//    public void changeLocale(String languageTag) {
//
//        formatter = formatters.getOrDefault(languageTag,formatters.get("en-GB"));
//    }


    public static Set<String> getSupportedLocales() {
        return formatters.keySet();
    }


    public Product searchProduct(int id) throws ProductManagerException {

        try {
            readLock.lock();
            return products.keySet().stream().filter(p -> p.getId() == id).findFirst().orElseThrow(() -> new ProductManagerException("Product with id " + id + " not found"));

        } finally {

            readLock.unlock();
        }
    }

    public Map<String,String> getDiscounts(String languageTag) {

        try {
            readLock.lock();
            //if languageTag does not exist or invalid, en-GB will be used
            ResourceFormatter formatter = formatters.getOrDefault(languageTag, formatters.get("en-GB"));

            return products.keySet().stream().collect(Collectors.groupingBy(product -> product.getRating().getStars(),
                    Collectors.collectingAndThen(Collectors.summarizingDouble( product -> product.getDiscount().doubleValue())
                            , discount -> formatter.moneyFormat.format(discount))));
        } finally {
            readLock.unlock();
        }

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
                    .collect(Collectors.toMap(product -> product, this::loadReviews));

        } catch (IOException e) {

            logger.log(Level.SEVERE, "Error Loading Data "  + e.getMessage());
        }
    }

    public void dumpData() {

        try {

            if(Files.notExists(tempFolder)){
                Files.createDirectory(tempFolder);
            }

            Path tempFile = tempFolder.resolve(MessageFormat.format(config.getString("temp.file"), Date.valueOf(LocalDate.now()).toString().trim()));

            try(ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(tempFile, StandardOpenOption.CREATE))){

                //we take all the product with reviews from memory and dump in the file
                out.writeObject(products);
                //and we reset at the reference map to point a new empty hash map with no products
                products = new HashMap<>();
            }

        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error dumping data "  + e.getMessage(), e);
        }
    }

    //we tell the compiler that we have sure that hes reading from file that has Products and Reviews
    @SuppressWarnings("unchecked")
    public void restoreData() {

        try {

            Path tempFile = Files.list(tempFolder).filter(path -> path.getFileName().toString().endsWith("tmp")).findFirst().orElseThrow();

            try(ObjectInputStream in = new ObjectInputStream(Files.newInputStream(tempFile, StandardOpenOption.DELETE_ON_CLOSE))) {

                //we lose generics here. compiler cant check if we are really getting Products and reviews
                products =(HashMap)in.readObject();

            }

        }catch (Exception e) {
            logger.log(Level.SEVERE, "Error restoring data "  + e.getMessage(), e);
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
