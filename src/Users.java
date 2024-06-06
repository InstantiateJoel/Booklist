import java.sql.*;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Users {
    // Initialize Scanner
    private static final Scanner userInput = new Scanner(System.in);
    private static final Logger usersLogger = Logger.getLogger(Users.class.getName());

    // Username and Password variables
    private static String userName = "";
    private static String userPassword;

    // strings to be used as the query for sql
    public static final String CHECK_USER_QUERY = "SELECT * FROM USERS WHERE USERNAME = ?";
    public static final String CHECK_USER_PWD_QUERY = "SELECT * FROM USERS WHERE LOWER(USERNAME) = ? AND BINARY USERPASSWORD = ?";
    public static final String INSERT_NEW_USER_QUERY = "INSERT INTO USERS (USERNAME, USERPASSWORD) VALUES (? , ?)";

    /*
     This method is used, to check if the username / account exists in the database. If it exists, it will go
     to the password validation. If user types in 'register' it goes to the register method
     */
    public static void checkUserName(Connection booklistConnection) {
        System.out.println("============Login to your booklist============");
        try {
            while (true) {
                System.out.print("Please enter your username> ");
                userName = userInput.nextLine().toLowerCase();
                if (userName.equals("register")) {
                    System.out.println("Registering a new user...");
                    registerNewUser(booklistConnection);
                    break;

                } else if (userName.isEmpty()) {
                    System.out.println("Field 'Username' is required!");

                } else {
                    PreparedStatement checkUsersExists = booklistConnection.prepareStatement(CHECK_USER_QUERY);
                    checkUsersExists.setString(1, userName);
                    ResultSet resultCheckUser = checkUsersExists.executeQuery();
                    if (resultCheckUser.next()) {
                        int userId = resultCheckUser.getInt("USERID");
                        checkPasswordMatch(booklistConnection, userId);
                        break;

                    } else if (!resultCheckUser.next()) {
                        System.out.println("The user " + "'" + userName + "'" + " was not found. " + "Please try again or type 'register', to register a new user.");
                    }
                }
            }
        } catch (SQLException e) {
            usersLogger.log(Level.SEVERE, "SQL Exception occurred while checking username.");
        }
    }

    /*
     On here, it checks, if the password matches the username in the database. If it matches, the access is granted,
     if it does not match, the user is prompted, to try it again.
     */
    public static int checkPasswordMatch(Connection booklistConnection, int userId) {
        try {
            while (true) {
                System.out.print(userName + " please enter your password> ");
                userPassword = userInput.nextLine();

                if (userPassword.equalsIgnoreCase("register")) {
                    System.out.println("Registering a new user...");
                    registerNewUser(booklistConnection);
                    break;

                } else if (userPassword.isEmpty()) {
                    System.out.println("Field 'Password' is required!");

                } else {
                    PreparedStatement checkUserPwdMatch = booklistConnection.prepareStatement(CHECK_USER_PWD_QUERY);
                    checkUserPwdMatch.setString(1, userName.toLowerCase());
                    checkUserPwdMatch.setString(2, userPassword);
                    ResultSet resultPassWordMatch = checkUserPwdMatch.executeQuery();
                    if (resultPassWordMatch.next()) {
                        System.out.println();
                        System.out.println("Access granted!");
                        System.out.println();
                        Books.printAllBooks(booklistConnection, userId);
                        break;

                    } else if (!resultPassWordMatch.next()) {
                        System.out.println();
                        System.out.println("Username " + userName + " and password don´t match. Please try again, or type 'register' " + "to register a new user.");
                    }
                }
            }
        } catch (SQLException e) {
            usersLogger.log(Level.SEVERE,"SQL Exception occurred, while checking the password.");
        }
        return userId;
    }

    public static void main(String[] args) {
        try {
            // connection to database
            Connection booklistConnection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3307/booklist", "root", "Tzz$dJG+YccV^HQs");

            // start of the whole program
            checkUserName(booklistConnection);

            // closing of resources
            userInput.close();
            booklistConnection.close();

        } catch (SQLException e) {
            usersLogger.log(Level.SEVERE, "SQL Exception occurred, while trying to check  username and password.");
        }
    }

    /*
     Here are the new users registered. They choose their user / account name and their password
    */
    public static void registerNewUser(Connection booklistConnection) {
        try {
            while (true) {
                System.out.print("Please enter the username you want> ");
                userName = userInput.nextLine();
                if (userName.isEmpty()) {
                    System.out.println("Field 'Username' is required!");
                } else {
                    System.out.print("Enter the password you want> ");
                    userPassword = userInput.nextLine();
                    if (userPassword.isEmpty()) {
                        System.out.println("Field 'Password' is required!");
                    } else {
                        PreparedStatement insertNewUserStatement = booklistConnection.prepareStatement(INSERT_NEW_USER_QUERY);
                        insertNewUserStatement.setString(1, userName);
                        insertNewUserStatement.setString(2, userPassword);
                        int rowsInserted = insertNewUserStatement.executeUpdate();
                        if (rowsInserted > 0) {
                            System.out.println("'" + userName + "'" + " was successfully registered!");
                            checkUserName(booklistConnection);
                            break;
                        } else {
                            System.out.println("Something went wrong. Please try again.");
                        }
                    }
                    System.out.println("Username is registered!");
                }
            }
        } catch (SQLException e) {
           usersLogger.log(Level.SEVERE,"SQL Exception occurred, while registering new user.");
        }
    }
}
