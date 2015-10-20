package com.tp.safeguard.test;

import com.tp.safeguard.db.BlackDao;

import android.test.AndroidTestCase;

public class BlackDaoTest extends AndroidTestCase {

	public void testInsert() {
		BlackDao dao = new BlackDao(getContext()); 
		boolean insert = dao.insert("5554", 0);
		assertEquals(true, insert);
	}
	
	public void InsertMany() {
		BlackDao dao = new BlackDao(getContext()); 
		for (int i = 0; i < 100; i++) {
			dao.insert("5554"+i, 2);
		}
	}

	public void testUpdate() {
		BlackDao dao = new BlackDao(getContext()); 
		boolean update = dao.update("5554", 1);
		assertEquals(true, update);
	}

	public void testDelete() {
		BlackDao dao = new BlackDao(getContext()); 
		boolean delete = dao.delete("5554");
		assertEquals(true, delete);
	}

	public void testFindType() {
		BlackDao dao = new BlackDao(getContext()); 
		int type = dao.findType("5554");
		assertEquals(1, type);
	}
}
