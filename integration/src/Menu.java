import java.util.Scanner;

public class Menu {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);

        while (true) {
            System.out.println("\n=== Budget App ===");
            System.out.println("1) Accounts (Login/Register)");
            System.out.println("2) Storage (Load/Save CSV)");
            System.out.println("3) Validation (Validate CSV/Input)");
            System.out.println("4) Reports (Monthly/Category Reports)");
            System.out.println("5) Prediction (Forecast Budget)");
            System.out.println("0) Exit");
            System.out.print("Choose: ");

            switch (in.nextLine().trim()) {
                case "1" -> System.out.println("[Accounts] — TODO: call accounts module");
                case "2" -> System.out.println("[Storage] — TODO: call storage module");
                case "3" -> System.out.println("[Validation] — TODO: call validation module");
                case "4" -> System.out.println("[Reports] — TODO: call reports module");
                case "5" -> System.out.println("[Prediction] — TODO: call prediction module");
                case "0" -> {
                    System.out.println("Goodbye!");
                    return;
                }
                default -> System.out.println("Invalid option — try again.");
            }
        }
    }
}
