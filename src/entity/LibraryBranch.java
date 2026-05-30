package entity;

import java.util.ArrayList;
import java.util.List;

public class LibraryBranch {
    private String branchId;
    private String name;
    private List<Book> books;

    public LibraryBranch(String branchId, String name) {
        this.branchId = branchId;
        this.name = name;
        this.books = new ArrayList<>();
    }

    public String getBranchId() {
        return branchId;
    }

    public void setBranchId(String branchId) {
        this.branchId = branchId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Book> getBooks() {
        return books;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }

    public void addBook(Book book) {
        if (!this.books.contains(book)) {
            this.books.add(book);
            book.setBranchId(this.branchId);
        }
    }

    public void removeBook(Book book) {
        if (this.books.remove(book)) {
            book.setBranchId(null);
        }
    }

    @Override
    public String toString() {
        return String.format("LibraryBranch[ID: %s, Name: %s, Book Count: %d]",
                branchId, name, books.size());
    }
}
