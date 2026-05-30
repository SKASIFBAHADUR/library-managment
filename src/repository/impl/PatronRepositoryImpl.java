package repository.impl;

import entity.Patron;
import repository.PatronRepository;
import java.util.*;

public class PatronRepositoryImpl implements PatronRepository {
    private final Map<String, Patron> patrons = new HashMap<>();

    @Override
    public void save(Patron patron) {
        if (patron != null && patron.getPatronId() != null) {
            patrons.put(patron.getPatronId(), patron);
        }
    }

    @Override
    public Patron findById(String patronId) {
        return patrons.get(patronId);
    }

    @Override
    public List<Patron> findAll() {
        return new ArrayList<>(patrons.values());
    }
}
