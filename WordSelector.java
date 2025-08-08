import java.util.*;
import java.util.concurrent.TimeUnit;

public class WordSelector {
    public static Word selectReviewWord(List<Word> words) {
        if (words.isEmpty()) return null;
        
        // 计算总权重
        float totalUrgency = 0;
        Map<Word, Float> urgencyMap = new HashMap<>();
        long currentTime = System.currentTimeMillis();
        
        for (Word word : words) {
            float urgency = calculateUrgency(word, currentTime);
            urgencyMap.put(word, urgency);
            totalUrgency += urgency;
        }
        
        // 随机选择（基于权重）
        if (totalUrgency <= 0) return words.get(new Random().nextInt(words.size()));
        
        float randomPoint = new Random().nextFloat() * totalUrgency;
        float cumulative = 0;
        
        for (Word word : words) {
            cumulative += urgencyMap.get(word);
            if (cumulative >= randomPoint) {
                return word;
            }
        }
        
        return words.get(new Random().nextInt(words.size()));
    }
    
    private static float calculateUrgency(Word word, long currentTime) {
        // 基础紧迫度 = 1 - 记忆强度
        float baseUrgency = 1.0f - word.calculateMemoryStrength();
        
        // 新词权重加倍
        float noveltyFactor = (word.getConsecutiveCorrect() < 3) ? 1.5f : 1.0f;
        
        // 超期复习权重
        float overdueFactor = 1.0f + calculateOverdueFactor(word, currentTime);
        
        return baseUrgency * noveltyFactor * overdueFactor;
    }
    
    private static float calculateOverdueFactor(Word word, long currentTime) {
        if (word.getInterval() == 0) return 0;
        
        long nextReviewTime = word.getLastReviewed().getTime() + 
                             TimeUnit.DAYS.toMillis(word.getInterval());
        
        if (currentTime > nextReviewTime) {
            long overdueDays = TimeUnit.DAYS.convert(
                currentTime - nextReviewTime, TimeUnit.MILLISECONDS);
            return overdueDays * 0.2f;
        }
        return 0;
    }
}