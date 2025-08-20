import java.util.Scanner;
public class Clementine {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println(line());
        System.out.println(intro());
        System.out.println(line());
        while(scanner.hasNextLine()) {
            String response = scanner.nextLine();

            if (response.equals("bye")) {
                System.out.println(line());
                System.out.println(outro());
                System.out.println(line());
                break;
            }
            System.out.println(line());
            System.out.println(response + " quack!");
            System.out.println(line());
        }
        scanner.close();
    }

    public static String intro () {
        return "Quack! I'm clementine\n What can i help you with today?\n";
    }

    public static String outro () {
        return "Bye! quack u later! hope you have a great day!";
    }

    public static String line() {
        return "______________________________________________\n";
    }


}
