import java.util.Scanner;
public class Clementine {
    private static String[] tasks = new String[100];
    private static int taskCount = 0;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println(line());
        System.out.println(intro());
        System.out.println(line());
        while(scanner.hasNextLine()) {
            String input = scanner.nextLine();

            if (input.equals("bye")) {
                System.out.println(line());
                System.out.println(outro());
                System.out.println(line());
                break;
            } else if (input.equals("list")) {
                System.out.println(line());
                System.out.println(listTasks(tasks));
                System.out.println(line());
            } else {
                System.out.println(line());
                System.out.println(addTask(input, tasks));
                System.out.println(line());
            }
        }
        scanner.close();
    }

    public static String intro () {
        return "Quack! I'm clementine\n What can i help you with today?\n";
    }

    public static String outro () {
        System.out.println(line());
        System.out.println("Bye! quack u later! hope you have a great day!");
        System.out.println(line());
        return "Bye! quack u later! hope you have a great day!";
    }

    public static String line() {
        return "______________________________________________\n";
    }

    public static String addTask (String task, String[] tasks) {
        tasks[taskCount] = task;
        String response = "okay! added: " + task + " quack!";
        taskCount++;
        return response;
    }

    public static String listTasks (String[] tasks) {
        String response = "";
        for (int i = 1; i <= taskCount; i++) {
            response += i + ". " + tasks[i - 1] + "\n";
        }
        return response;
    }

    public static Boolean isListCommand (String response) {
        return response.equals("list");
    }

}
