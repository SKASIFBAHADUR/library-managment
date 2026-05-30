package service;

import entity.Book;
import entity.LibraryBranch;
import util.IdGenerator;
import java.util.*;
import java.util.logging.Logger;

public class BranchService {
    private static final Logger LOGGER = Logger.getLogger(BranchService.class.getName());
    private final Map<String, LibraryBranch> branches = new HashMap<>();
    private final BookService bookService;

    public BranchService(BookService bookService) {
        this.bookService = bookService;
    }

    public LibraryBranch createBranch(String name) {
        String branchId = IdGenerator.generateBranchId();
        LibraryBranch branch = new LibraryBranch(branchId, name);
        branches.put(branchId, branch);
        LOGGER.info("Library Branch Created: " + branch.getBranchId() + " - " + branch.getName());
        return branch;
    }

    public void addBranch(LibraryBranch branch) {
        if (branch != null && branch.getBranchId() != null) {
            branches.put(branch.getBranchId(), branch);
            LOGGER.info("Library Branch Added: " + branch.getBranchId() + " - " + branch.getName());
        }
    }

    public LibraryBranch getBranch(String branchId) {
        return branches.get(branchId);
    }

    public List<LibraryBranch> getAllBranches() {
        return new ArrayList<>(branches.values());
    }

    public boolean transferBook(String isbn, String sourceBranchId, String targetBranchId) {
        LibraryBranch source = branches.get(sourceBranchId);
        LibraryBranch target = branches.get(targetBranchId);

        if (source == null) {
            LOGGER.warning("Transfer failed: Source branch with ID " + sourceBranchId + " not found.");
            return false;
        }
        if (target == null) {
            LOGGER.warning("Transfer failed: Target branch with ID " + targetBranchId + " not found.");
            return false;
        }

        Book bookToTransfer = null;
        for (Book book : source.getBooks()) {
            if (book.getIsbn().equals(isbn)) {
                bookToTransfer = book;
                break;
            }
        }

        if (bookToTransfer == null) {
            LOGGER.warning("Transfer failed: Book with ISBN " + isbn + " not found in Source Branch: " + source.getName());
            return false;
        }

        source.removeBook(bookToTransfer);
        target.addBook(bookToTransfer);
        
        bookService.addBook(bookToTransfer);

        LOGGER.info("Transfer: Book '" + bookToTransfer.getTitle() + "' (ISBN: " + isbn + ") transferred from " + source.getName() + " to " + target.getName());
        return true;
    }
}
