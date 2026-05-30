package observer;

import entity.Book;

public interface Subject {
    void registerObserver(String isbn, Observer observer);
    void removeObserver(String isbn, Observer observer);
    void notifyObservers(Book book);
}
