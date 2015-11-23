package mockObject;

import bcoop.util.ObjectStore;;

public class MockObjectStore extends ObjectStore {
	
	public MockObjectStore(){
		super();
		createSchema();
	}
	
	protected String getDbLocation() {
		return "."; //Returns a in-memory DB
	}
}
