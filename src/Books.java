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
1. when printing books, and no books are existent, print no books yet or some (done)
2. add exit, to exit and log out of the book list
3. Make a method, that gets all the user input for the books (name, author...) so there´s basically only the methods left,
 that make the sql parts.
 */


public class Books {
    // Scanner initialization
    public static final Scanner userInput = new Scanner(System.in);

    // logger initialization
    private static final Logger booklistLogger = Logger.getLogger(Books.class.getName());

    private static String bookName = "";
    private static String authorName;
    private static String bookGenre;
    private static String selectedBookName;
    private static String selectedAuthorName;
    public static int selectedId;

    // SQL Queries
    public static final String INSERT_INTO_BOOKS_QUERY = "INSERT INTO BOOKS (USER_ID, NAME, AUTHOR, GENRE) VALUES (?, ?, ?, ?)";
    public static final String DELETE_BOOK_QUERY = "DELETE FROM BOOKS WHERE USER_ID = ? AND NAME = ? AND AUTHOR = ?";
    public static final String CHECK_BOOK_EXISTS_QUERY = "SELECT * FROM BOOKS WHERE NAME = ? AND AUTHOR = ? AND USER_ID = ?";
    public static final String SELECT_ALL_BOOKS_QUERY = "SELECT * FROM BOOKS WHERE USER_ID = ?";

    /*
     Method below is used to read the user input, check which one and then go to the method, that executes it
     */
    public static void printAllBooks(Connection booklistConnection, int userId) {
        String selectedGenre;
        try {
            // preparing sql statements
            PreparedStatement selectAllBooksStatement = booklistConnection.prepareStatement(SELECT_ALL_BOOKS_QUERY);
            selectAllBooksStatement.setInt(1, userId);
            ResultSet selectAllBooksResult = selectAllBooksStatement.executeQuery();

            System.out.println("===========BOOK-LIST===========");
            System.out.println("These are your books:");

            if (selectAllBooksResult.next()) {
                while (selectAllBooksResult.next()) {
                    selectedId = selectAllBooksResult.getInt("USER_ID");
                    selectedBookName = selectAllBooksResult.getString("NAME");
                    selectedAuthorName = selectAllBooksResult.getString("AUTHOR");
                    selectedGenre = selectAllBooksResult.getString("GENRE");
                    // print all the books
                    System.out.println("------------------------------");
                    System.out.println("Title:  " + selectedBookName);
                    System.out.println("Author: " + selectedAuthorName);
                    System.out.println("Genre:  " + selectedGenre);
                }
                } else {
                System.out.println("No books where found yet.");
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
                3: Delete a book
                 \
                4: Exit
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
            case "3": // delete a book
                deleteBook(booklistConnection, userId);
                break;
            case "4":
                System.exit(69);
                break;
            default:
                System.out.println("Unexpected error!");
        }
    }

    // add the user inputs method here, that gets all the user inputs for the books, to just simply ask
    // everything needed

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
                System.out.println(" ");
                userOptions(booklistConnection, userId);
            } else {
                System.out.println("Book is not in your Book-list.");
                userOptions(booklistConnection, userId);
            }
        } catch (SQLException e) {
            booklistLogger.log(Level.SEVERE, "SQL Exception occurred, while deleting a book.", e);
        }
    }

    public static void main(String[] args) {
        try {
            // connection to database
            Connection booklistConnection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3307/booklist",
                    "root",
                    "Tzz$dJG+YccV^HQs");

            // closing of resources
            booklistConnection.close();
            userInput.close();

        } catch (SQLException e) {
            booklistLogger.log(Level.SEVERE, "SQL Exception occurred, while connecting to database", e);
        }
    }
}