# Software Design Document - Library Management System

This document outlines the software design, architectural decisions, domain model, design patterns, and SOLID principles of the Library Management System.

---

## 1. Project Overview

### Purpose
The Library Management System is a clean console-based application designed to manage books, library branches, patrons, lending actions, reservations, and recommendation generation. It serves as a production-inspired example of object-oriented design and SOLID principles in core Java.

### Goals
* Implement core library business requirements with high decoupling and clear separation of concerns.
* Showcase Design Patterns (Strategy, Observer, Factory) in practice.
* Provide an in-memory repository architecture using standard Java Collections.
* Compile and run with zero external frameworks or dependencies (Maven, Gradle, Spring Boot, databases).

### Scope
Includes Book Management, Patron registration and profiles, branch-specific inventories, lending validation (checkout/return), observers for reserving unavailable books, and metadata-matching recommendation generation.

---

## 2. Functional Requirements

All functional requirements are backed by concrete implementations in the source code:

* **Book Management**:
  * Add books to a physical branch: [BookService.java](file:///e:/libary-managment/src/service/BookService.java#L16).
  * Remove books: [BookService.java](file:///e:/libary-managment/src/service/BookService.java#L21).
  * Update book details: [BookService.java](file:///e:/libary-managment/src/service/BookService.java#L31).
  * Search books by Title, Author, or ISBN via strategies: [SearchService.java](file:///e:/libary-managment/src/service/SearchService.java#L20).
* **Patron Management**:
  * Register new patrons with email: [PatronService.java](file:///e:/libary-managment/src/service/PatronService.java#L17).
  * Update patron profile data: [PatronService.java](file:///e:/libary-managment/src/service/PatronService.java#L25).
  * Retrieve borrowing records history: [PatronService.java](file:///e:/libary-managment/src/service/PatronService.java#L44).
* **Lending**:
  * Checkout book copies verifying availability status: [LoanService.java](file:///e:/libary-managment/src/service/LoanService.java#L31).
  * Return borrowed books and update status: [LoanService.java](file:///e:/libary-managment/src/service/LoanService.java#L62).
* **Inventory Tracking**:
  * List all books currently available or currently checked out: [InventoryService.java](file:///e:/libary-managment/src/service/InventoryService.java).
* **Reservation System**:
  * Allow patrons to register interest on a book: [ReservationService.java](file:///e:/libary-managment/src/service/ReservationService.java#L19).
  * Notify patrons immediately when a book is returned: [ReservationManager.java](file:///e:/libary-managment/src/observer/ReservationManager.java#L28).
* **Recommendation Engine**:
  * Match history metadata to recommend unread titles: [RecommendationService.java](file:///e:/libary-managment/src/service/RecommendationService.java#L16).
* **Branch Management**:
  * Support physical branches and book transfers: [BranchService.java](file:///e:/libary-managment/src/service/BranchService.java).

---

## 3. Architecture Overview

The system uses a strict **Layered Architecture** representing separate tiers:

```
+-------------------------------------------------------------+
|                         main (UI)                           |
|       - LibraryManagementApplication (Console CLI)          |
+------------------------------+------------------------------+
                               | (Calls)
                               v
+-------------------------------------------------------------+
|                        service                              |
|   - Coordinates operations & implements business logic      |
+------------------------------+------------------------------+
                               | (Retrieves/Persists)
                               v
+-------------------------------------------------------------+
|                      repository                             |
|  - Manages raw collection data via abstract interfaces      |
+------------------------------+------------------------------+
                               | (Updates state)
                               v
+-------------------------------------------------------------+
|                        entity                               |
|        - Plain Old Java Objects (POJOs / Domain model)      |
+-------------------------------------------------------------+
```

### Dependency Flow
Dependencies flow **downward** from CLI user interface -> Services -> Repositories -> Entities. High-level service modules interact with Repository interfaces, maintaining decoupling.

---

## 4. Package Structure

Every package is isolated by responsibility:

* **[entity](file:///e:/libary-managment/src/entity)**:
  * *Responsibility*: Domain data models.
  * *Key Classes*: `Book`, `Patron`, `Loan`, `LibraryBranch`.
  * *Collaborators*: `enums.BookStatus`.
* **[enums](file:///e:/libary-managment/src/enums)**:
  * *Responsibility*: System-wide enums.
  * *Key Classes*: `BookStatus`.
* **[repository](file:///e:/libary-managment/src/repository)**:
  * *Responsibility*: Data access layer contracts.
  * *Key Classes*: `BookRepository`, `PatronRepository`, `LoanRepository`.
* **[repository.impl](file:///e:/libary-managment/src/repository/impl)**:
  * *Responsibility*: In-memory data structures.
  * *Key Classes*: `BookRepositoryImpl`, `PatronRepositoryImpl`, `LoanRepositoryImpl`.
  * *Collaborators*: `entity.*`, `repository.*`.
* **[service](file:///e:/libary-managment/src/service)**:
  * *Responsibility*: Core business operations & transactions.
  * *Key Classes*: `BookService`, `LoanService`, `BranchService`, `InventoryService`, `SearchService`, `ReservationService`, `RecommendationService`.
  * *Collaborators*: `repository.*`, `observer.ReservationManager`.
* **[strategy](file:///e:/libary-managment/src/strategy)**:
  * *Responsibility*: Encapsulating search algorithms.
  * *Key Classes*: `SearchStrategy`, `TitleSearchStrategy`, `AuthorSearchStrategy`, `IsbnSearchStrategy`.
* **[observer](file:///e:/libary-managment/src/observer)**:
  * *Responsibility*: Event notifications for book returns.
  * *Key Classes*: `Observer`, `Subject`, `ReservationManager`.
* **[factory](file:///e:/libary-managment/src/factory)**:
  * *Responsibility*: Instantiating objects.
  * *Key Classes*: `BookFactory`.
* **[util](file:///e:/libary-managment/src/util)**:
  * *Responsibility*: General utilities.
  * *Key Classes*: `IdGenerator`.
* **[main](file:///e:/libary-managment/src/main)**:
  * *Responsibility*: Interactive console UI and system setup.
  * *Key Classes*: `LibraryManagementApplication`.

---

## 5. Domain Model

### Book
* **File**: [Book.java](file:///e:/libary-managment/src/entity/Book.java)
* **Fields**: `String isbn`, `String title`, `String author`, `int publicationYear`, `BookStatus status`, `String branchId`.
* **Responsibilities**: Holds book metadata, track availability state, and stores its physical branch assignment.
* **Relationships**: Relates to `BookStatus` (1:1), and has a reference association to a `LibraryBranch` ID.

### Patron
* **File**: [Patron.java](file:///e:/libary-managment/src/entity/Patron.java)
* **Fields**: `String patronId`, `String name`, `String email`, `List<Loan> borrowingHistory`.
* **Responsibilities**: Stores patron contact details, registers as a reservation `Observer`, and maintains borrowing records.
* **Relationships**: Implements `observer.Observer`. Holds a 1:N aggregation of `Loan` instances.

### Loan
* **File**: [Loan.java](file:///e:/libary-managment/src/entity/Loan.java)
* **Fields**: `String loanId`, `Book book`, `Patron patron`, `LocalDate checkoutDate`, `LocalDate returnDate`, `boolean isReturned`.
* **Responsibilities**: Records lending transactions and associates checkouts with return operations.
* **Relationships**: Associates `Book` (1:1) and `Patron` (1:1).

### LibraryBranch
* **File**: [LibraryBranch.java](file:///e:/libary-managment/src/entity/LibraryBranch.java)
* **Fields**: `String branchId`, `String name`, `List<Book> books`.
* **Responsibilities**: Tracks inventory locally at the branch level and handles additions/removals.
* **Relationships**: Aggregates a 1:N list of `Book` objects.

---

## 6. Repository Layer

The repository layer abstracts storage details from the service layer:

* **[BookRepository](file:///e:/libary-managment/src/repository/BookRepository.java)** / **[BookRepositoryImpl](file:///e:/libary-managment/src/repository/impl/BookRepositoryImpl.java)**: Stores all books by ISBN.
* **[PatronRepository](file:///e:/libary-managment/src/repository/PatronRepository.java)** / **[PatronRepositoryImpl](file:///e:/libary-managment/src/repository/impl/PatronRepositoryImpl.java)**: Stores patrons by Patron ID.
* **[LoanRepository](file:///e:/libary-managment/src/repository/LoanRepository.java)** / **[LoanRepositoryImpl](file:///e:/libary-managment/src/repository/impl/LoanRepositoryImpl.java)**: Stores loans by Loan ID.

### Rationale
Using Repository interfaces ensures the codebase complies with LSP and DIP. Service business code is not tied to concrete data structures (like maps or lists); if a real database is added in the future, we only need to provide new repository implementations, keeping the service logic unchanged.

---

## 7. Service Layer

Services encapsulate business flows and manage interactions:

* **[BookService](file:///e:/libary-managment/src/service/BookService.java)**: Handles book additions, removals, and detail updates. Collaborates with `BookRepository`.
* **[PatronService](file:///e:/libary-managment/src/service/PatronService.java)**: Registers patrons (using `IdGenerator`) and manages borrowing histories. Collaborates with `PatronRepository`.
* **[LoanService](file:///e:/libary-managment/src/service/LoanService.java)**: Validates checkouts (checks availability) and processes returns. Collaborates with `BookRepository`, `PatronRepository`, `LoanRepository`, and `ReservationManager`.
* **[InventoryService](file:///e:/libary-managment/src/service/InventoryService.java)**: Tracks availability status and queries available vs borrowed books. Collaborates with `BookRepository`.
* **[SearchService](file:///e:/libary-managment/src/service/SearchService.java)**: swappable strategy selector for title, author, and ISBN lookups. Collaborates with `BookRepository` and `SearchStrategy`.
* **[ReservationService](file:///e:/libary-managment/src/service/ReservationService.java)**: Registers interest reservations for patrons on a book. Collaborates with `BookService` and `ReservationManager`.
* **[RecommendationService](file:///e:/libary-managment/src/service/RecommendationService.java)**: Calculates scoring metrics on unread books using author and keyword frequency. Collaborates with `BookRepository`.
* **[BranchService](file:///e:/libary-managment/src/service/BranchService.java)**: Creates physical branches and manages transferring book copies. Collaborates with `BookService`.

---

## 8. Design Patterns

### Strategy Pattern
Used for searching books dynamically:
* **Classes**: `SearchStrategy` (interface), `TitleSearchStrategy`, `AuthorSearchStrategy`, `IsbnSearchStrategy`, and `SearchService` (context).
* **Flow**: The client sets the search strategy via `SearchService.setSearchStrategy(...)` and calls `search(query)`. The service delegates search execution to the active strategy.
* **Advantages**: Adding search metrics (such as search by genre or year) requires only creating a new strategy class, which keeps the existing search service code clean and unmodified (OCP).

### Observer Pattern
Used for the reservation notification flow:
* **Classes**: `Observer` (interface implemented by `Patron`), `Subject` (interface), and `ReservationManager` (concrete implementation).
* **Flow**: When a patron reserves a borrowed book, the system calls `registerObserver(isbn, patron)`. Upon book return in `LoanService`, the manager triggers `notifyObservers(book)`. This iterates through the waiting queue and invokes `patron.update(book)`.
* **Advantages**: Loose coupling. The reservation manager notifies waiting observers without needing to know concrete details about their implementation.

### Factory Pattern
Used for book instantiation:
* **Classes**: `BookFactory`.
* **Flow**: Centralizes object creation. Instead of calling constructors directly, components invoke `BookFactory.createBook(...)`.
* **Advantages**: Ensures books are instantiated with correct default states (e.g. status initialized to `BookStatus.AVAILABLE`).

---

## 9. SOLID Principles Audit

### SRP (Single Responsibility Principle)
* **Evidence**: Repositories only persist/retrieve data. Services only compute transaction steps. Factories only construct objects.
* **Strengths**: High modularity. Debugging data retrieval issues points directly to repository files, while validation errors map to service classes.
* **Weaknesses**: In `BookService.addBook`, the link to a library branch is omitted. Orchestration leaks into `LibraryManagementApplication`, which has to manually register books in the selected branch.

### OCP (Open-Closed Principle)
* **Evidence**: SWappable search strategies in `SearchService`.
* **Strengths**: Easily extensible. Adding new search strategies does not modify the context or client code.
* **Weaknesses**: Adding new notification paths (e.g., SMS alerts) would require modifying the `Patron` implementation directly instead of injecting separate notification handlers.

### LSP (Liskov Substitution Principle)
* **Evidence**: High-level modules interact with `BookRepository` and `SearchStrategy` interfaces.
* **Strengths**: Safe implementations swapping. Concrete repository implementations can be replaced with a database implementation cleanly without breaking execution.
* **Weaknesses**: None. The class interfaces contain logical, complete abstractions.

### ISP (Interface Segregation Principle)
* **Evidence**: Focused interfaces such as `Observer` (one method) and `Subject` (three methods).
* **Strengths**: Clients do not depend on methods they do not use, preventing bloated interfaces.

### DIP (Dependency Inversion Principle)
* **Evidence**: Services receive repository dependencies via constructors using repository interfaces, decoupling them from concrete map storage classes.
* **Strengths**: Decoupled design. High-level service logic is decoupled from lower-level in-memory storage implementations.

---

## 10. Data Structures Used

* **`HashMap`**: Used for O(1) key-value lookups (e.g., mapping ISBNs to books or IDs to patrons). This ensures fast indexing as the library scale grows.
* **`ArrayList`**: Used for sequential list iterations (e.g., book inventories, search matches, and patron borrowing histories), offering fast appends and index-based traversals.
* **`HashSet`**: Used in the recommendation engine to store stop words and keywords, offering fast lookup times when filtering out duplicates and calculating scores.
* **`Queue` (via `LinkedList`)**: Used in `ReservationManager` to store reservations. This implements a fair First-In, First-Out (FIFO) queue for patrons waiting for returned books.

---

## 11. Application Flows

### Add Book Flow
1. CLI prompts the user to enter metadata (ISBN, Title, Author, Year, and Branch choice).
2. UI calls `BookFactory.createBook(...)` returning a new Book instance.
3. UI calls `BookService.addBook(book)` to save it to the general repository index.
4. UI calls `branch.addBook(book)` to add it to the branch's list and set the book's `branchId`.

### Search Book Flow
1. CLI prompts the user to select the search strategy (Title, Author, or ISBN).
2. UI instantiates the strategy (e.g. `TitleSearchStrategy`) and calls `SearchService.setSearchStrategy(...)`.
3. UI prompts the user to enter the search query.
4. UI calls `SearchService.search(query)`, which delegates search execution to the active strategy and returns matching books.

### Checkout Book Flow
1. CLI prompts the user to enter the book ISBN and patron ID.
2. UI calls `LoanService.checkoutBook(isbn, patronId)`.
3. `LoanService` fetches the book. It verifies the book is not null and its status is `AVAILABLE`.
4. `LoanService` fetches the patron. It verifies the patron exists.
5. It creates a `Loan` object, sets the book status to `BORROWED`, and adds the loan to the patron's borrowing history.
6. It saves the updated book, patron, and loan objects in repositories.

### Return Book Flow
1. CLI prompts the user to enter the book ISBN.
2. UI calls `LoanService.returnBook(isbn)`.
3. `LoanService` locates the active loan for the book (where `isReturned` is false).
4. It sets the loan's return status to true and updates its return date.
5. It sets the book status to `AVAILABLE` and updates repositories.
6. If the book has reservations, it calls `reservationManager.notifyObservers(book)` to notify waiting patrons.

### Reserve Book Flow
1. CLI prompts the user to enter the patron ID and book ISBN.
2. UI calls `ReservationService.reserveBook(isbn, patron)`.
3. `ReservationService` calls `reservationManager.registerObserver(isbn, patron)` to add the patron to the book's reservation queue.
4. If the book is currently `AVAILABLE`, it changes the status to `RESERVED` and updates repositories.

### Transfer Book Flow
1. CLI prompts the user to enter the book ISBN, source branch ID, and target branch ID.
2. UI calls `BranchService.transferBook(isbn, sourceBranchId, targetBranchId)`.
3. `BranchService` fetches the source and target branches.
4. It removes the book from the source branch's list and adds it to the target branch's list.
5. It calls `bookService.addBook(book)` to update the book's branch ID in the repository.

### Recommendation Generation Flow
1. CLI prompts the user to enter the patron ID.
2. UI calls `RecommendationService.getRecommendations(patron)`.
3. `RecommendationService` extracts keywords from titles and authors of the patron's borrowed books.
4. It iterates through all books, skipping already borrowed ones.
5. It calculates a matching score for each book (Author match = +5, Title keyword match = +2).
6. It sorts recommendations in descending order of score and returns them.

---

## 12. Known Limitations

* **Duplicate ISBN Handling**: Adding a book with an existing ISBN silently overwrites the previous entry, which could cause inconsistent state if the book copy is assigned to another branch or currently checked out.
* **Reservation Race Condition**: Returning a book changes its status to `AVAILABLE` and alerts patrons. However, because the book is unlocked, anyone can check out the book before the notified patron can act.
* **Reserved Book Checkout Lock**: Setting a book's status to `RESERVED` makes it un-borrowable since `checkoutBook` rejects checkout for any book whose status is not `AVAILABLE`. There is no exception logic allowing the reserving patron to checkout the reserved book.
* **Lack of Database Persistence**: All changes are stored in-memory. Restarting the application wipes the data.
* **Thread Safety**: The repository collections (`HashMap` and `ArrayList`) are not thread-safe. Concurrent access can cause race conditions or data corruption.

---

## 13. Future Enhancements

* **Thread-Safe Collections**: Replace `HashMap` and `ArrayList` inside repositories with `ConcurrentHashMap` and thread-safe list wrappers (`Collections.synchronizedList`).
* **Persistent Storage**: Implement a database layer (e.g. SQLite or PostgreSQL) by providing new repository implementations, keeping service layer business logic decoupled.
* **Strict Reservation Holds**: Track which patron has reserved a book and restrict checkouts to the reserving patron for a set hold period.
* **Advanced Error Handling**: Introduce custom exceptions (e.g., `BookUnavailableException`, `PatronNotFoundException`) instead of returning nulls or boolean flags.
