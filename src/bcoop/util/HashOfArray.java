package bcoop.util;

import java.io.Serializable;
import java.util.Vector;
import java.util.Hashtable;
import java.util.Set;

public class HashOfArray<K, V> implements Serializable {
	private static final long serialVersionUID = -2565956158265396767L;
	Hashtable<K, Vector<V>> mainHash = new Hashtable<K, Vector<V>>();
	
	public Vector<V> getAllBlockOfPeer(K key){
		return mainHash.get(key);
	}
	
	public void addValue(K key, V value){
		Vector<V> array = mainHash.get(key);
		if(array == null){
			array = new Vector<V>();
			mainHash.put(key, array);
		}
		array.add(value);
	}

	public Set<K> keySet() {
		return mainHash.keySet();
	}

	public int getNumberOfKeys() {
		return this.mainHash.size();
	}
	
	public boolean equals(Object obj1){
		HashOfArray<K, V> hash1 = (HashOfArray<K, V>) obj1;
		for(K key : this.mainHash.keySet()){
			if(!this.mainHash.get(key).equals(hash1.mainHash.get(key))){
				return false;
			}
		}
		return true;
	}
}
