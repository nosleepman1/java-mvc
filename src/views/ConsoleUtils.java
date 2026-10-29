package views;

import java.util.Scanner;

public class ConsoleUtils {

    private static final Scanner scanner = new Scanner(System.in);

    public static void printHeader(String title) {
        System.out.println();
        System.out.println("===============================================================");
        System.out.println("   " + title);
        System.out.println("===============================================================");
    }

    public static void printSubHeader(String title) {
        System.out.println();
        System.out.println("---------------- " + title + " ----------------");
    }

    public static void printSuccess(String message) {
        System.out.println("[SUCCÈS] " + message);
    }

    public static void printError(String message) {
        System.out.println("[ERREUR] " + message);
    }

    public static void printInfo(String message) {
        System.out.println("[INFO] " + message);
    }

    public static void printWarning(String message) {
        System.out.println("[ATTENTION] " + message);
    }

    public static String readString(String prompt) {
        System.out.print(prompt + ": ");
        return scanner.nextLine().trim();
    }

    public static String readNonEmptyString(String prompt) {
        while (true) {
            System.out.print(prompt + ": ");
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) {
                return input;
            }
            printError("Cette valeur ne peut pas être vide. Veuillez réessayer.");
        }
    }

    public static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt + ": ");
            String input = scanner.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                printError("Veuillez saisir un nombre entier valide.");
            }
        }
    }

    public static int readIntBetween(String prompt, int min, int max) {
        while (true) {
            int value = readInt(prompt);
            if (value >= min && value <= max) {
                return value;
            }
            printError(String.format("Veuillez choisir un nombre entre %d et %d.", min, max));
        }
    }

    public static String readPassword(String prompt) {
        return readNonEmptyString(prompt);
    }

    public static void pause() {
        System.out.println("\nAppuyez sur Entrée pour continuer...");
        scanner.nextLine();
    }
}
