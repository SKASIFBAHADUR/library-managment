package entity;

import observer.Observer;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class Patron implements Observer {
    private static final Logger LOGGER = Logger.getLogger(Patron.class.getName());

    private String patronId;
    private String name;
    private String email;
    private List<Loan> borrowingHistory;

    public Patron(String patronId, String name, String email) {
        this.patronId = patronId;
        this.name = name;
        this.email = email;
        this.borrowingHistory = new ArrayList<>();
    }

    public String getPatronId() {
        return patronId;
    }

    public void setPatronId(String patronId) {
        this.patronId = patronId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Loan> getBorrowingHistory() {
        return borrowingHistory;
    }

    public void addLoanToHistory(Loan loan) {
        this.borrowingHistory.add(loan);
    }

    @Override
    public void update(Book book) {
        LOGGER.info(String.format("Notification sent to Patron %s (%s): Book '%s' (ISBN: %s) is now available!",
                name, email, book.getTitle(), book.getIsbn()));
        System.out.printf("[NOTIFICATION] Hello %s, the book '%s' (ISBN: %s) is now AVAILABLE for checkout!%n",
                name, book.getTitle(), book.getIsbn());
    }

    @Override
    public String toString() {
        return String.format("Patron[ID: %s, Name: %s, Email: %s, Borrowed Books count: %d]",
                patronId, name, email, borrowingHistory.size());
    }
}
