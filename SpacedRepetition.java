import java.util.Date;

public class SpacedRepetition {
    private static final float MIN_EASINESS = 1.3f;
    private static final int[] INITIAL_INTERVALS = {1, 3, 7}; // 天为单位
    
    public static void updateMemory(Word word, int quality) {
        if (quality < 3) { // 回答错误
            word.setConsecutiveCorrect(0);
            word.setInterval(1);
        } else { // 回答正确
            int repetitions = word.getConsecutiveCorrect() + 1;
            word.setConsecutiveCorrect(repetitions);
            
            // 更新易度因子
            float newEasiness = word.getEasiness() + 
                (0.1f - (5 - quality) * (0.08f + (5 - quality) * 0.02f));
            word.setEasiness(Math.max(newEasiness, MIN_EASINESS));
            
            // 设置复习间隔
            if (repetitions <= 3) {
                word.setInterval(INITIAL_INTERVALS[repetitions - 1]);
            } else {
                word.setInterval((int)(word.getInterval() * word.getEasiness()));
            }
        }
        
        // 更新最后复习时间
        word.setLastReviewed(new Date());
    }
}