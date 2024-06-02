import java.sql.*;
import java.util.Scanner;

/*
 Ideas:
 - Check if the book already exists in the DB (when inserting a new book)
 - When printing all the books, make it in a "table view" like the DB rows
 - either make table wishlist (with wishlist class maybe then) or use the status wishlist

 Code ideas:
 when giving the status for the insert/ change etc. Maybe make an array? (1, read; 2, ordered...) so there´s no typo problem with inputs
 */

/*
 todo:
 1. Order of the switch case (which option which number)
 2. Debug EVERYTHING
 */

public class Books {
    // Scanner initialization
    public static Scanner userInput = new Scanner(System.in);

    // Variable declarations
    public static String userChoice; // used to check which case from switch should be used
    public static String bookName, authorName, bookGenre, bookStatus; // used to get the input and safe it for the DB

    // SQL Queries
    public static final String insertBooksQuery = "INSERT INTO BOOKS (USER_ID, NAME, AUTHOR, GENRE, STATUS) VALUES (?, ?, ?, ?, ?)";
    public static final String deleteBookQuery = "DELETE FROM BOOKS WHERE USER_ID = ? AND NAME = ? AND AUTHOR = ?";
    // public static final String checkBookExistsQuery = "SELECT * FROM BOOKLIST WHERE NAME = ? AND AUTHOR = ? AND USER_ID = ?";

    /*
     Method below is used to read the user input, check which one and then go to the method, that executes it
     */
    public static void userOptions(Connection booklistConnection, int userId) {
        System.out.println("============Booklist============");
        System.out.println();
        System.out.println("These are your books:");
        /*
         todo:
         see if you can print like a "table" with every book displayed like the sql rows
         number 5 in print line (write that different)
         change the switch case (add the new case)
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
                5 : Select / show all books
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
                while (true) {
                    System.out.print("Enter the name of the book> ");
                    bookName = userInput.nextLine();
                    if (bookName.isEmpty()) {
                        System.out.println("The name of the book is required!");
                        continue;
                    }
                    System.out.print("Enter the name of the author> ");
                    authorName = userInput.nextLine();
                    if (authorName.isEmpty()) {
                        System.out.println("The name of the author is required!");
                        continue;
                    }
                    System.out.print("Enter the genre of the book. If you don´t want to add it, please proceed with 'enter'> ");
                    bookGenre = userInput.nextLine();
                    if (bookGenre.isEmpty()) {
                        bookGenre = "Not defined";
                    }

                    System.out.print("Enter the name of the status (read, shelf, ordered, lend)> ");
                    bookStatus = userInput.nextLine();
                    if (bookStatus.isEmpty()) {
                        System.out.println("The status of the book is required!");
                        continue;
                    }
                    System.out.println("Name: " + bookName);
                    System.out.println("Author: " + authorName);
                    if (bookGenre.isEmpty()) {
                        System.out.println("Genre not specified.");
                    } else {
                        System.out.println("Genre: " + bookGenre);
                    }
                    System.out.println("Status: " + bookStatus);

                    insertIntoBooks(booklistConnection, userId);
                    break;
                }
                break;
            case "2":// Change status of a book
            case "3": // Change a column of a book
            case "4": // Filtering for specific books
            case "5": // delete a book
                deleteBook(booklistConnection, userId);
            default:
                System.out.println("Unexpected error!");
        }
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
                System.out.println("There was an error. Please try again or contact the administrator!");
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