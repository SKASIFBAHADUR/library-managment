package strategy;

import entity.Book;
import java.util.ArrayList;
import java.util.List;

public class IsbnSearchStrategy implements SearchStrategy {
    @Override
    public List<Book> search(List<Book> books, String query) {
        List<Book> results = new ArrayList<>();
        if (query == null || query.trim().isEmpty()) {
            return results;
        }
        String cleanQuery = query.replaceAll("[\\s-]", "").toLowerCase();
        for (Book book : books) {
            String cleanIsbn = book.getIsbn().replaceAll("[\\s-]", "").toLowerCase();
            if (cleanIsbn.contains(cleanQuery)) {
                results.add(book);
            }
        }
        return results;
    }
}
