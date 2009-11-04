package com.google.gwt.junit.client.impl;

public class StackTraceWrapper_FieldSerializer {
  public static void deserialize(com.google.gwt.user.client.rpc.SerializationStreamReader streamReader, com.google.gwt.junit.client.impl.StackTraceWrapper instance) throws com.google.gwt.user.client.rpc.SerializationException{
    instance.className = streamReader.readString();
    instance.fileName = streamReader.readString();
    instance.lineNumber = streamReader.readInt();
    instance.methodName = streamReader.readString();
    
  }
  
  public static native com.google.gwt.junit.client.impl.StackTraceWrapper instantiate(com.google.gwt.user.client.rpc.SerializationStreamReader streamReader) throws com.google.gwt.user.client.rpc.SerializationException/*-{
    return @com.google.gwt.junit.client.impl.StackTraceWrapper::new()();
  }-*/;
  
  public static void serialize(com.google.gwt.user.client.rpc.SerializationStreamWriter streamWriter, com.google.gwt.junit.client.impl.StackTraceWrapper instance) throws com.google.gwt.user.client.rpc.SerializationException {
    streamWriter.writeString(instance.className);
    streamWriter.writeString(instance.fileName);
    streamWriter.writeInt(instance.lineNumber);
    streamWriter.writeString(instance.methodName);
    
  }
  
}
