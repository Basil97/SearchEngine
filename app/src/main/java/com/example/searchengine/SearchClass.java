package com.example.searchengine;



public class SearchClass {
    String partNumber;
    String datasheet;
    String imagePath;
    String productLink;
    String description;

    public SearchClass(String partNumber, String description, String productLink, String imagePath, String datasheet) {

        this.datasheet = datasheet;
        this.imagePath = imagePath;
        this.partNumber = partNumber;
        this.productLink = productLink;
        this.description = description;

    }
    public String getPartNumber(){
        return partNumber;
    }
    public String getDatasheet(){
        return partNumber;
    }
    public String getImagePath(){
        return partNumber;
    }
    public String getProductLink(){
        return partNumber;
    }
    public String getDescription(){
        return partNumber;
    }
}