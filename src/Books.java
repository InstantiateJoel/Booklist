import java.sql.*;
import java.util.Scanner;

/*
 Ideas:
 - When printing all the books, make it in a "table view" like the DB rows
 - either make table wishlist (with wishlist class maybe then) or use the status wishlist

 Code ideas:
 when giving the status for the insert/ change etc. Maybe make an array? (1, read; 2, ordered...) so there´s no typo problem with inputs
 */

/*
 todo:
 1. Make the print of the books more "beautiful"
 */

public class Books {
    // Scanner initialization
    public static Scanner userInput = new Scanner(System.in);

    // Variable declarations
    public static String userChoice; // used to check which case from switch should be used
    public static String bookName, authorName, bookGenre, bookStatus; // used to get the input and safe it for the DB
    public static String selectedBookName, selectedAuthorName, selectedGenre, selectedStatus;
    public static int selectedId;

    // SQL Queries
    public static final String insertBooksQuery = "INSERT INTO BOOKS (USER_ID, NAME, AUTHOR, GENRE, STATUS) VALUES (?, ?, ?, ?, ?)";
    public static final String deleteBookQuery = "DELETE FROM BOOKS WHERE USER_ID = ? AND NAME = ? AND AUTHOR = ?";
    public static final String checkBookExistsQuery = "SELECT * FROM BOOKS WHERE NAME = ? AND AUTHOR = ? AND USER_ID = ?";
    public static final String selectAllBooksQuery = "SELECT * FROM BOOKS WHERE USER_ID = ?";

    /*
     Method below is used to read the user input, check which one and then go to the method, that executes it
     */
    public static void printAllBooks(Connection booklistConnection, int userId) {
        try {
            // preparing sql statements
            PreparedStatement selectAllBooksStatement = booklistConnection.prepareStatement(selectAllBooksQuery);
            selectAllBooksStatement.setInt(1, userId);
            ResultSet selectAllBooksResult = selectAllBooksStatement.executeQuery();

            System.out.println("================BOOKLIST================");
            System.out.println("These are your books:");
            while (selectAllBooksResult.next()) {
                selectedId = selectAllBooksResult.getInt("USER_ID");
                selectedBookName = selectAllBooksResult.getString("NAME");
                selectedAuthorName = selectAllBooksResult.getString("AUTHOR");
                selectedGenre = selectAllBooksResult.getString("GENRE");
                selectedStatus = selectAllBooksResult.getString("STATUS");
                System.out.println("Title: " + selectedBookName + "     Author: " + selectedAuthorName  + "     Genre: " + selectedGenre + "     Status: " + selectedStatus);
            }
            userOptions(booklistConnection, userId);
        } catch (SQLException e) {
            e.printStackTrace(System.out);
        }
    }

        public static void userOptions(Connection booklistConnection, int userId) {
        /*
         todo:
         1. see if you can print like a "table" with every book displayed like the sql rows
         number 5 in print line (write that different)
         change the switch case (add the new case)
         2. Wishlist
         */
        System.out.print("""
                Please choose from the following options (use the numbers):
                 \
                1: Add book
                 \
                2: Alter Status
                 \
                3: Alter book
                 \
                4: Filter
                 \
                5: Select / show all books
                 \
                6: Delete book
                 \
                Enter number>""");
        while (true) {
            userChoice = userInput.nextLine();
            if (userChoice.isEmpty()) {
                System.out.println("You need to enter one number from above, or type 'help' to get the possibilities again.");
            } else {
                break;
            }
        }
        switch (userChoice) {
            case "1": // add book
                addBook(booklistConnection, userId);
                break;
            case "2":// Change status of a book
                break;
            case "3": // Change a column of a book
                break;
            case "4": // Filtering for specific books
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
                continue;
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

            System.out.print("Enter the name of the status (read, shelf, ordered, lend)> ");
            bookStatus = userInput.nextLine();
            if (bookStatus.isEmpty()) {
                System.out.println("The status of the book is required!");
                continue;
            } else if (bookStatus.equals("options")) {
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
        try {
                    PreparedStatement checkBookExistStatement = booklistConnection.prepareStatement(checkBookExistsQuery);
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
            e.printStackTrace(System.out);
        }
        return false;
    }


    // this method inserts a new book in the DB
    public static void insertIntoBooks(Connection booklistConnection, int userId) {
        try {
            PreparedStatement insertBooksStatement = booklistConnection.prepareStatement(insertBooksQuery);
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
            e.printStackTrace(System.out);
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
                    break;
                }
            }
            PreparedStatement deleteBookStatement = booklistConnection.prepareStatement(deleteBookQuery);
            deleteBookStatement.setInt(1, userId);
            deleteBookStatement.setString(2, bookName);
            deleteBookStatement.setString(3, authorName);
            int rowsAffected = deleteBookStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Book was successfully deleted!");
                System.out.println();
                userOptions(booklistConnection, userId);
            } else {
                System.out.println("Book is not in your Booklist.");
                userOptions(booklistConnection, userId);
            }
        } catch (SQLException e) {
            e.printStackTrace(System.out);
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
            e.printStackTrace(System.out);
        }
    }
}
