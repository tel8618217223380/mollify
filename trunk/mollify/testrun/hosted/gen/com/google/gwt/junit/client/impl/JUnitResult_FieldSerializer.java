package com.google.gwt.junit.client.impl;

public class JUnitResult_FieldSerializer {
  private static native com.google.gwt.junit.client.impl.ExceptionWrapper getExceptionWrapper(com.google.gwt.junit.client.impl.JUnitResult instance) /*-{
    return instance.@com.google.gwt.junit.client.impl.JUnitResult::exceptionWrapper;
  }-*/;
  
  private static native void  setExceptionWrapper(com.google.gwt.junit.client.impl.JUnitResult instance, com.google.gwt.junit.client.impl.ExceptionWrapper value) /*-{
    instance.@com.google.gwt.junit.client.impl.JUnitResult::exceptionWrapper = value;
  }-*/;
  
  public static void deserialize(com.google.gwt.user.client.rpc.SerializationStreamReader streamReader, com.google.gwt.junit.client.impl.JUnitResult instance) throws com.google.gwt.user.client.rpc.SerializationException{
    setExceptionWrapper(instance, (com.google.gwt.junit.client.impl.ExceptionWrapper) streamReader.readObject());
    
  }
  
  public static native com.google.gwt.junit.client.impl.JUnitResult instantiate(com.google.gwt.user.client.rpc.SerializationStreamReader streamReader) throws com.google.gwt.user.client.rpc.SerializationException/*-{
    return @com.google.gwt.junit.client.impl.JUnitResult::new()();
  }-*/;
  
  public static void serialize(com.google.gwt.user.client.rpc.SerializationStreamWriter streamWriter, com.google.gwt.junit.client.impl.JUnitResult instance) throws com.google.gwt.user.client.rpc.SerializationException {
    streamWriter.writeObject(getExceptionWrapper(instance));
    
  }
  
}
