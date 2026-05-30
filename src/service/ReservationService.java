package service;

import entity.Book;
import entity.Patron;
import enums.BookStatus;
import observer.ReservationManager;
import java.util.logging.Logger;

public class ReservationService {
    private static final Logger LOGGER = Logger.getLogger(ReservationService.class.getName());
    private final ReservationManager reservationManager;
    private final BookService bookService;

    public ReservationService(ReservationManager reservationManager, BookService bookService) {
        this.reservationManager = reservationManager;
        this.bookService = bookService;
    }

    public boolean reserveBook(String isbn, Patron patron) {
        Book book = bookService.getBookByIsbn(isbn);
        if (book == null) {
            LOGGER.warning("Reservation failed: Book not found with ISBN " + isbn);
            return false;
        }
        if (patron == null) {
            LOGGER.warning("Reservation failed: Patron is null");
            return false;
        }

        reservationManager.registerObserver(isbn, patron);

        if (book.getStatus() == BookStatus.AVAILABLE) {
            book.setStatus(BookStatus.RESERVED);
            bookService.addBook(book);
        }

        LOGGER.info("Reservation: Patron '" + patron.getName() + "' reserved Book '" + book.getTitle() + "' (ISBN: " + isbn + ")");
        return true;
    }

    public boolean cancelReservation(String isbn, Patron patron) {
        Book book = bookService.getBookByIsbn(isbn);
        if (book == null || patron == null) {
            return false;
        }
        reservationManager.removeObserver(isbn, patron);
        LOGGER.info("Reservation Cancelled: Patron '" + patron.getName() + "' for Book '" + book.getTitle() + "'");
        return true;
    }
}
