import java.util.Date;

public class Word {
    private String english;
    private String chinese;
    private Date addedDate;
    private Date lastReviewed;
    private float easiness = 2.5f;
    private int interval = 0;
    private int consecutiveCorrect = 0;
    
    public Word(String english, String chinese) {
        this.english = english;
        this.chinese = chinese;
        this.addedDate = new Date();
        this.lastReviewed = new Date();
    }
    
    // 文件构造器
    public Word(String english, String chinese, Date addedDate, Date lastReviewed, 
                float easiness, int interval, int consecutiveCorrect) {
        this.english = english;
        this.chinese = chinese;
        this.addedDate = addedDate;
        this.lastReviewed = lastReviewed;
        this.easiness = easiness;
        this.interval = interval;
        this.consecutiveCorrect = consecutiveCorrect;
    }
    
    // Getters & Setters
    public String getEnglish() { return english; }
    public String getChinese() { return chinese; }
    public Date getAddedDate() { return addedDate; }
    public Date getLastReviewed() { return lastReviewed; }
    public void setLastReviewed(Date date) { this.lastReviewed = date; }
    public float getEasiness() { return easiness; }
    public void setEasiness(float easiness) { this.easiness = easiness; }
    public int getInterval() { return interval; }
    public void setInterval(int interval) { this.interval = interval; }
    public int getConsecutiveCorrect() { return consecutiveCorrect; }
    public void setConsecutiveCorrect(int count) { this.consecutiveCorrect = count; }
    
    // 计算记忆强度
    public float calculateMemoryStrength() {
        long hoursSinceReview = (System.currentTimeMillis() - lastReviewed.getTime()) / (1000 * 3600);
        float stability = 5.0f * easiness * (1 + consecutiveCorrect / 10.0f);
        return (float) Math.exp(-hoursSinceReview / stability);
    }
}