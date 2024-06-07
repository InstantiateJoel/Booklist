import java.util.Scanner;

public class Testing{
    public static void main(String[] args) {
        String userOption = "";
        Scanner sc = new Scanner(System.in);
        String[][] bookStatusOptions = {
                {"1", "ordered"},
                {"2", "shelf"},
                {"3", "read"},
                {"4", "lend"}
        };

        System.out.println("Choose option");
        userOption = sc.nextLine();

        for (String[] option : bookStatusOptions) {
            if (option[0].equals(userOption)) {
                String statusBook = option[1];
                System.out.println(statusBook);
                break;
            }
        }
    }
}
