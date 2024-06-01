import java.sql.*;
import java.util.Scanner;

public class Users {
    // Initialize Scanner
    public static Scanner userInput = new Scanner(System.in);

    // Username and Password variables
    public static String userName, userPassword;

    // strings to be used as the query for sql
    public static final String checkUserQuery = "SELECT * FROM USERS WHERE USERNAME = ? ";
    public static final String checkUserPwdQuery = "SELECT * FROM USERS WHERE LOWER(USERNAME) = ? AND BINARY USERPASSWORD = ? ";
    public static final String insertNewUserQuery = "INSERT INTO USERS (USERNAME, USERPASSWORD) VALUES (? , ?)";

    /*
     This method is used, to check if the username / account exists in the database. If it exists, it will go
     to the password validation
     */
    public static void checkUserName(Connection booklistConnection) {
        System.out.println("============Login to your booklist============");
        try {
            while (true) {
                System.out.print("Please enter your username> ");
                userName = userInput.nextLine().toLowerCase();
                if (userName.equals("register")) { //It checks, if user types in "register". If so, it jumps to the register method
                    System.out.println("Registering a new user...");
                    registerNewUser(booklistConnection);
                    break;

                } else if (userName.isEmpty()) {
                    System.out.println("Field 'Username' is required!");

                } else {
                    PreparedStatement checkUsersExists = booklistConnection.prepareStatement(checkUserQuery);
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
            e.printStackTrace(System.out);
        }
    }

    /*
     On here, it checks, if the password matches the username in the database. If it matches, the access is granted,
     if it does not match, the user is prompted, to try it again.
     */
    @SuppressWarnings("UnusedReturnValue")
    public static int checkPasswordMatch(Connection booklistConnection, int userId) {
        try {
            while (true) {
                // register new user variable
                System.out.print(userName + " please enter your password> ");
                userPassword = userInput.nextLine();

                if (userPassword.equalsIgnoreCase("register")) {
                    System.out.println("Registering a new user...");
                    registerNewUser(booklistConnection);
                    break;

                } else if (userPassword.isEmpty()) {
                    System.out.println("Field 'Password' is required!");

                } else {
                    PreparedStatement checkUserPwdMatch = booklistConnection.prepareStatement(checkUserPwdQuery);
                    checkUserPwdMatch.setString(1, userName.toLowerCase());
                    checkUserPwdMatch.setString(2, userPassword);
                    ResultSet resultPassWordMatch = checkUserPwdMatch.executeQuery();
                    if (resultPassWordMatch.next()) {
                        System.out.println();
                        System.out.println("Access granted!");
                        System.out.println();
                        Books.userOptions(booklistConnection, userId);
                        break;

                    } else if (!resultPassWordMatch.next()) {
                        System.out.println();
                        System.out.println("Username " + userName + " and password don´t match. Please try again, or type 'register' " + "to register a new user.");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(System.out);
        }
        return userId;
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
                        PreparedStatement insertNewUserStatement = booklistConnection.prepareStatement(insertNewUserQuery);
                        insertNewUserStatement.setString(1, userName);
                        insertNewUserStatement.setString(2, userPassword);
                        int rowsInserted = insertNewUserStatement.executeUpdate();
                        if (rowsInserted > 0) {
                            System.out.println("'" + userName + "'" + " was successfully registered!");
                            break;
                        } else {
                            System.out.println("Something went wrong. Please try again.");
                        }
                    }

                    System.out.println("Username is registered!");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(System.out);
        }
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
            e.printStackTrace(System.out);
        }
    }
}