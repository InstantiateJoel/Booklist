/*import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;// changes the status of a book
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
*/