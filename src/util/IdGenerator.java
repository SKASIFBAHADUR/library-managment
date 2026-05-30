package util;

import java.util.concurrent.atomic.AtomicInteger;

public class IdGenerator {
    private static final AtomicInteger PATRON_COUNTER = new AtomicInteger(1000);
    private static final AtomicInteger LOAN_COUNTER = new AtomicInteger(5000);
    private static final AtomicInteger BRANCH_COUNTER = new AtomicInteger(10);

    public static String generatePatronId() {
        return "PATRON-" + PATRON_COUNTER.incrementAndGet();
    }

    public static String generateLoanId() {
        return "LOAN-" + LOAN_COUNTER.incrementAndGet();
    }

    public static String generateBranchId() {
        return "BRANCH-" + BRANCH_COUNTER.incrementAndGet();
    }
}
