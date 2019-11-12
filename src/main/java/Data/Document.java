package Data;

import java.util.UUID;

public class Document {
    /* need to define level and suggest to user, these may be set by user or developer with constraint.*/
    /* Cacheを行うか, あるいは置き換えるかの判断に用いる*/

    /** METADATA **/
    private UUID id;
    /*とりあえず3段階 1.弱い, 2. 普通, 3. 強い*/
    private int consistencyLevel = 2;
    private int priorityLevel = 2;
    /* サイズはドキュメントがポストされたときに動的に取得する必要がある. Simulationでは固定?*/
    private int size = 1;

    /** Part of Body **/
    private int userId;

    public Document(UUID id, int userId) {
        this.id = id;
        this.userId = userId;
    }

    public int getConsistencyLevel() {
        return consistencyLevel;
    }

    public void setConsistencyLevel(int consistencyLevel) {
        if(consistencyLevel >= 1 && consistencyLevel <= 3){
            this.consistencyLevel = consistencyLevel;
        } else {
            System.err.println("You can set 1(Weak), 2(Normal), 3(Strong) as consistency level");
            return;
        }
    }

    public int getPriorityLevel() {
        return priorityLevel;
    }

    public void setPriorityLevel(int priorityLevel) {
        if(consistencyLevel >= 1 && consistencyLevel <= 3){
            this.consistencyLevel = consistencyLevel;
        } else {
            System.err.println("You can set 1(Weak), 2(Normal), 3(Strong) as priority level");
            return;
        }
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public boolean isCached() {
        return isCached;
    }

    public void setCached(boolean cached) {
        isCached = cached;
    }

    boolean isCached = false;


}
