import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;

/*
 Ideas:
 - When user wants to filter for specific
 */
public class Books {
    // Scanner initialization
    public static Scanner userInput = new Scanner(System.in);

    // public var declarations
    public static String userChoice; // either variable or array, to check if user input is in options
    public static String bookName, authorName, bookGenre, bookStatus; // used to get the input and safe it for the DB
    public static String[] filterKeywords = {""};

    // public SQL Queries
    public static final String insertBook = "INSERT INTO BOOKS ";

    /*
     Method below is used to read the user input, check which one and then go to the method, that executes it
     */

    /*
    todo:
    review the code (print lines and the if statements, check if they can be made easier or some)
     */
    public static void userOptions() {
        System.out.println("============Booklist============");
        System.out.println();

        System.out.print("""
                Please choose from the following options (use the numbers):
                 \
                1: Add book
                 \
                2: Change Status
                 \
                3: Change book
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

                    insertIntoBooks(); // goes to the method that executes the sql queries
                    break;
                }
                break;
            case "2": // Change status of a book
            case "3": // Change a column of a book
            case "4": // Filtering for specific words
            case "5": // Delete a book
            default:
                System.out.println("Unexpected error!");
        }
    }


    public static void insertIntoBooks() {
    }

    public static void main(String[] args) {
        try {
            // connection to database
            Connection booklistConnection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/booklist", "root", "Tzz$dJG+YccV^HQs");

            // start of the program
            userOptions();
            // closing of resources
            booklistConnection.close();

        } catch (SQLException e) {
            e.printStackTrace(System.out);
        }
    }
}
