package observer;

import entity.Book;
import java.util.*;
import java.util.logging.Logger;

public class ReservationManager implements Subject {
    private static final Logger LOGGER = Logger.getLogger(ReservationManager.class.getName());

    private final Map<String, Queue<Observer>> reservationQueue = new HashMap<>();

    @Override
    public void registerObserver(String isbn, Observer observer) {
        reservationQueue.computeIfAbsent(isbn, k -> new LinkedList<>()).add(observer);
        LOGGER.info("Registered reservation observer for Book ISBN: " + isbn);
    }

    @Override
    public void removeObserver(String isbn, Observer observer) {
        Queue<Observer> queue = reservationQueue.get(isbn);
        if (queue != null) {
            queue.remove(observer);
            LOGGER.info("Removed reservation observer for Book ISBN: " + isbn);
        }
    }

    @Override
    public void notifyObservers(Book book) {
        if (book == null) {
            return;
        }
        Queue<Observer> queue = reservationQueue.get(book.getIsbn());
        if (queue != null && !queue.isEmpty()) {
            LOGGER.info("Notifying observers for Book ISBN: " + book.getIsbn());
            List<Observer> observersToNotify = new ArrayList<>(queue);
            queue.clear(); // Clear notifications once sent
            for (Observer observer : observersToNotify) {
                observer.update(book);
            }
        }
    }

    public boolean hasReservations(String isbn) {
        Queue<Observer> queue = reservationQueue.get(isbn);
        return queue != null && !queue.isEmpty();
    }

    public Queue<Observer> getReservations(String isbn) {
        return reservationQueue.get(isbn);
    }
}
