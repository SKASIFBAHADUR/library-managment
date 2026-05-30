package service;

import entity.Book;
import repository.BookRepository;
import java.util.List;
import java.util.logging.Logger;

public class BookService {
    private static final Logger LOGGER = Logger.getLogger(BookService.class.getName());
    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public void addBook(Book book) {
        bookRepository.save(book);
        LOGGER.info("Book Added: " + book.getIsbn() + " - " + book.getTitle());
    }

    public void removeBook(String isbn) {
        Book book = bookRepository.findByIsbn(isbn);
        if (book != null) {
            bookRepository.delete(isbn);
            LOGGER.info("Book Removed: " + book.getIsbn() + " - " + book.getTitle());
        } else {
            LOGGER.warning("Attempted to remove non-existent book with ISBN: " + isbn);
        }
    }

    public void updateBook(String isbn, String title, String author, int publicationYear) {
        Book book = bookRepository.findByIsbn(isbn);
        if (book != null) {
            book.setTitle(title);
            book.setAuthor(author);
            book.setPublicationYear(publicationYear);
            bookRepository.save(book);
            LOGGER.info("Book Updated: " + book.getIsbn() + " - " + book.getTitle());
        } else {
            LOGGER.warning("Attempted to update non-existent book with ISBN: " + isbn);
        }
    }

    public Book getBookByIsbn(String isbn) {
        return bookRepository.findByIsbn(isbn);
    }

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }
}
