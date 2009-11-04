package com.google.gwt.junit.client.impl;

import com.google.gwt.user.client.rpc.impl.RemoteServiceProxy;
import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.impl.RequestCallbackAdapter.ResponseReader;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.impl.ClientSerializationStreamWriter;

public class JUnitHost_Proxy extends RemoteServiceProxy implements com.google.gwt.junit.client.impl.JUnitHostAsync {
  private static final String REMOTE_SERVICE_INTERFACE_NAME = "com.google.gwt.junit.client.impl.JUnitHost";
  private static final String SERIALIZATION_POLICY ="E30A631FABED1F5978A55E488E512193";
  private static final com.google.gwt.junit.client.impl.JUnitHost_TypeSerializer SERIALIZER = new com.google.gwt.junit.client.impl.JUnitHost_TypeSerializer();
  
  public JUnitHost_Proxy() {
    super(GWT.getModuleBaseURL(),
      null, 
      SERIALIZATION_POLICY, 
      SERIALIZER);
  }
  
  public void getFirstMethod(com.google.gwt.user.client.rpc.AsyncCallback callBack) {
    int requestId = getNextRequestId();
    boolean toss = isStatsAvailable() && stats(timeStat("JUnitHost_Proxy.getFirstMethod", getRequestId(), "begin"));
    ClientSerializationStreamWriter streamWriter = createStreamWriter();
    // createStreamWriter() prepared the stream
    streamWriter.writeString(REMOTE_SERVICE_INTERFACE_NAME);
    streamWriter.writeString("getFirstMethod");
    streamWriter.writeInt(0);
    String payload = streamWriter.toString();
    toss = isStatsAvailable() && stats(timeStat("JUnitHost_Proxy.getFirstMethod", getRequestId(), "requestSerialized"));
    doInvoke(ResponseReader.OBJECT, "JUnitHost_Proxy.getFirstMethod", getRequestId(), payload, callBack);
  }
  
  public void reportResultsAndGetNextMethod(com.google.gwt.junit.client.impl.JUnitHost.TestInfo testInfo, com.google.gwt.junit.client.impl.JUnitResult result, com.google.gwt.user.client.rpc.AsyncCallback callBack) {
    int requestId = getNextRequestId();
    boolean toss = isStatsAvailable() && stats(timeStat("JUnitHost_Proxy.reportResultsAndGetNextMethod", getRequestId(), "begin"));
    ClientSerializationStreamWriter streamWriter = createStreamWriter();
    // createStreamWriter() prepared the stream
    streamWriter.writeString(REMOTE_SERVICE_INTERFACE_NAME);
    try {
      streamWriter.writeString("reportResultsAndGetNextMethod");
      streamWriter.writeInt(2);
      streamWriter.writeString("com.google.gwt.junit.client.impl.JUnitHost$TestInfo");
      streamWriter.writeString("com.google.gwt.junit.client.impl.JUnitResult");
      streamWriter.writeObject(testInfo);
      streamWriter.writeObject(result);
      String payload = streamWriter.toString();
      toss = isStatsAvailable() && stats(timeStat("JUnitHost_Proxy.reportResultsAndGetNextMethod", getRequestId(), "requestSerialized"));
      doInvoke(ResponseReader.OBJECT, "JUnitHost_Proxy.reportResultsAndGetNextMethod", getRequestId(), payload, callBack);
    } catch (SerializationException ex) {
      callBack.onFailure(ex);
    }
  }
}
