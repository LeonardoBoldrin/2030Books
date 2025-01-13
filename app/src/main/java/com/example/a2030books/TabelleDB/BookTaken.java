package com.example.a2030books.TabelleDB;

public class BookTaken extends Book{

    private String Owner;
    private String End;
    private String Type;

    public BookTaken(){}

    public BookTaken(String Title, String author, String Owner, String end, String type) {
        super.setTitle(Title);
        super.setAuthor(author);
        this.Owner = Owner;
        this.End = end;
        this.Type = type;
    }

    public String getOwner() { return Owner; }

    public String getEnd() { return End; }
}

