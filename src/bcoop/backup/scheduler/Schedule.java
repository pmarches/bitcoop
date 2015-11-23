/**
 * <p>Title: Schedule.java</p>
 * <p>Description: TODO Enter description</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * @author Philippe Marchesseault
 * @version 1.0
 */

package bcoop.backup.scheduler;

import java.io.Serializable;
import java.util.Calendar;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * @author pmarches
 *
 */
public class Schedule implements Serializable{
    private static final long serialVersionUID = 7386538915130574094L;
	private final static String TIME_WILDCARD="*";
    String month=TIME_WILDCARD;
    String weekday=TIME_WILDCARD;
    String day=TIME_WILDCARD;
    String hour=TIME_WILDCARD;
    String minute=TIME_WILDCARD;

    final private String name; //Immutable
    
    public Schedule(String name){
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
    
    @SuppressWarnings("unused")
	private void setName(){
    		//The name is immutable, create new instance to change the name.
    		throw new NotImplementedException();
    }
    
    public boolean isScheduled(long time){
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);
        
        if(!matches(cal.get(Calendar.MINUTE), minute)) return false;
        if(!matches(cal.get(Calendar.HOUR_OF_DAY), hour)) return false;
        if(!matches(cal.get(Calendar.DAY_OF_MONTH), day)) return false;
        if(!matches(cal.get(Calendar.DAY_OF_WEEK), weekday)) return false;
        if(!matches(cal.get(Calendar.MONTH)+1, month)) return false;
        
        return true;
    }

    /**
     * @param cal
     * @param schedule
     * @return
     */
    private boolean matches(int nowValue, String schedule) {
        if(schedule == null) return false;
        if("*".equals(schedule)) return true;
        
        return nowValue == Integer.parseInt(schedule);
    }

    public String getDay() {
        return day;
    }
    public void setDay(String day) {
        this.day = day;
    }
    public String getHour() {
        return hour;
    }
    public void setHour(String hour) {
        this.hour = hour;
    }
    public String getMinute() {
        return minute;
    }
    public void setMinute(String minute) {
        this.minute = minute;
    }
    public String getMonth() {
        return month;
    }
    public void setMonth(String month) {
        this.month = month;
    }
    public String getWeekday() {
        return weekday;
    }
    public void setWeekday(String weekday) {
        this.weekday = weekday;
    }

    public boolean willRunAgain() {
        //FIXME Check if we can really run again later..
        return true;
    }
}
