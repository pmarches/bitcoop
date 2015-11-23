package bcoop.util;

import java.util.Hashtable;

import bcoop.util.HashMatrix;

import junit.framework.TestCase;

public class HashMatrixTest extends TestCase {
	HashMatrix<Integer, String, String> matrix1 = null;

	public void setUp(){
		matrix1 = new HashMatrix<Integer, String, String>();
		matrix1.put(1, "a", "A1");
		matrix1.put(1, "b", "B1");
		matrix1.put(2, "b", "B2");
		matrix1.put(3, "c", "C3");
	}
	
	public void testPutGet() {
		assertEquals("A1", matrix1.get(1, "a"));
		assertEquals("B1", matrix1.get(1, "b"));
		assertEquals("B2", matrix1.get(2, "b"));
		assertEquals("C3", matrix1.get(3, "c"));
		assertNull(matrix1.get(10, "c"));
	}

	public void testCountRowsOrColumn() {
		assertEquals(2, matrix1.getNumberOfRowsForColumn("b"));
		assertEquals(2, matrix1.getNumberOfColumnsForRow(1));
		assertEquals(1, matrix1.getNumberOfColumnsForRow(3));
		assertEquals(0, matrix1.getNumberOfRowsForColumn("z"));
	}
	
	public void testGetRowOrColumn(){
		Hashtable<String, String> row = matrix1.getRow(1);
		assertEquals(2, row.size());
		row = matrix1.getRow(1000);
		assertNull(row);
		
		Hashtable<Integer, String> col = matrix1.getColumn("b");
		assertEquals(2, col.size());
		col = matrix1.getColumn("asdas");
		assertNull(col);
	}

}
