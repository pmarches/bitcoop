package bcoop.util;

import java.util.Hashtable;
import java.util.Set;

public class HashMatrix <R, C, V>{
	private int numberOfEntries;
	private Hashtable<R, Hashtable<C, V>> rows = new Hashtable<R, Hashtable<C, V>>();
	private Hashtable<C, Hashtable<R, V>> cols = new Hashtable<C, Hashtable<R, V>>();
	
	public void put(R rowIndex, C colIndex, V value){
		if(get(rowIndex, colIndex) == null){
			numberOfEntries++;
		}
		putRows(this.rows, rowIndex, colIndex, value);
		putCols(this.cols, rowIndex, colIndex, value);
	}

	protected void putRows(Hashtable<R, Hashtable<C, V>> hMap, R rowIndex, C colIndex, V value){
		Hashtable<C, V> lineFound = hMap.get(rowIndex);
		if(lineFound == null){
			lineFound = new Hashtable<C, V>();
			hMap.put(rowIndex, lineFound);
		}
		lineFound.put(colIndex, value);
	}

	protected void putCols(Hashtable<C, Hashtable<R, V>> hMap, R rowIndex, C colIndex, V value){
		Hashtable<R, V> lineFound = hMap.get(colIndex);
		if(lineFound == null){
			lineFound = new Hashtable<R, V>();
			hMap.put(colIndex, lineFound);
		}
		lineFound.put(rowIndex, value);
	}

	public V get(R rowIndex, C colIndex) {
		Hashtable<C, V> lineFound = rows.get(rowIndex);
		if(lineFound == null) return null;
		return lineFound.get(colIndex);
	}

	public int getNumberOfRowsForColumn(C columnIndex) {
		Hashtable<R, V> lineFound = cols.get(columnIndex);
		if(lineFound == null) return 0;
		return lineFound.size();
	}
	public int getNumberOfColumnsForRow(R rowIndex) {
		Hashtable<C, V> colFound = rows.get(rowIndex);
		if(colFound == null) return 0;
		return colFound.size();
	}

	@SuppressWarnings("unchecked")
	public Hashtable<C, V> getRow(R rowIndex) {
		Hashtable<C, V> lineFound = rows.get(rowIndex);
		if(lineFound == null) return lineFound;
		return (Hashtable<C, V>) lineFound.clone();
	}
	
	@SuppressWarnings("unchecked")
	public Hashtable<R, V> getColumn(C columnIndex) {
		Hashtable<R, V> colFound = cols.get(columnIndex);
		if(colFound == null) return colFound;
		return (Hashtable<R, V>) colFound.clone();
	}

	public Set<C> getColumnIndex() {
		return this.cols.keySet();
	}

	public Set<R> getRowIndex() {
		return this.rows.keySet();
	}

	public int getNumberOfEntries() {
		return numberOfEntries;
	}
}
