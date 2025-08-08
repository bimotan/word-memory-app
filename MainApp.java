import java.util.*;
import java.io.*;
public class MainApp {
    public static void main(String[] args) {
        System.out.println("=== 智能单词学习系统 ===");
        
        // 文件选择
        String fileName = selectWordFile();
        WordManager wordManager = new WordManager(fileName);
        
        while (true) {
            System.out.println("\n请选择操作:");
            System.out.println("1. 添加新单词");
            System.out.println("2. 复习单词");
            System.out.println("3. 查看所有单词");
            System.out.println("4. 退出");
            
            int choice = InputUtils.inputInt();
            
            switch (choice) {
                case 1:
                    addNewWords(wordManager);
                    break;
                case 2:
                    reviewWords(wordManager);
                    break;
                case 3:
                    listAllWords(wordManager);
                    break;
                case 4:
                    System.out.println("感谢使用，再见!");
                    return;
                default:
                    System.out.println("无效选择，请重新输入!");
            }
        }
    }
    
    private static String selectWordFile() {
        List<String> textFiles = FileUtils.listTextFiles();
        
        if (textFiles.isEmpty()) {
            System.out.println("未找到单词文件，创建新文件...");
            return createNewWordFile();
        }
        
        System.out.println("请选择单词文件:");
        for (int i = 0; i < textFiles.size(); i++) {
            System.out.println((i + 1) + ". " + textFiles.get(i));
        }
        System.out.println((textFiles.size() + 1) + ". 创建新文件");
        
        int choice = InputUtils.inputInt();
        if (choice > 0 && choice <= textFiles.size()) {
            return textFiles.get(choice - 1);
        } else if (choice == textFiles.size() + 1) {
            return createNewWordFile();
        } else {
            System.out.println("无效选择，使用默认文件");
            return "default_words.txt";
        }
    }
    
    private static String createNewWordFile() {
        try {
            System.out.print("请输入新文件名: ");
            String fileName = InputUtils.inputString();
            return FileUtils.createTextFile(fileName);
        } catch (IOException e) {
            System.err.println("创建文件失败: " + e.getMessage());
            return "words.txt";
        }
    }
    
    private static void addNewWords(WordManager manager) {
        System.out.print("请输入要添加的单词数量: ");
        int count = InputUtils.inputInt();
        
        for (int i = 0; i < count; i++) {
            System.out.printf("\n添加单词 (%d/%d)\n", i + 1, count);
            System.out.print("英文(输入-1来结束单词输入): ");
            String english = InputUtils.inputString();
            if(english.equals("-1")){
                System.out.println("you have end adding new words");
                System.exit(0);
        }
            System.out.print("中文: ");
            String chinese = InputUtils.inputLine();
            
            manager.addWord(english, chinese);
        }
        System.out.println("单词添加完成!");
    }
    
    private static void reviewWords(WordManager manager) {
        System.out.print("请输入要复习的单词数量: ");
        int count = InputUtils.inputInt();
        
        for (int i = 0; i < count; i++) {
            System.out.printf("\n复习单词 (%d/%d)\n", i + 1, count);
            Word word = manager.getReviewWord();
            if (word != null) {
                manager.reviewWord(word);
            } else {
                System.out.println("没有可复习的单词!");
                return;
            }
        }
        System.out.println("复习完成!");
    }
    
    private static void listAllWords(WordManager manager) {
        List<Word> words = manager.getAllWords();
        if (words.isEmpty()) {
            System.out.println("单词列表为空!");
            return;
        }
        
        System.out.println("\n单词列表:");
        System.out.println("----------------------------------------------");
        System.out.printf("%-20s %-20s %-10s\n", "英文", "中文", "记忆强度");
        System.out.println("----------------------------------------------");
        
        for (Word word : words) {
            System.out.printf("%-20s %-20s %-10.0f%%\n", 
                word.getEnglish(), 
                word.getChinese(),
                word.calculateMemoryStrength() * 100);
        }
        System.out.println("----------------------------------------------");
        System.out.println("总计: " + words.size() + " 个单词");
    }
}