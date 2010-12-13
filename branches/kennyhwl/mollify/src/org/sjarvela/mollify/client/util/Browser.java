package org.sjarvela.mollify.client.util;

public class Browser {
	public static final native boolean isIE() /*-{
		return /MSIE/i.test(navigator.userAgent);
	}-*/;
}
