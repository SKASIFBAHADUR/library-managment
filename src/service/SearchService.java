package service;

import entity.Book;
import repository.BookRepository;
import strategy.SearchStrategy;
import java.util.List;

public class SearchService {
    private final BookRepository bookRepository;
    private SearchStrategy searchStrategy;

    public SearchService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public void setSearchStrategy(SearchStrategy searchStrategy) {
        this.searchStrategy = searchStrategy;
    }

    public List<Book> search(String query) {
        if (searchStrategy == null) {
            throw new IllegalStateException("Search strategy has not been set.");
        }
        return searchStrategy.search(bookRepository.findAll(), query);
    }
}
