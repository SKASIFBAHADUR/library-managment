package repository;

import entity.Loan;
import java.util.List;

public interface LoanRepository {
    void save(Loan loan);
    Loan findById(String loanId);
    List<Loan> findAll();
}
