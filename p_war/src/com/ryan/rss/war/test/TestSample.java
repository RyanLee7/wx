package com.ryan.rss.war.test;

import android.test.AndroidTestCase;
import android.util.Log;

public class TestSample extends AndroidTestCase {

	protected double fValue1;
	protected double fValue2;
	protected double fRe;

	static final String LOGTAG = "TestSample";

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		fValue1 = 2.0;
		fValue2 = 3.0;
		fRe = 5.0;
	}

	public void testAdd() {
		Log.v(LOGTAG, "testAdd");
		assertTrue(LOGTAG + "1", ((fValue1 + fValue2) == fRe));
	}

}
