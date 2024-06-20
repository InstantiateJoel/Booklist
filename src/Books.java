import java.sql.*;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 Coding Ideas:
 Make a method, that gets all the user input for the books (name, author...) so there´s basically only the methods left,
 that make the sql parts.
 */

/*
todo:
1. Add filtering for books
2. when printing books, and no books are existent, print no books yet or some
3. Status can only be one of the numbers
4. (maybe add 'options' to delete book, to go back
5. add exit, to exit and log out of the book list

 */

public class Books {
    // Scanner initialization
    public static final Scanner userInput = new Scanner(System.in);

    // logger initialization
    private static final Logger booklistLogger = Logger.getLogger(Books.class.getName());

    private static String bookName = "";
    private static String authorName;
    private static String bookGenre;
    private static String bookStatus; // used to get the input and safe it for the DB
    private static String selectedBookName;
    private static String selectedAuthorName;
    private static String filterGenre;
    private static final String[][] bookStatusOptions = {{"1", "ordered"}, {"2", "shelf"}, {"3", "read"}, {"4", "lend"}, {"5", "wishlist"}};
    private static final String[][] filterOptions = {{"1", "Book name"}, {"2", "Author Name"}, {"3", "Genre"}};
    public static int selectedId;

    // SQL Queries
    public static final String INSERT_INTO_BOOKS_QUERY = "INSERT INTO BOOKS (USER_ID, NAME, AUTHOR, GENRE, STATUS) VALUES (?, ?, ?, ?, ?)";
    public static final String DELETE_BOOK_QUERY = "DELETE FROM BOOKS WHERE USER_ID = ? AND NAME = ? AND AUTHOR = ?";
    public static final String CHECK_BOOK_EXISTS_QUERY = "SELECT * FROM BOOKS WHERE NAME = ? AND AUTHOR = ? AND USER_ID = ?";
    public static final String SELECT_ALL_BOOKS_QUERY = "SELECT * FROM BOOKS WHERE USER_ID = ?";
    public static final String DISABLE_SAFE_UPDATES_QUERY = "SET SESSION sql_safe_updates = 0";
    public static final String ENABLE_SAFE_UPDATES_QUERY = "SET SESSION sql_safe_updates = 1";
    public static final String CHANGE_BOOK_STATUS_QUERY = "UPDATE books SET STATUS = ? WHERE NAME = ? AND AUTHOR = ? AND USER_ID = ?";
    public static final String FILTER_BOOK_GENRE_QUERY = "SELECT * FROM BOOKS WHERE USER_ID = ? AND GENRE = ?";
    public static final String FILTER_BOOK_AUTHOR_QUERY = "SELECT * FROM BOOKS WHERE USER_ID = ? AND AUTHOR = ?";
    public static final String FILTER_BOOK_NAME_QUERY = "SELECT * FROM BOOKS WHERE USER_ID = ? AND NAME = ?";

    /*
     Method below is used to read the user input, check which one and then go to the method, that executes it
     */
    public static void printAllBooks(Connection booklistConnection, int userId) {
        String selectedStatus;
        String selectedGenre;
        try {
            // preparing sql statements
            PreparedStatement selectAllBooksStatement = booklistConnection.prepareStatement(SELECT_ALL_BOOKS_QUERY);
            selectAllBooksStatement.setInt(1, userId);
            ResultSet selectAllBooksResult = selectAllBooksStatement.executeQuery();

            System.out.println("===========BOOKLIST===========");
            System.out.println("These are your books:");
            while (selectAllBooksResult.next()) {
                selectedId = selectAllBooksResult.getInt("USER_ID");
                selectedBookName = selectAllBooksResult.getString("NAME");
                selectedAuthorName = selectAllBooksResult.getString("AUTHOR");
                selectedGenre = selectAllBooksResult.getString("GENRE");
                selectedStatus = selectAllBooksResult.getString("STATUS");

                // print all the books
                System.out.println("------------------------------");
                System.out.println("Title:  " + selectedBookName);
                System.out.println("Author: " + selectedAuthorName);
                System.out.println("Genre:  " + selectedGenre);
                System.out.println("Status: " + selectedStatus);
            }
            System.out.println("------------------------------");
            userOptions(booklistConnection, userId);
        } catch (SQLException e) {
            booklistLogger.log(Level.SEVERE, "SQL Exception occurred, while fetching books", e);
        }
    }

    public static void userOptions(Connection booklistConnection, int userId) {
        System.out.print("""
                Please choose from the following options (use the numbers):
                 \
                1: Print all books
                 \
                2: Add a book
                 \
                3: Filter books
                 \
                4: Change status of a book
                 \
                5: Delete book
                 \
                Enter number>""");
        // used to check which case from switch should be used
        String userChoice;
        while (true) {
            userChoice = userInput.nextLine();
            if (userChoice.isEmpty()) {
                System.out.println("You need to enter one number from above, or type 'help' to get the possibilities again.");
            } else {
                break;
            }
        }
        switch (userChoice) {
            case "1": // print all books
                printAllBooks(booklistConnection, userId);
                break;
            case "2": // add a book
                addBook(booklistConnection, userId);
                break;
            case "3": // Filtering for specific book(s)
                filterBooks(booklistConnection, userId);
                break;
            case "4": // change status of a book
                changeStatus(booklistConnection, userId);
                break;
            case "5": // delete a book
                deleteBook(booklistConnection, userId);
                break;
            default:
                System.out.println("Unexpected error!");
        }
    }

    // insert book, asks about the book
    public static void addBook(Connection booklistConnection, int userId) {
        while (true) {
            System.out.print("Enter the name of the book> ");
            bookName = userInput.nextLine();
            if (bookName.isEmpty()) {
                System.out.println("The name of the book is required!");
            } else if (bookName.equals("options")) {
                userOptions(booklistConnection, userId);
                break;
            }
            System.out.print("Enter the name of the author> ");
            authorName = userInput.nextLine();
            if (authorName.isEmpty()) {
                System.out.println("The name of the author is required!");
                continue;
            } else if (authorName.equals("options")) {
                userOptions(booklistConnection, userId);
                break;
            }
            System.out.print("Enter the genre of the book. If you don´t want to add it, please proceed with 'enter'> ");
            bookGenre = userInput.nextLine();
            if (bookGenre.isEmpty()) {
                bookGenre = "Not defined";
            } else if (bookGenre.equals("options")) {
                userOptions(booklistConnection, userId);
                break;
            }

            System.out.print("Enter the name of the status: ");
            bookStatus = userInput.nextLine();
            if (bookStatus.isEmpty()) {
                System.out.println("The status of the book is required!");
            } else if (bookStatus.equals("options")) {
                userOptions(booklistConnection, userId);
                break;
            } else {
                boolean bookStatusFound = false;
                for (String[] options : bookStatusOptions) {
                    if (options[0].equals(bookStatus)) {
                        bookStatus = options[1];
                        bookStatusFound = true;
                        break;
                    }
                }
            }
            if (!checkBookExist(booklistConnection, userId)) {
                insertIntoBooks(booklistConnection, userId);
                break;
            } else {
                System.out.println("This book already exists. Please try again. If you want to use another option type 'options'.");
            }
        }
    }

    // checks, if book already exists
    public static boolean checkBookExist(Connection booklistConnection, int userId) {
        PreparedStatement checkBookExistStatement = null;
        try {
            checkBookExistStatement = booklistConnection.prepareStatement(CHECK_BOOK_EXISTS_QUERY);
            checkBookExistStatement.setString(1, bookName);
            checkBookExistStatement.setString(2, authorName);
            checkBookExistStatement.setInt(3, userId);

            ResultSet checkBookExistResult = checkBookExistStatement.executeQuery();
            if (checkBookExistResult.next()) {
                selectedId = checkBookExistResult.getInt("USER_ID");
                selectedBookName = checkBookExistResult.getString("NAME");
                selectedAuthorName = checkBookExistResult.getString("AUTHOR");

                if (selectedBookName.equalsIgnoreCase(bookName) && selectedAuthorName.equalsIgnoreCase(authorName)) {
                    return true;
                }
            }
        } catch (SQLException e) {
            booklistLogger.log(Level.SEVERE, "SQL Exception occurred, while checking if book exists", e);
        }
        return false;
    }

    // this method inserts a new book in the DB
    public static void insertIntoBooks(Connection booklistConnection, int userId) {
        try {
            PreparedStatement insertBooksStatement = booklistConnection.prepareStatement(INSERT_INTO_BOOKS_QUERY);
            insertBooksStatement.setInt(1, userId);
            insertBooksStatement.setString(2, bookName);
            insertBooksStatement.setString(3, authorName);
            insertBooksStatement.setString(4, bookGenre);
            insertBooksStatement.setString(5, bookStatus);
            int rowsAffected = insertBooksStatement.executeUpdate();

            // when the rows are not created, the user is prompted to try again
            if (rowsAffected > 0) {
                System.out.println("Book successfully added!");
                userOptions(booklistConnection, userId);
            } else {
                System.out.println("Something went wrong! Please try again or contact the admin!");
            }
        } catch (SQLException e) {
            booklistLogger.log(Level.SEVERE, "SQL Exception occurred, while inserting book.", e);
        }
    }

    // changes the status of a book
    public static void changeStatus(Connection booklistConnection, int userId) {
        try {
            while (true) {
                System.out.println("Please enter the following information about the book you want to change the status:");
                System.out.print("Name> ");
                bookName = userInput.nextLine();
                System.out.print("Author> ");
                authorName = userInput.nextLine();
                System.out.print("Status> ");
                bookStatus = userInput.nextLine();

                if (bookName.isEmpty() || authorName.isEmpty() || bookStatus.isEmpty()) {
                    System.out.println("You need to enter the name of the book and the author name!");
                } else {
                    boolean statusFound = false;
                    for (String[] options : bookStatusOptions) {
                        if (options[0].equals(bookStatus)) {
                            bookStatus = options[1];
                            statusFound = true;
                            break;
                        }
                    }
                    if (statusFound) {
                        PreparedStatement disableSafeUpdateStatement = booklistConnection.prepareStatement(DISABLE_SAFE_UPDATES_QUERY);
                        disableSafeUpdateStatement.executeUpdate();
                        PreparedStatement changeBookStatusStatement = booklistConnection.prepareStatement(CHANGE_BOOK_STATUS_QUERY);
                        changeBookStatusStatement.setString(1, bookStatus);
                        changeBookStatusStatement.setString(2, bookName);
                        changeBookStatusStatement.setString(3, authorName);
                        changeBookStatusStatement.setInt(4, userId);

                        int rowsAffected = changeBookStatusStatement.executeUpdate();
                        if (rowsAffected > 0) {
                            System.out.println("Status of the book " + bookName + " changed to " + bookStatus + ".");
                            PreparedStatement enableSafeUpdateStatement = booklistConnection.prepareStatement(ENABLE_SAFE_UPDATES_QUERY);
                            enableSafeUpdateStatement.executeUpdate();
                            userOptions(booklistConnection, userId);
                            break;
                        } else {
                            System.out.println("Error occurred. Please try again.");
                        }
                    }
                }
            }
        } catch (SQLException e) {
            booklistLogger.log(Level.SEVERE, "SQL Exception occurred, while changing the status of a book.");
        }
    }

    public static void filterBooks(Connection booklistConnection, int userId) {
        try {
            while (true) {
                System.out.print("Which filter do you want to use? (1, Book name; 2, Author; 3, Genre> "); // todo change that into the input method
                String filterOptionUser = userInput.nextLine(); // stores the filter option in a var
                if (filterOptionUser.isEmpty()) {
                    System.out.println("You need to enter a number from above!");
                } else {
                    switch (filterOptionUser) {
                        case "1":
                            System.out.print("Which Book name do you want to filter?> ");
                            bookName = userInput.nextLine();
                            if (bookName.isEmpty()) {
                                System.out.println("You need to enter the name!");
                            } else {
                                PreparedStatement filterBookNameStatement = booklistConnection.prepareStatement(FILTER_BOOK_NAME_QUERY);
                                filterBookNameStatement.setInt(1, userId);
                                filterBookNameStatement.setString(2, bookName);
                                /*
                                todo:
                                add the resultset int counter, print the thing
                                finish the whole thing
                                 */
                            }
                    }
                    break;
                }

            }
        } catch (SQLException e) {
            booklistLogger.log(Level.SEVERE, "SQL Exception occurred while trying to filter for books");
        }
    }

    // this method deletes a book from the DB
    public static void deleteBook(Connection booklistConnection, int userId) {
        try {
            while (true) {
                System.out.print("Which book do you want to delete?> ");
                bookName = userInput.nextLine().toLowerCase();

                System.out.print("What is the name of the author?> ");
                authorName = userInput.nextLine().toLowerCase();

                if (bookName.isEmpty() || authorName.isEmpty()) {
                    System.out.println("You need to enter both the name of the book and the author!");
                } else {
                    boolean bookStatusFound = false;
                    for (String[] options : bookStatusOptions) {
                        if (options[0].equals(bookStatus)) {
                            bookStatus = options[1];
                            bookStatusFound = true;
                            break;
                        }
                    }
                    break;
                }
            }
            PreparedStatement deleteBookStatement = booklistConnection.prepareStatement(DELETE_BOOK_QUERY);
            deleteBookStatement.setInt(1, userId);
            deleteBookStatement.setString(2, bookName);
            deleteBookStatement.setString(3, authorName);
            int rowsAffected = deleteBookStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Book was successfully deleted!");
                System.out.println("");
                userOptions(booklistConnection, userId);
            } else {
                System.out.println("Book is not in your Booklist.");
                userOptions(booklistConnection, userId);
            }
        } catch (SQLException e) {
            booklistLogger.log(Level.SEVERE, "SQL Exception occurred, while deleting a book.", e);
        }
    }

    public static void main(String[] args) {
        try {
            // connection to database
            Connection booklistConnection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3307/booklist", "root", "Tzz$dJG+YccV^HQs");

            // start of the program

            // closing of resources
            booklistConnection.close();

        } catch (SQLException e) {
            booklistLogger.log(Level.SEVERE, "SQL Exception occurred, while connecting to database", e);
        }
    }
}