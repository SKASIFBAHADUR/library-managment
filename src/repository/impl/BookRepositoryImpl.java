package repository.impl;

import entity.Book;
import repository.BookRepository;
import java.util.*;

public class BookRepositoryImpl implements BookRepository {
    private final Map<String, Book> books = new HashMap<>();

    @Override
    public void save(Book book) {
        if (book != null && book.getIsbn() != null) {
            books.put(book.getIsbn(), book);
        }
    }

    @Override
    public void delete(String isbn) {
        books.remove(isbn);
    }

    @Override
    public Book findByIsbn(String isbn) {
        return books.get(isbn);
    }

    @Override
    public List<Book> findAll() {
        return new ArrayList<>(books.values());
    }
}
