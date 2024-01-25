package com.booleanuk.extension;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Basket {
    private ArrayList<Product> basket;
    private int capacity;
     Inventory inventoryProduct;
     HashMap<String, Integer> productQuantities;

    public Basket( int capacity ){
        this.basket = new ArrayList<>();
        this.capacity = capacity;
        this.inventoryProduct = new Inventory();
    }

    public boolean addItem(Product item) {
        if (isFull()) {
            return false;
        }
        else if(!isItemInInventory(item)){
            System.out.println("Product is not in our inventory!");
            return false;
        }
        else if(item.getQuantity() > capacity){
            System.out.println("The quantity of the product exceeds the basket's capacity. Please try adding fewer items!");
            return false;
        }
        this.basket.add(item);
        System.out.println(item + " added successfully!");
        return true;
    }

    public String remove(Product item){
        if(this.basket.isEmpty()){
            return "Basket is empty";
        }
        if(!this.basket.contains(item)){
            return item + " is not in the basket!";
        }
        this.basket.remove(item);
        return item +" removed from basket";
    }

    public boolean isFull(){
        if( this.basket.size() >= capacity){
            System.out.println("Basket is full");
            return true;
        }
        return false;

    }

    public String changeCapacity(int newCapacity){
        this.basket.ensureCapacity(newCapacity);
        return "Basket size is updated to " + newCapacity;
    }


    public double getTotalCost(){
        double total = 0.0;
        for(Product item : basket){
           total+= item.getPrice();
        }
        BigDecimal roundedTotal = BigDecimal.valueOf(total).setScale(2, RoundingMode.HALF_UP);
        return roundedTotal.doubleValue();
    }


    public double getItemCost(Bagel item){
        return item.getPrice();
    }


    public String addingFillingWhenBagelInBasket( Filling filling) {
        boolean bagelInBasket = false;
        for(Product item : this.basket){
            if(item instanceof Bagel){
                bagelInBasket = true;
                break;
            }
        }
        if(bagelInBasket){
            this.basket.add(filling);
            return filling + " is added";
        }else {
            return "Please select a bagel before adding filling";
        }
    }


    public double getFillingCost(Filling item){
        return item.getPrice();
    }

    public boolean isItemInInventory(Product item) {
       return inventoryProduct.getInventoryItem().contains(item);
    }


    public String printReceipt(){
        // StringBuilder to construct the receipt
        StringBuilder receipt = new StringBuilder();

        // Static content for the receipt
        String title =   "~~~ Bob's Bagels ~~~";
        String line = "-----------------------------------";
        String endOfReceipt ="Thank you for your order!";

        // Get the current date and time
        LocalDateTime localDate = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String dateTime = localDate.format(formatter);

        receipt.append(title).append(System.lineSeparator());
        receipt.append(dateTime).append(System.lineSeparator());
        receipt.append(line).append(System.lineSeparator());

        // Todo: how to display total quantities and total prices of similar items in one line
        // Todo: display name variant for discounted product
        Map<String, Integer> productCountMap = new HashMap<>();
        Map<String, Double> productTotalPriceMap = new HashMap<>();
        // Loop through the products in the basket
        for (Product product : basket) {
            if(productCountMap.containsKey(product.getSKU())){
                //Increment the count if the product is already in the map
                productCountMap.put(product.getSKU(), productCountMap.get(product.getSKU()) +1 );
            }else {
                //Otherwise add the product to the map with count 1
                productCountMap.put(product.getSKU(),1);
            }
            //Update the total price map by multiplying the price of each product with quantity
            double totalPrice = product.getPrice() * productCountMap.get(product.getSKU());
            productTotalPriceMap.put(product.getSKU(),totalPrice);

        }
        //Print the results
        for(Map.Entry<String, Integer> entry : productCountMap.entrySet()){
            String product = entry.getKey();
            int quantity = entry.getValue();
            double totalPrice = productTotalPriceMap.get(product);
            String productInfo = String.format("%-18s %4s £%.2f", product , quantity, totalPrice);
            receipt.append(productInfo).append(System.lineSeparator());
        }
        receipt.append(line);
        receipt.append(System.lineSeparator());
        receipt.append(String.format("%-23s £%.2f","Total ",getTotalCost()));
        receipt.append(System.lineSeparator());
        receipt.append(endOfReceipt);
        return receipt.toString();
    }


    public static void main(String[] arg){
        Basket basket = new Basket(30);
        Product bagel1 = new Bagel("BGLP",0.39, "Bagel", "Plain" );
        Product bagel2 = new Bagel("BGLP",0.39, "Bagel", "Plain" );
        Product bagel3 = new Bagel("BGLP",0.39, "Bagel", "Plain" );
        Product bagel4 = new Bagel("BGLP",0.39, "Bagel", "Plain" );


        Product filling = new Filling("FILB",0.12, "Filling", "Bacon");
        Product quantity = new QuantityDiscountProduct("BGLO", 2.49, "Bagel", "Onion", 6);
        basket.addItem(bagel1);
        basket.addItem(bagel2);
        basket.addItem(bagel3);
        basket.addItem(bagel4);


        basket.addItem(filling);
        basket.addItem(quantity);
        System.out.println(basket.getTotalCost());
        System.out.println(basket.printReceipt());

    }

}
