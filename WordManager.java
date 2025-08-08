import java.io.*;
import java.util.*;
import java.text.SimpleDateFormat;

public class WordManager {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private List<Word> wordList = new ArrayList<>();
    private String fileName;
    
    public WordManager(String fileName) {
        this.fileName = fileName;
        loadWords();
    }
    
    // 添加新单词
    public void addWord(String english, String chinese) {
        // 检查是否已存在
        for (Word word : wordList) {
            if (word.getEnglish().equalsIgnoreCase(english)) {
                System.out.println("单词已存在: " + english);
                return;
            }
        }
        wordList.add(new Word(english, chinese));
        saveWords();
    }
    
    // 复习单词
    public void reviewWord(Word word) {
        System.out.println("\n请输入单词 [" + word.getEnglish() + "] 的中文意思(提示：输入-1以退出)：");
        String userAnswer = InputUtils.inputString();
        
        if(userAnswer.equals("-1")){
            System.out.println("you have end the review");
            System.exit(0);
        }
        
        // 显示正确答案
        System.out.println("\n正确答案: " + word.getChinese());
        System.out.println("你的答案: " + userAnswer);
        
        // 评估相似度
        float similarity = calculateSimilarity(userAnswer, word.getChinese());
        int quality = evaluateQuality(similarity);
        
        // 更新记忆数据
        SpacedRepetition.updateMemory(word, quality);
        
        // 显示记忆状态
        System.out.printf("记忆强度: %.0f%%\n", word.calculateMemoryStrength() * 100);
        System.out.println("下次复习间隔: " + word.getInterval() + "天后");
        
        saveWords();
    }
    
    // 获取需要复习的单词
    public Word getReviewWord() {
        return WordSelector.selectReviewWord(wordList);
    }
    
    public List<Word> getAllWords() {
        return new ArrayList<>(wordList);
    }
    
    private float calculateSimilarity(String a, String b) {
        // 简化的相似度计算
        if (a.equalsIgnoreCase(b)) return 1.0f;
        
        Set<Character> setA = new HashSet<>();
        for (char c : a.toCharArray()) setA.add(c);
        
        Set<Character> setB = new HashSet<>();
        for (char c : b.toCharArray()) setB.add(c);
        
        Set<Character> intersection = new HashSet<>(setA);
        intersection.retainAll(setB);
        
        Set<Character> union = new HashSet<>(setA);
        union.addAll(setB);
        
        return (float) intersection.size() / union.size();
    }
    
    private int evaluateQuality(float similarity) {
        if (similarity > 0.9) return 5;
        if (similarity > 0.7) return 4;
        if (similarity > 0.5) return 3;
        if (similarity > 0.3) return 2;
        return 1;
    }
    
    // 文件操作
    private void loadWords() {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length == 7) {
                    Date addedDate = DATE_FORMAT.parse(parts[2]);
                    Date lastReviewed = DATE_FORMAT.parse(parts[3]);
                    float easiness = Float.parseFloat(parts[4]);
                    int interval = Integer.parseInt(parts[5]);
                    int consecutiveCorrect = Integer.parseInt(parts[6]);
                    
                    wordList.add(new Word(parts[0], parts[1], addedDate, 
                                        lastReviewed, easiness, interval, consecutiveCorrect));
                }
            }
        } catch (Exception e) {
            System.err.println("加载单词失败: " + e.getMessage());
        }
    }
    
    private void saveWords() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName))) {
            for (Word word : wordList) {
                String line = word.getEnglish() + ";" + word.getChinese() + ";" +
                              DATE_FORMAT.format(word.getAddedDate()) + ";" +
                              DATE_FORMAT.format(word.getLastReviewed()) + ";" +
                              word.getEasiness() + ";" + word.getInterval() + ";" +
                              word.getConsecutiveCorrect();
                bw.write(line);
                bw.newLine();
            }
        } catch (Exception e) {
            System.err.println("保存单词失败: " + e.getMessage());
        }
    }
}