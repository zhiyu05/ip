public class Clementine {
    public static void main(String[] args) {
        System.out.println(line());
        System.out.println(intro());
        System.out.println(line());
        System.out.println(outro());
        System.out.println(line());
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
