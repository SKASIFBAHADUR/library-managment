package service;

import entity.Book;
import entity.Loan;
import entity.Patron;
import repository.BookRepository;
import java.util.*;

public class RecommendationService {
    private final BookRepository bookRepository;

    public RecommendationService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public List<Book> getRecommendations(Patron patron) {
        if (patron == null) {
            return Collections.emptyList();
        }

        List<Loan> history = patron.getBorrowingHistory();
        if (history.isEmpty()) {
            return Collections.emptyList();
        }

        Set<String> borrowedIsbns = new HashSet<>();
        Set<String> borrowedAuthors = new HashSet<>();
        Set<String> keywords = new HashSet<>();

        Set<String> stopWords = new HashSet<>(Arrays.asList(
            "the", "a", "an", "and", "or", "but", "in", "on", "at", "to", "for", "with", "by", "of", "is", "are", "was", "were", "it", "this", "that"
        ));

        for (Loan loan : history) {
            Book book = loan.getBook();
            borrowedIsbns.add(book.getIsbn());
            borrowedAuthors.add(book.getAuthor().toLowerCase().trim());
            
            String[] titleWords = book.getTitle().toLowerCase().split("\\s+");
            for (String word : titleWords) {
                word = word.replaceAll("[^a-zA-Z0-9]", "");
                if (!word.isEmpty() && !stopWords.contains(word)) {
                    keywords.add(word);
                }
            }
        }

        List<Book> allBooks = bookRepository.findAll();
        Map<Book, Integer> recommendationScores = new HashMap<>();

        for (Book book : allBooks) {
            if (borrowedIsbns.contains(book.getIsbn())) {
                continue;
            }

            int score = 0;

            if (borrowedAuthors.contains(book.getAuthor().toLowerCase().trim())) {
                score += 5;
            }

            String[] titleWords = book.getTitle().toLowerCase().split("\\s+");
            for (String word : titleWords) {
                word = word.replaceAll("[^a-zA-Z0-9]", "");
                if (keywords.contains(word)) {
                    score += 2;
                }
            }

            if (score > 0) {
                recommendationScores.put(book, score);
            }
        }

        List<Book> recommendedBooks = new ArrayList<>(recommendationScores.keySet());
        recommendedBooks.sort((b1, b2) -> recommendationScores.get(b2).compareTo(recommendationScores.get(b1)));

        return recommendedBooks;
    }
}
