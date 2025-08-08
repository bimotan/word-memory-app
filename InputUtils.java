import java.util.Scanner;

public class InputUtils {
    private static final Scanner scanner = new Scanner(System.in);
    
    public static int inputInt() {
        System.out.print("请输入数字: ");
        while (!scanner.hasNextInt()) {
            System.out.println("输入无效，请重新输入数字!");
            scanner.next();
        }
        return scanner.nextInt();
    }
    
    public static String inputString() {
        System.out.print("请输入: ");
        return scanner.next().trim();
    }
    
    public static String inputLine() {
        System.out.print("请输入: ");
        scanner.nextLine(); // 清除缓冲区
        return scanner.nextLine().trim();
    }
}