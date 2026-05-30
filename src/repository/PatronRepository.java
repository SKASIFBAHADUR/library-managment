package repository;

import entity.Patron;
import java.util.List;

public interface PatronRepository {
    void save(Patron patron);
    Patron findById(String patronId);
    List<Patron> findAll();
}
