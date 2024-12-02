package com.example.a2030books;

public class Book {
    private String title;
    private String author;
    private String genre;
    private String publisher;
    private String availability;
    private float price;

    // Empty constructor for Firebase
    public Book() {}

    public Book(String title, String author, String genre, String publisher, String availability, float price) {
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.publisher = publisher;
        this.availability = availability;
        this.price = price;
    }

    public String getTitle() { return title; }

    public String getAuthor() { return author; }

    public String getGenre() { return genre; }

    public String getPublisher() { return publisher; }

    public String getAvailability() { return availability; }

    public float getPrice() { return price; }
}
