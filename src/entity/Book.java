package entity;

import enums.BookStatus;

public class Book {
    private String isbn;
    private String title;
    private String author;
    private int publicationYear;
    private BookStatus status;
    private String branchId;

    public Book(String isbn, String title, String author, int publicationYear, BookStatus status) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.publicationYear = publicationYear;
        this.status = status;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getPublicationYear() {
        return publicationYear;
    }

    public void setPublicationYear(int publicationYear) {
        this.publicationYear = publicationYear;
    }

    public BookStatus getStatus() {
        return status;
    }

    public void setStatus(BookStatus status) {
        this.status = status;
    }

    public String getBranchId() {
        return branchId;
    }

    public void setBranchId(String branchId) {
        this.branchId = branchId;
    }

    @Override
    public String toString() {
        return String.format("Book[ISBN: %s, Title: '%s', Author: %s, Year: %d, Status: %s, Branch: %s]",
                isbn, title, author, publicationYear, status, branchId != null ? branchId : "None");
    }
}
