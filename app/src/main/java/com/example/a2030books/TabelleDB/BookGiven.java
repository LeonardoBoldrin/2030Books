package com.example.a2030books.TabelleDB;

public class BookGiven extends Book{

    private String OtherUser;
    private String End;

    public BookGiven(){}

    public BookGiven(String title, String otherUser, String author, String end) {
        super.setTitle(title);
        super.setAuthor(author);
        this.OtherUser = otherUser;
        this.End = end;
    }

    public String getOtherUser() { return OtherUser; }

    public String getEnd() { return End; }
}
