package com.example.a2030books.TabelleDB;

public class Book {
    private String Title;
    private String Author;
    private String Genre;
    private String Publisher;
    private String Availability;
    private float Price;

    // Empty constructor for Firebase
    public Book() {}

    public Book(String title, String author, String genre, String publisher, String availability, float price) {
        this.Title = title;
        this.Author = author;
        this.Genre = genre;
        this.Publisher = publisher;
        this.Availability = availability;
        this.Price = price;
    }

    public String getTitle() { return Title; }

    public void setTitle(String title) { this.Title = title; }

    public String getAuthor() { return Author; }

    public void setAuthor(String author) { this.Author = author; }

    public String getGenre() { return Genre; }

    public String getPublisher() { return Publisher; }

    public String getAvailability() { return Availability; }

    public float getPrice() { return Price; }
}
