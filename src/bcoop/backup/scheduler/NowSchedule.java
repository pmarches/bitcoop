/**
 * <p>Title: NowSchedule.java</p>
 * <p>Description: A special scedule that runs immediately and only once.</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * @author Philippe Marchesseault
 * @version 1.0
 */

package bcoop.backup.scheduler;

/**
 * @author pmarches
 *
 */
public class NowSchedule extends Schedule{
    private static final long serialVersionUID = 7126206538093213055L;

	public NowSchedule(){
        super("Now schedule");
    }
    
    public boolean isScheduled(long time){
        return true;
    }

    public boolean willRunAgain(){
        return false;
    }
}
