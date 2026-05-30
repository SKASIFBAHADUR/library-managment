package repository;

import entity.Book;
import java.util.List;

public interface BookRepository {
    void save(Book book);
    void delete(String isbn);
    Book findByIsbn(String isbn);
    List<Book> findAll();
}
