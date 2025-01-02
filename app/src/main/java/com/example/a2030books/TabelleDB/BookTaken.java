package com.example.a2030books.TabelleDB;

public class BookTaken {
    private String Title;
    private String Author;
    private String Owner;
    private String End;
    private String Type;

    public BookTaken(){}

    public BookTaken(String Title, String author, String Owner, String end, String type) {
        this.Title = Title;
        this.Author = author;
        this.Owner = Owner;
        this.End = end;
        this.Type = type;
    }

    public String getTitle() { return Title; }

    public String getAuthor() { return Author; }

    public String getOwner() { return Owner; }

    public String getEnd() { return End; }

    public String getType() { return Type; }

    public void setTitle(String Title) { this.Title = Title; }
}

