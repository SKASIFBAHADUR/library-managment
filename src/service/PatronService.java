package service;

import entity.Loan;
import entity.Patron;
import repository.PatronRepository;
import util.IdGenerator;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

public class PatronService {
    private static final Logger LOGGER = Logger.getLogger(PatronService.class.getName());
    private final PatronRepository patronRepository;

    public PatronService(PatronRepository patronRepository) {
        this.patronRepository = patronRepository;
    }

    public Patron registerPatron(String name, String email) {
        String patronId = IdGenerator.generatePatronId();
        Patron patron = new Patron(patronId, name, email);
        patronRepository.save(patron);
        LOGGER.info("Patron Registered: " + patron.getPatronId() + " - " + patron.getName());
        return patron;
    }

    public void updatePatron(String patronId, String name, String email) {
        Patron patron = patronRepository.findById(patronId);
        if (patron != null) {
            patron.setName(name);
            patron.setEmail(email);
            patronRepository.save(patron);
            LOGGER.info("Patron Updated: " + patron.getPatronId() + " - " + patron.getName());
        } else {
            LOGGER.warning("Attempted to update non-existent patron with ID: " + patronId);
        }
    }

    public Patron getPatronById(String patronId) {
        return patronRepository.findById(patronId);
    }

    public List<Patron> getAllPatrons() {
        return patronRepository.findAll();
    }

    public List<Loan> getBorrowingHistory(String patronId) {
        Patron patron = patronRepository.findById(patronId);
        if (patron != null) {
            return patron.getBorrowingHistory();
        }
        LOGGER.warning("Attempted to view borrowing history for non-existent patron with ID: " + patronId);
        return Collections.emptyList();
    }
}
