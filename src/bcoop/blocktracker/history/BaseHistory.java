package bcoop.blocktracker.history;

import java.io.Serializable;
import java.util.Vector;
import java.util.LinkedList;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.InheritanceType;
import javax.persistence.Transient;

import org.apache.log4j.Logger;

public class BaseHistory <T> implements Serializable{
	private static final long serialVersionUID = -5049899688638220946L;

	protected LinkedList<T> revisions = new LinkedList<T>();
	int maxHistorySize;
	transient private DiscardEventHandlerIf<T> discardHandler;
	
	public BaseHistory(int maxHistory, DiscardEventHandlerIf<T> discardHandler){
		if(maxHistory <= 0){
			throw new RuntimeException("Cannot create history with 0 or less revisions.");
		}
		this.maxHistorySize = maxHistory;
		this.discardHandler = discardHandler;
	}
	
	@Transient
	public T getNewest(){
		if(revisions.isEmpty()) return null;
		return revisions.getLast();
	}
	
	public void addNewest(T latestTransaction){
		if(revisions.contains(latestTransaction)) return;
		
		revisions.addLast(latestTransaction);
		if(revisions.size() > this.maxHistorySize){
			removeOldest();
		}
	}

	public T removeOldest() {
		if(revisions.isEmpty()) return null;
		Logger.getLogger(this.getClass()).debug("Removing oldest element from history of size "+revisions.size()+" with max of "+this.maxHistorySize);
		T objToRemove = revisions.removeFirst();
		if(discardHandler != null){
			discardHandler.onBeforeBlockToRemove(objToRemove);
		}
		return objToRemove;
	}

	public long getNumberOfRevisions() {
		return revisions.size();
	}
	
	@Transient
	public Vector<T> getAllHistory(){
		return new Vector<T>(this.revisions);
	}

	@Transient
	public int getMaxNumberOfRevision() {
		return this.maxHistorySize;
	}

}
