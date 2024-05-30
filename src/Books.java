import java.sql.*;
import java.util.Scanner;

/*
 todo:
 1. Password is not case sensitive anymore? -> hotfix!
 2. User can delete ALL books from EVERY user -> hotfix!
    possible fix:
    I need to check, if the two ID´s are matching
 3. If book is deleted, code still says "something went wrong", even if it is not in the db anymore
 4. When all above is done => Debug EVERYTHING
 */

public class Books {
    // Scanner initialization
    public static Scanner userInput = new Scanner(System.in);

    // public var declarations
    public static String userChoice; // used to check which case from switch should be used
    public static String bookName, authorName, bookGenre, bookStatus; // used to get the input and safe it for the DB

    // public SQL Queries
    public static final String insertBooksQuery = "INSERT INTO BOOKS (USER_ID, NAME, AUTHOR, GENRE, STATUS) VALUES (?, ?, ?, ?, ?)";
    public static final String deleteBookQuery = "DELETE FROM BOOKS WHERE USER_ID = ? AND NAME = ? AND AUTHOR = ?";

    /*
     Method below is used to read the user input, check which one and then go to the method, that executes it
     */

    /*
    todo:
    review the code (print lines and the if statements, check if they can be made easier or some)
     */
    public static void userOptions(Connection booklistConnection, int userId) {
        System.out.println("============Booklist============");
        System.out.println();

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
                5: Delete book
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

                    insertIntoBooks(booklistConnection, userId); // goes to the method that executes the sql queries
                    break;
                }
                break;
            case "2":// Change status of a book
            case "3": // Change a column of a book
            case "4": // Filtering for specific words
            case "5": // delete a book
                while (true) {
                    System.out.println("Which book do you want to delete?> ");
                    bookName = userInput.nextLine();

                    System.out.println("What is the name of the author?> ");
                    authorName = userInput.nextLine();

                    if (bookName.isEmpty() || authorName.isEmpty()) {
                        System.out.println("You need to enter both the name of the book and the author!");
                    } else {
                        deleteBook(booklistConnection, userId); // goes to the method, that deletes the book
                        break;
                    }
                }
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
            PreparedStatement deleteBookStatement = booklistConnection.prepareStatement(deleteBookQuery);
            deleteBookStatement.setInt(1, userId);
            deleteBookStatement.setString(2, bookName);
            deleteBookStatement.setString(3, authorName);
            int rowsAffected = deleteBookStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Book was successfully deleted!");
                userOptions(booklistConnection, userId);
            } else {
                System.out.println("There was an error. Please try again, or contact your admin!");
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