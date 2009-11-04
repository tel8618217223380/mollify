package com.allen_sauer.gwt.log.client;

import com.google.gwt.user.client.rpc.impl.RemoteServiceProxy;
import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.impl.RequestCallbackAdapter.ResponseReader;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.impl.ClientSerializationStreamWriter;

public class RemoteLoggerService_Proxy extends RemoteServiceProxy implements com.allen_sauer.gwt.log.client.RemoteLoggerServiceAsync {
  private static final String REMOTE_SERVICE_INTERFACE_NAME = "com.allen_sauer.gwt.log.client.RemoteLoggerService";
  private static final String SERIALIZATION_POLICY ="5EE298F556A3FDC1F0EC4360352651DA";
  private static final com.allen_sauer.gwt.log.client.RemoteLoggerService_TypeSerializer SERIALIZER = new com.allen_sauer.gwt.log.client.RemoteLoggerService_TypeSerializer();
  
  public RemoteLoggerService_Proxy() {
    super(GWT.getModuleBaseURL(),
      null, 
      SERIALIZATION_POLICY, 
      SERIALIZER);
  }
  
  public void debug(java.lang.String message, com.allen_sauer.gwt.log.client.WrappedClientThrowable ex, com.google.gwt.user.client.rpc.AsyncCallback callback) {
    int requestId = getNextRequestId();
    boolean toss = isStatsAvailable() && stats(timeStat("RemoteLoggerService_Proxy.debug", getRequestId(), "begin"));
    ClientSerializationStreamWriter streamWriter = createStreamWriter();
    // createStreamWriter() prepared the stream
    streamWriter.writeString(REMOTE_SERVICE_INTERFACE_NAME);
    try {
      streamWriter.writeString("debug");
      streamWriter.writeInt(2);
      streamWriter.writeString("java.lang.String");
      streamWriter.writeString("com.allen_sauer.gwt.log.client.WrappedClientThrowable");
      streamWriter.writeString(message);
      streamWriter.writeObject(ex);
      String payload = streamWriter.toString();
      toss = isStatsAvailable() && stats(timeStat("RemoteLoggerService_Proxy.debug", getRequestId(), "requestSerialized"));
      doInvoke(ResponseReader.VOID, "RemoteLoggerService_Proxy.debug", getRequestId(), payload, callback);
    } catch (SerializationException ex0) {
      callback.onFailure(ex0);
    }
  }
  
  public void diagnostic(java.lang.String message, com.allen_sauer.gwt.log.client.WrappedClientThrowable throwable, com.google.gwt.user.client.rpc.AsyncCallback callback) {
    int requestId = getNextRequestId();
    boolean toss = isStatsAvailable() && stats(timeStat("RemoteLoggerService_Proxy.diagnostic", getRequestId(), "begin"));
    ClientSerializationStreamWriter streamWriter = createStreamWriter();
    // createStreamWriter() prepared the stream
    streamWriter.writeString(REMOTE_SERVICE_INTERFACE_NAME);
    try {
      streamWriter.writeString("diagnostic");
      streamWriter.writeInt(2);
      streamWriter.writeString("java.lang.String");
      streamWriter.writeString("com.allen_sauer.gwt.log.client.WrappedClientThrowable");
      streamWriter.writeString(message);
      streamWriter.writeObject(throwable);
      String payload = streamWriter.toString();
      toss = isStatsAvailable() && stats(timeStat("RemoteLoggerService_Proxy.diagnostic", getRequestId(), "requestSerialized"));
      doInvoke(ResponseReader.VOID, "RemoteLoggerService_Proxy.diagnostic", getRequestId(), payload, callback);
    } catch (SerializationException ex) {
      callback.onFailure(ex);
    }
  }
  
  public void error(java.lang.String message, com.allen_sauer.gwt.log.client.WrappedClientThrowable ex, com.google.gwt.user.client.rpc.AsyncCallback callback) {
    int requestId = getNextRequestId();
    boolean toss = isStatsAvailable() && stats(timeStat("RemoteLoggerService_Proxy.error", getRequestId(), "begin"));
    ClientSerializationStreamWriter streamWriter = createStreamWriter();
    // createStreamWriter() prepared the stream
    streamWriter.writeString(REMOTE_SERVICE_INTERFACE_NAME);
    try {
      streamWriter.writeString("error");
      streamWriter.writeInt(2);
      streamWriter.writeString("java.lang.String");
      streamWriter.writeString("com.allen_sauer.gwt.log.client.WrappedClientThrowable");
      streamWriter.writeString(message);
      streamWriter.writeObject(ex);
      String payload = streamWriter.toString();
      toss = isStatsAvailable() && stats(timeStat("RemoteLoggerService_Proxy.error", getRequestId(), "requestSerialized"));
      doInvoke(ResponseReader.VOID, "RemoteLoggerService_Proxy.error", getRequestId(), payload, callback);
    } catch (SerializationException ex0) {
      callback.onFailure(ex0);
    }
  }
  
  public void fatal(java.lang.String message, com.allen_sauer.gwt.log.client.WrappedClientThrowable ex, com.google.gwt.user.client.rpc.AsyncCallback callback) {
    int requestId = getNextRequestId();
    boolean toss = isStatsAvailable() && stats(timeStat("RemoteLoggerService_Proxy.fatal", getRequestId(), "begin"));
    ClientSerializationStreamWriter streamWriter = createStreamWriter();
    // createStreamWriter() prepared the stream
    streamWriter.writeString(REMOTE_SERVICE_INTERFACE_NAME);
    try {
      streamWriter.writeString("fatal");
      streamWriter.writeInt(2);
      streamWriter.writeString("java.lang.String");
      streamWriter.writeString("com.allen_sauer.gwt.log.client.WrappedClientThrowable");
      streamWriter.writeString(message);
      streamWriter.writeObject(ex);
      String payload = streamWriter.toString();
      toss = isStatsAvailable() && stats(timeStat("RemoteLoggerService_Proxy.fatal", getRequestId(), "requestSerialized"));
      doInvoke(ResponseReader.VOID, "RemoteLoggerService_Proxy.fatal", getRequestId(), payload, callback);
    } catch (SerializationException ex0) {
      callback.onFailure(ex0);
    }
  }
  
  public void info(java.lang.String message, com.allen_sauer.gwt.log.client.WrappedClientThrowable ex, com.google.gwt.user.client.rpc.AsyncCallback callback) {
    int requestId = getNextRequestId();
    boolean toss = isStatsAvailable() && stats(timeStat("RemoteLoggerService_Proxy.info", getRequestId(), "begin"));
    ClientSerializationStreamWriter streamWriter = createStreamWriter();
    // createStreamWriter() prepared the stream
    streamWriter.writeString(REMOTE_SERVICE_INTERFACE_NAME);
    try {
      streamWriter.writeString("info");
      streamWriter.writeInt(2);
      streamWriter.writeString("java.lang.String");
      streamWriter.writeString("com.allen_sauer.gwt.log.client.WrappedClientThrowable");
      streamWriter.writeString(message);
      streamWriter.writeObject(ex);
      String payload = streamWriter.toString();
      toss = isStatsAvailable() && stats(timeStat("RemoteLoggerService_Proxy.info", getRequestId(), "requestSerialized"));
      doInvoke(ResponseReader.VOID, "RemoteLoggerService_Proxy.info", getRequestId(), payload, callback);
    } catch (SerializationException ex0) {
      callback.onFailure(ex0);
    }
  }
  
  public void trace(java.lang.String message, com.allen_sauer.gwt.log.client.WrappedClientThrowable ex, com.google.gwt.user.client.rpc.AsyncCallback callback) {
    int requestId = getNextRequestId();
    boolean toss = isStatsAvailable() && stats(timeStat("RemoteLoggerService_Proxy.trace", getRequestId(), "begin"));
    ClientSerializationStreamWriter streamWriter = createStreamWriter();
    // createStreamWriter() prepared the stream
    streamWriter.writeString(REMOTE_SERVICE_INTERFACE_NAME);
    try {
      streamWriter.writeString("trace");
      streamWriter.writeInt(2);
      streamWriter.writeString("java.lang.String");
      streamWriter.writeString("com.allen_sauer.gwt.log.client.WrappedClientThrowable");
      streamWriter.writeString(message);
      streamWriter.writeObject(ex);
      String payload = streamWriter.toString();
      toss = isStatsAvailable() && stats(timeStat("RemoteLoggerService_Proxy.trace", getRequestId(), "requestSerialized"));
      doInvoke(ResponseReader.VOID, "RemoteLoggerService_Proxy.trace", getRequestId(), payload, callback);
    } catch (SerializationException ex0) {
      callback.onFailure(ex0);
    }
  }
  
  public void warn(java.lang.String message, com.allen_sauer.gwt.log.client.WrappedClientThrowable ex, com.google.gwt.user.client.rpc.AsyncCallback callback) {
    int requestId = getNextRequestId();
    boolean toss = isStatsAvailable() && stats(timeStat("RemoteLoggerService_Proxy.warn", getRequestId(), "begin"));
    ClientSerializationStreamWriter streamWriter = createStreamWriter();
    // createStreamWriter() prepared the stream
    streamWriter.writeString(REMOTE_SERVICE_INTERFACE_NAME);
    try {
      streamWriter.writeString("warn");
      streamWriter.writeInt(2);
      streamWriter.writeString("java.lang.String");
      streamWriter.writeString("com.allen_sauer.gwt.log.client.WrappedClientThrowable");
      streamWriter.writeString(message);
      streamWriter.writeObject(ex);
      String payload = streamWriter.toString();
      toss = isStatsAvailable() && stats(timeStat("RemoteLoggerService_Proxy.warn", getRequestId(), "requestSerialized"));
      doInvoke(ResponseReader.VOID, "RemoteLoggerService_Proxy.warn", getRequestId(), payload, callback);
    } catch (SerializationException ex0) {
      callback.onFailure(ex0);
    }
  }
}
