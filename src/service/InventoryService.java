package service;

import entity.Book;
import enums.BookStatus;
import repository.BookRepository;
import java.util.ArrayList;
import java.util.List;

public class InventoryService {
    private final BookRepository bookRepository;

    public InventoryService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public List<Book> getAvailableBooks() {
        List<Book> available = new ArrayList<>();
        for (Book book : bookRepository.findAll()) {
            if (book.getStatus() == BookStatus.AVAILABLE) {
                available.add(book);
            }
        }
        return available;
    }

    public List<Book> getBorrowedBooks() {
        List<Book> borrowed = new ArrayList<>();
        for (Book book : bookRepository.findAll()) {
            if (book.getStatus() == BookStatus.BORROWED) {
                borrowed.add(book);
            }
        }
        return borrowed;
    }
}
