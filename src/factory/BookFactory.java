package factory;

import entity.Book;
import enums.BookStatus;

public class BookFactory {
    
    public static Book createBook(String isbn, String title, String author, int publicationYear) {
        return new Book(isbn, title, author, publicationYear, BookStatus.AVAILABLE);
    }
}
