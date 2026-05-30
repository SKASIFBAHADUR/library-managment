# Setup & Usage Guide - Library Management System

This document provides step-by-step instructions on setting up, compiling, running, and navigating the Library Management System.

---

## Prerequisites

* **Java Development Kit (JDK)**: Version 17 or higher.
* **Git**: Installed and configured (optional, for cloning).

Verify your installed Java version:
```bash
java -version
javac -version
```

---

## Clone Repository

Clone the project repository and navigate into the root directory:
```bash
git clone <repo-url>
cd library-management
```

---

## Compile Project

The project is built using only core Java. Do not use Maven, Gradle, or external build frameworks.

### Windows (PowerShell)
You can compile all packages into the `out/` directory using:
```powershell
# Create the build directory
New-Item -ItemType Directory -Path out -Force

# Write source files list and compile
Get-ChildItem -Path src -Filter *.java -Recurse | ForEach-Object { $_.FullName } | Out-File -FilePath sources.txt -Encoding ASCII
cmd /c "javac -d out @sources.txt"
Remove-Item sources.txt
```

### Windows (CMD)
```cmd
mkdir out
dir /s /b src\*.java > sources.txt
javac -d out @sources.txt
del sources.txt
```

### Linux / macOS (Terminal)
```bash
mkdir -p out
find src -name "*.java" > sources.txt
javac -d out @sources.txt
rm sources.txt
```

---

## Run Project

Once compiled, run the console application by pointing the classpath to the `out/` directory:

```bash
java -cp out main.LibraryManagementApplication
```

---

## Project Structure

The project directory layout:
```text
library-management/
├── src/                      # Source code
│   ├── entity/               # Domain model entity POJOs
│   ├── enums/                # BookStatus state enum
│   ├── factory/              # BookFactory for object creation
│   ├── main/                 # Main entry point CLI class
│   ├── observer/             # Observer pattern interface & manager
│   ├── repository/           # Storage layer interfaces
│   │   └── impl/             # In-memory HashMap implementations
│   ├── service/              # Core business services logic
│   ├── strategy/             # Swappable book searching algorithms
│   └── util/                 # Unique ID generator utility
├── out/                      # Compiled class files
├── docs/                     # Design & Setup documents
├── images/                   # UML and Flow diagrams
└── README.md                 # Project README file
```

---

## Common Issues

* **Compilation splatting issues in PowerShell**:
  Running `javac -d out @sources.txt` can fail in PowerShell if it interprets `@` as a splatting variable. Bypass this by using the `cmd /c` prefix or running compilation under standard Command Prompt (CMD).
* **Incorrect JDK version**:
  Ensure your `JAVA_HOME` environment variable points to a JDK version $\ge 17$. The code uses Java 17 features (such as switch expressions and formatted printing) which will fail to compile on older versions.
* **Console logging format verbose**:
  The system logger utilizes standard standard error handlers by default. The application automatically resets default formatters at startup to present clean logged outputs. If logs appear duplicate, ensure no custom system logging configurations override the initialization steps.

---

## CLI Usage Guide

When launched, the application initializes and pre-populates dummy data (2 branches, 5 books, 3 patrons, active loans, and active reservations). It then presents an interactive console menu:

### 1. Add Book
1. Select Option `1`.
2. Input the book metadata (ISBN, Title, Author, Year).
3. The console lists all available physical branches. Choose the branch number to register the book inside its local inventory list.

### 2. Search Book
1. Select Option `2`.
2. Select your search strategy: `1` for Title, `2` for Author, `3` for ISBN.
3. Enter the search query. The system matches and displays all matching books.

### 3. Register Patron
1. Select Option `3`.
2. Enter the patron's name and email address.
3. The system registers the patron and displays the generated Patron ID (e.g. `PATRON-1004`).

### 4. Checkout Book
1. Select Option `4`.
2. Enter the ISBN of the book to checkout.
3. Enter the Patron ID.
4. The system validates availability, marks the status as `BORROWED`, and registers the loan transaction.

### 5. Return Book
1. Select Option `5`.
2. Enter the ISBN of the returned book.
3. The system locates the active loan, marks it returned, sets the book status to `AVAILABLE`, and notifies any waiting reserved observers.

### 6. View Available Books
1. Select Option `6`.
2. Lists all books in the library system whose current status is `AVAILABLE`.

### 7. View Borrowed Books
1. Select Option `7`.
2. Lists all books in the library system whose current status is `BORROWED`.

### 8. Reserve Book
1. Select Option `8`.
2. Enter the Patron ID and the ISBN of the book to reserve.
3. The patron joins the reservation queue for the book.

### 9. Get Recommendations
1. Select Option `9`.
2. Enter the Patron ID.
3. The system parses keywords and authors from the patron's borrowing history, compares them to other unread books, and lists recommended books ranked by score.

### 10. Transfer Book Between Branches
1. Select Option `10`.
2. Enter the book ISBN.
3. The console lists all branches. Select the source branch and target branch.
4. The system moves the book from the source branch to the target branch inventory.
