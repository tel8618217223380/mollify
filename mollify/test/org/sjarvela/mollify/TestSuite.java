package org.sjarvela.mollify;
import junit.framework.Test;

import org.sjarvela.mollify.client.util.HtmlTest;

import com.google.gwt.junit.tools.GWTTestSuite;

public class TestSuite extends GWTTestSuite {
	public static Test suite() {
		TestSuite suite = new TestSuite();
		suite.addTestSuite(HtmlTest.class);
		return suite;
	}
}
