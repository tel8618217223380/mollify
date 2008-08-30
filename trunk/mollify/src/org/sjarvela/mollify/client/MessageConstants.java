package org.sjarvela.mollify.client;

import com.google.gwt.i18n.client.Messages;

public interface MessageConstants extends Messages{
	String sizeOneByte();
	String sizeInBytes(int bytes);
	String sizeOneKilobyte();
	String sizeInKilobytes(double kilobytes);
	String sizeInMegabytes(double megabytes);
}
