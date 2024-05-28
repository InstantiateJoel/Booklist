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

    // public SQL Queries
    public static void userOptions() {
        System.out.println("============Booklist============");
        System.out.println();

        while (true) {
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
            userChoice = userInput.nextLine();
            // validate user input here and start with switch case and so on
        }
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
