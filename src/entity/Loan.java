package entity;

import java.time.LocalDate;

public class Loan {
    private String loanId;
    private Book book;
    private Patron patron;
    private LocalDate checkoutDate;
    private LocalDate returnDate;
    private boolean isReturned;

    public Loan(String loanId, Book book, Patron patron, LocalDate checkoutDate) {
        this.loanId = loanId;
        this.book = book;
        this.patron = patron;
        this.checkoutDate = checkoutDate;
        this.isReturned = false;
    }

    public String getLoanId() {
        return loanId;
    }

    public void setLoanId(String loanId) {
        this.loanId = loanId;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public Patron getPatron() {
        return patron;
    }

    public void setPatron(Patron patron) {
        this.patron = patron;
    }

    public LocalDate getCheckoutDate() {
        return checkoutDate;
    }

    public void setCheckoutDate(LocalDate checkoutDate) {
        this.checkoutDate = checkoutDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public boolean isReturned() {
        return isReturned;
    }

    public void setReturned(boolean returned) {
        isReturned = returned;
    }

    @Override
    public String toString() {
        return String.format("Loan[ID: %s, Book Title: '%s' (ISBN: %s), Patron: %s, Checkout: %s, Return: %s, Status: %s]",
                loanId, book.getTitle(), book.getIsbn(), patron.getName(), checkoutDate,
                returnDate != null ? returnDate.toString() : "Not returned",
                isReturned ? "Returned" : "Active");
    }
}
