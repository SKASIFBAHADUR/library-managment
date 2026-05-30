package main;

import entity.*;
import enums.*;
import repository.*;
import repository.impl.*;
import service.*;
import strategy.*;
import observer.*;
import factory.*;

import java.util.*;
import java.util.logging.*;

public class LibraryManagementApplication {
    private static final Logger LOGGER = Logger.getLogger(LibraryManagementApplication.class.getName());

    public static void main(String[] args) {
        setupLogger();

        BookRepository bookRepository = new BookRepositoryImpl();
        PatronRepository patronRepository = new PatronRepositoryImpl();
        LoanRepository loanRepository = new LoanRepositoryImpl();

        ReservationManager reservationManager = new ReservationManager();

        BookService bookService = new BookService(bookRepository);
        PatronService patronService = new PatronService(patronRepository);
        LoanService loanService = new LoanService(bookRepository, patronRepository, loanRepository, reservationManager);
        InventoryService inventoryService = new InventoryService(bookRepository);
        SearchService searchService = new SearchService(bookRepository);
        ReservationService reservationService = new ReservationService(reservationManager, bookService);
        RecommendationService recommendationService = new RecommendationService(bookRepository);
        BranchService branchService = new BranchService(bookService);

        prepopulateSampleData(bookService, patronService, loanService, branchService, reservationService);

        Scanner scanner = new Scanner(System.in);
        System.out.println("==================================================");
        System.out.println("   WELCOME TO THE LIBRARY MANAGEMENT SYSTEM   ");
        System.out.println("==================================================");

        boolean running = true;
        while (running) {
            printMenu();
            System.out.print("Enter your choice (1-11): ");
            String choiceStr = scanner.nextLine().trim();
            int choice = -1;
            try {
                choice = Integer.parseInt(choiceStr);
            } catch (NumberFormatException e) {
                // keep -1
            }

            switch (choice) {
                case 1:
                    handleAddBook(scanner, bookService, branchService);
                    break;
                case 2:
                    handleSearchBook(scanner, searchService);
                    break;
                case 3:
                    handleRegisterPatron(scanner, patronService);
                    break;
                case 4:
                    handleCheckoutBook(scanner, loanService);
                    break;
                case 5:
                    handleReturnBook(scanner, loanService);
                    break;
                case 6:
                    handleViewAvailableBooks(inventoryService);
                    break;
                case 7:
                    handleViewBorrowedBooks(inventoryService);
                    break;
                case 8:
                    handleReserveBook(scanner, patronService, reservationService);
                    break;
                case 9:
                    handleGetRecommendations(scanner, patronService, recommendationService);
                    break;
                case 10:
                    handleTransferBook(scanner, branchService);
                    break;
                case 11:
                    System.out.println("Exiting the application. Goodbye!");
                    running = false;
                    break;
                default:
                    System.out.println("[ERROR] Invalid choice. Please enter a number between 1 and 11.");
            }
            System.out.println();
        }
        scanner.close();
    }

    private static void setupLogger() {
        LogManager.getLogManager().reset();
        Logger rootLogger = Logger.getLogger("");
        ConsoleHandler handler = new ConsoleHandler();
        handler.setFormatter(new SimpleFormatter() {
            @Override
            public synchronized String format(LogRecord lr) {
                return String.format("[%s] %s%n", lr.getLevel(), lr.getMessage());
            }
        });
        rootLogger.addHandler(handler);
    }

    private static void prepopulateSampleData(BookService bookService, PatronService patronService,
                                             LoanService loanService, BranchService branchService,
                                             ReservationService reservationService) {
        System.out.println("[SYSTEM] Prepopulating sample library data...");

        LibraryBranch branch1 = branchService.createBranch("Central Library");
        LibraryBranch branch2 = branchService.createBranch("East End Library");

        Book book1 = BookFactory.createBook("978-0134685991", "Effective Java", "Joshua Bloch", 2018);
        Book book2 = BookFactory.createBook("978-0132350884", "Clean Code", "Robert C. Martin", 2008);
        Book book3 = BookFactory.createBook("978-0134494166", "Clean Architecture", "Robert C. Martin", 2017);
        Book book4 = BookFactory.createBook("978-0135974445", "Refactoring", "Martin Fowler", 2018);
        Book book5 = BookFactory.createBook("978-0596007126", "Head First Design Patterns", "Eric Freeman", 2004);

        bookService.addBook(book1);
        branch1.addBook(book1);

        bookService.addBook(book2);
        branch1.addBook(book2);

        bookService.addBook(book3);
        branch2.addBook(book3);

        bookService.addBook(book4);
        branch2.addBook(book4);

        bookService.addBook(book5);
        branch1.addBook(book5);

        Patron p1 = patronService.registerPatron("Alice Smith", "alice@example.com");
        Patron p2 = patronService.registerPatron("Bob Jones", "bob@example.com");
        Patron p3 = patronService.registerPatron("Charlie Brown", "charlie@example.com");

        loanService.checkoutBook(book2.getIsbn(), p1.getPatronId());
        loanService.checkoutBook(book1.getIsbn(), p2.getPatronId());

        reservationService.reserveBook(book2.getIsbn(), p3);

        System.out.println("[SYSTEM] Prepopulation complete!\n");
    }

    private static void printMenu() {
        System.out.println("--------------------------------------------------");
        System.out.println("1. Add Book");
        System.out.println("2. Search Book");
        System.out.println("3. Register Patron");
        System.out.println("4. Checkout Book");
        System.out.println("5. Return Book");
        System.out.println("6. View Available Books");
        System.out.println("7. View Borrowed Books");
        System.out.println("8. Reserve Book");
        System.out.println("9. Get Recommendations");
        System.out.println("10. Transfer Book Between Branches");
        System.out.println("11. Exit");
        System.out.println("--------------------------------------------------");
    }

    private static void handleAddBook(Scanner scanner, BookService bookService, BranchService branchService) {
        System.out.println("\n--- Add Book ---");
        System.out.print("Enter ISBN: ");
        String isbn = scanner.nextLine().trim();
        System.out.print("Enter Title: ");
        String title = scanner.nextLine().trim();
        System.out.print("Enter Author: ");
        String author = scanner.nextLine().trim();
        System.out.print("Enter Publication Year: ");
        String yearStr = scanner.nextLine().trim();
        int year;
        try {
            year = Integer.parseInt(yearStr);
        } catch (NumberFormatException e) {
            System.out.println("[ERROR] Invalid year format. Book addition aborted.");
            return;
        }

        List<LibraryBranch> branches = branchService.getAllBranches();
        if (branches.isEmpty()) {
            System.out.println("[ERROR] No branches exist. Please create a branch first.");
            return;
        }

        System.out.println("Select a Branch to add this book to:");
        for (int i = 0; i < branches.size(); i++) {
            System.out.printf("%d. %s (ID: %s)%n", i + 1, branches.get(i).getName(), branches.get(i).getBranchId());
        }
        System.out.print("Enter branch choice (1-" + branches.size() + "): ");
        String branchChoiceStr = scanner.nextLine().trim();
        int branchChoice;
        try {
            branchChoice = Integer.parseInt(branchChoiceStr);
            if (branchChoice < 1 || branchChoice > branches.size()) {
                System.out.println("[ERROR] Invalid choice. Book addition aborted.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("[ERROR] Invalid choice. Book addition aborted.");
            return;
        }

        LibraryBranch selectedBranch = branches.get(branchChoice - 1);
        Book newBook = BookFactory.createBook(isbn, title, author, year);
        bookService.addBook(newBook);
        selectedBranch.addBook(newBook);
        System.out.println("[SUCCESS] Book added to system and inventory of branch: " + selectedBranch.getName());
    }

    private static void handleSearchBook(Scanner scanner, SearchService searchService) {
        System.out.println("\n--- Search Book ---");
        System.out.println("Select Search Strategy:");
        System.out.println("1. Search by Title");
        System.out.println("2. Search by Author");
        System.out.println("3. Search by ISBN");
        System.out.print("Enter choice (1-3): ");
        String choiceStr = scanner.nextLine().trim();
        
        switch (choiceStr) {
            case "1":
                searchService.setSearchStrategy(new TitleSearchStrategy());
                break;
            case "2":
                searchService.setSearchStrategy(new AuthorSearchStrategy());
                break;
            case "3":
                searchService.setSearchStrategy(new IsbnSearchStrategy());
                break;
            default:
                System.out.println("[ERROR] Invalid choice. Search aborted.");
                return;
        }

        System.out.print("Enter search query: ");
        String query = scanner.nextLine().trim();
        List<Book> results = searchService.search(query);
        if (results.isEmpty()) {
            System.out.println("No books found matching the search criteria.");
        } else {
            System.out.println("Found " + results.size() + " book(s):");
            for (Book book : results) {
                System.out.println(" - " + book);
            }
        }
    }

    private static void handleRegisterPatron(Scanner scanner, PatronService patronService) {
        System.out.println("\n--- Register Patron ---");
        System.out.print("Enter Patron Name: ");
        String name = scanner.nextLine().trim();
        System.out.print("Enter Patron Email: ");
        String email = scanner.nextLine().trim();
        
        if (name.isEmpty() || email.isEmpty()) {
            System.out.println("[ERROR] Name and Email cannot be empty.");
            return;
        }
        Patron patron = patronService.registerPatron(name, email);
        System.out.println("[SUCCESS] Patron registered with ID: " + patron.getPatronId());
    }

    private static void handleCheckoutBook(Scanner scanner, LoanService loanService) {
        System.out.println("\n--- Checkout Book ---");
        System.out.print("Enter Book ISBN: ");
        String isbn = scanner.nextLine().trim();
        System.out.print("Enter Patron ID: ");
        String patronId = scanner.nextLine().trim();

        Loan loan = loanService.checkoutBook(isbn, patronId);
        if (loan != null) {
            System.out.println("[SUCCESS] Book checkout successful! Loan ID: " + loan.getLoanId());
        } else {
            System.out.println("[ERROR] Book checkout failed. Review the logs/reasons above.");
        }
    }

    private static void handleReturnBook(Scanner scanner, LoanService loanService) {
        System.out.println("\n--- Return Book ---");
        System.out.print("Enter Book ISBN to return: ");
        String isbn = scanner.nextLine().trim();

        boolean success = loanService.returnBook(isbn);
        if (success) {
            System.out.println("[SUCCESS] Book returned successfully.");
        } else {
            System.out.println("[ERROR] Book return failed. Verify the ISBN is correct and currently borrowed.");
        }
    }

    private static void handleViewAvailableBooks(InventoryService inventoryService) {
        System.out.println("\n--- Available Books ---");
        List<Book> available = inventoryService.getAvailableBooks();
        if (available.isEmpty()) {
            System.out.println("No available books in the system.");
        } else {
            for (Book book : available) {
                System.out.println(" - " + book);
            }
        }
    }

    private static void handleViewBorrowedBooks(InventoryService inventoryService) {
        System.out.println("\n--- Borrowed Books ---");
        List<Book> borrowed = inventoryService.getBorrowedBooks();
        if (borrowed.isEmpty()) {
            System.out.println("No borrowed books in the system.");
        } else {
            for (Book book : borrowed) {
                System.out.println(" - " + book);
            }
        }
    }

    private static void handleReserveBook(Scanner scanner, PatronService patronService, ReservationService reservationService) {
        System.out.println("\n--- Reserve Book ---");
        System.out.print("Enter Patron ID: ");
        String patronId = scanner.nextLine().trim();
        Patron patron = patronService.getPatronById(patronId);
        if (patron == null) {
            System.out.println("[ERROR] Patron not found.");
            return;
        }

        System.out.print("Enter Book ISBN to reserve: ");
        String isbn = scanner.nextLine().trim();

        boolean success = reservationService.reserveBook(isbn, patron);
        if (success) {
            System.out.println("[SUCCESS] Book reservation recorded successfully. Patron will be notified when it is returned.");
        } else {
            System.out.println("[ERROR] Reservation failed.");
        }
    }

    private static void handleGetRecommendations(Scanner scanner, PatronService patronService, RecommendationService recommendationService) {
        System.out.println("\n--- Get Book Recommendations ---");
        System.out.print("Enter Patron ID: ");
        String patronId = scanner.nextLine().trim();
        Patron patron = patronService.getPatronById(patronId);
        if (patron == null) {
            System.out.println("[ERROR] Patron not found.");
            return;
        }

        List<Book> recommendations = recommendationService.getRecommendations(patron);
        if (recommendations.isEmpty()) {
            System.out.println("No recommendations available. Please borrow some books first to build history, or no similar books found.");
        } else {
            System.out.println("Recommended Books for " + patron.getName() + ":");
            for (Book book : recommendations) {
                System.out.println(" - " + book);
            }
        }
    }

    private static void handleTransferBook(Scanner scanner, BranchService branchService) {
        System.out.println("\n--- Transfer Book Between Branches ---");
        System.out.print("Enter Book ISBN to transfer: ");
        String isbn = scanner.nextLine().trim();

        List<LibraryBranch> branches = branchService.getAllBranches();
        if (branches.size() < 2) {
            System.out.println("[ERROR] Transfer requires at least two library branches.");
            return;
        }

        System.out.println("Select SOURCE Branch:");
        for (int i = 0; i < branches.size(); i++) {
            System.out.printf("%d. %s (ID: %s)%n", i + 1, branches.get(i).getName(), branches.get(i).getBranchId());
        }
        System.out.print("Enter choice (1-" + branches.size() + "): ");
        String sourceChoiceStr = scanner.nextLine().trim();
        int sourceChoice;
        try {
            sourceChoice = Integer.parseInt(sourceChoiceStr);
            if (sourceChoice < 1 || sourceChoice > branches.size()) {
                System.out.println("[ERROR] Invalid choice.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("[ERROR] Invalid choice.");
            return;
        }
        LibraryBranch sourceBranch = branches.get(sourceChoice - 1);

        System.out.println("Select TARGET Branch:");
        for (int i = 0; i < branches.size(); i++) {
            System.out.printf("%d. %s (ID: %s)%n", i + 1, branches.get(i).getName(), branches.get(i).getBranchId());
        }
        System.out.print("Enter choice (1-" + branches.size() + "): ");
        String targetChoiceStr = scanner.nextLine().trim();
        int targetChoice;
        try {
            targetChoice = Integer.parseInt(targetChoiceStr);
            if (targetChoice < 1 || targetChoice > branches.size() || targetChoice == sourceChoice) {
                System.out.println("[ERROR] Invalid target choice (must be different from source).");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("[ERROR] Invalid choice.");
            return;
        }
        LibraryBranch targetBranch = branches.get(targetChoice - 1);

        boolean success = branchService.transferBook(isbn, sourceBranch.getBranchId(), targetBranch.getBranchId());
        if (success) {
            System.out.println("[SUCCESS] Book transfer successful.");
        } else {
            System.out.println("[ERROR] Book transfer failed. Make sure the book is in the source branch.");
        }
    }
}
