package repository.impl;

import entity.Loan;
import repository.LoanRepository;
import java.util.*;

public class LoanRepositoryImpl implements LoanRepository {
    private final Map<String, Loan> loans = new HashMap<>();

    @Override
    public void save(Loan loan) {
        if (loan != null && loan.getLoanId() != null) {
            loans.put(loan.getLoanId(), loan);
        }
    }

    @Override
    public Loan findById(String loanId) {
        return loans.get(loanId);
    }

    @Override
    public List<Loan> findAll() {
        return new ArrayList<>(loans.values());
    }
}
