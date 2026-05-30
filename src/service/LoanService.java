package service;

import entity.Book;
import entity.Loan;
import entity.Patron;
import enums.BookStatus;
import observer.ReservationManager;
import repository.BookRepository;
import repository.LoanRepository;
import repository.PatronRepository;
import util.IdGenerator;
import java.time.LocalDate;
import java.util.List;
import java.util.logging.Logger;

public class LoanService {
    private static final Logger LOGGER = Logger.getLogger(LoanService.class.getName());
    private final BookRepository bookRepository;
    private final PatronRepository patronRepository;
    private final LoanRepository loanRepository;
    private final ReservationManager reservationManager;

    public LoanService(BookRepository bookRepository, PatronRepository patronRepository,
                       LoanRepository loanRepository, ReservationManager reservationManager) {
        this.bookRepository = bookRepository;
        this.patronRepository = patronRepository;
        this.loanRepository = loanRepository;
        this.reservationManager = reservationManager;
    }

    public Loan checkoutBook(String isbn, String patronId) {
        Book book = bookRepository.findByIsbn(isbn);
        if (book == null) {
            LOGGER.warning("Checkout failed: Book not found with ISBN " + isbn);
            return null;
        }

        if (book.getStatus() != BookStatus.AVAILABLE) {
            LOGGER.warning("Checkout failed: Book with ISBN " + isbn + " is not AVAILABLE (Status: " + book.getStatus() + ")");
            return null;
        }

        Patron patron = patronRepository.findById(patronId);
        if (patron == null) {
            LOGGER.warning("Checkout failed: Patron not found with ID " + patronId);
            return null;
        }

        String loanId = IdGenerator.generateLoanId();
        Loan loan = new Loan(loanId, book, patron, LocalDate.now());
        book.setStatus(BookStatus.BORROWED);
        patron.addLoanToHistory(loan);

        bookRepository.save(book);
        patronRepository.save(patron);
        loanRepository.save(loan);

        LOGGER.info("Checkout: Book '" + book.getTitle() + "' (ISBN: " + isbn + ") checked out to Patron '" + patron.getName() + "' (ID: " + patronId + ")");
        return loan;
    }

    public boolean returnBook(String isbn) {
        Book book = bookRepository.findByIsbn(isbn);
        if (book == null) {
            LOGGER.warning("Return failed: Book not found with ISBN " + isbn);
            return false;
        }

        Loan activeLoan = null;
        for (Loan loan : loanRepository.findAll()) {
            if (loan.getBook().getIsbn().equals(isbn) && !loan.isReturned()) {
                activeLoan = loan;
                break;
            }
        }

        if (activeLoan == null) {
            LOGGER.warning("Return failed: No active loan found for Book ISBN " + isbn);
            return false;
        }

        activeLoan.setReturned(true);
        activeLoan.setReturnDate(LocalDate.now());
        book.setStatus(BookStatus.AVAILABLE);

        bookRepository.save(book);
        loanRepository.save(activeLoan);

        LOGGER.info("Return: Book '" + book.getTitle() + "' (ISBN: " + isbn + ") returned by Patron '" + activeLoan.getPatron().getName() + "'");

        // Notify reservation observers if any are waiting
        if (reservationManager.hasReservations(isbn)) {
            reservationManager.notifyObservers(book);
        }

        return true;
    }

    public List<Loan> getAllLoans() {
        return loanRepository.findAll();
    }
}
