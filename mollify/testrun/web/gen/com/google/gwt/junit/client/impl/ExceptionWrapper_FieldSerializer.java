package com.google.gwt.junit.client.impl;

public class ExceptionWrapper_FieldSerializer {
  public static void deserialize(com.google.gwt.user.client.rpc.SerializationStreamReader streamReader, com.google.gwt.junit.client.impl.ExceptionWrapper instance) throws com.google.gwt.user.client.rpc.SerializationException{
    instance.cause = (com.google.gwt.junit.client.impl.ExceptionWrapper) streamReader.readObject();
    instance.message = streamReader.readString();
    instance.stackTrace = (com.google.gwt.junit.client.impl.StackTraceWrapper[]) streamReader.readObject();
    instance.typeName = streamReader.readString();
    
  }
  
  public static native com.google.gwt.junit.client.impl.ExceptionWrapper instantiate(com.google.gwt.user.client.rpc.SerializationStreamReader streamReader) throws com.google.gwt.user.client.rpc.SerializationException/*-{
    return @com.google.gwt.junit.client.impl.ExceptionWrapper::new()();
  }-*/;
  
  public static void serialize(com.google.gwt.user.client.rpc.SerializationStreamWriter streamWriter, com.google.gwt.junit.client.impl.ExceptionWrapper instance) throws com.google.gwt.user.client.rpc.SerializationException {
    streamWriter.writeObject(instance.cause);
    streamWriter.writeString(instance.message);
    streamWriter.writeObject(instance.stackTrace);
    streamWriter.writeString(instance.typeName);
    
  }
  
}
