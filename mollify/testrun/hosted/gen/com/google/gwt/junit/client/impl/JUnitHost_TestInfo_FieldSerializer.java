package com.google.gwt.junit.client.impl;

public class JUnitHost_TestInfo_FieldSerializer {
  private static native java.lang.String getTestClass(com.google.gwt.junit.client.impl.JUnitHost.TestInfo instance) /*-{
    return instance.@com.google.gwt.junit.client.impl.JUnitHost$TestInfo::testClass;
  }-*/;
  
  private static native void  setTestClass(com.google.gwt.junit.client.impl.JUnitHost.TestInfo instance, java.lang.String value) /*-{
    instance.@com.google.gwt.junit.client.impl.JUnitHost$TestInfo::testClass = value;
  }-*/;
  
  private static native java.lang.String getTestMethod(com.google.gwt.junit.client.impl.JUnitHost.TestInfo instance) /*-{
    return instance.@com.google.gwt.junit.client.impl.JUnitHost$TestInfo::testMethod;
  }-*/;
  
  private static native void  setTestMethod(com.google.gwt.junit.client.impl.JUnitHost.TestInfo instance, java.lang.String value) /*-{
    instance.@com.google.gwt.junit.client.impl.JUnitHost$TestInfo::testMethod = value;
  }-*/;
  
  private static native java.lang.String getTestModule(com.google.gwt.junit.client.impl.JUnitHost.TestInfo instance) /*-{
    return instance.@com.google.gwt.junit.client.impl.JUnitHost$TestInfo::testModule;
  }-*/;
  
  private static native void  setTestModule(com.google.gwt.junit.client.impl.JUnitHost.TestInfo instance, java.lang.String value) /*-{
    instance.@com.google.gwt.junit.client.impl.JUnitHost$TestInfo::testModule = value;
  }-*/;
  
  public static void deserialize(com.google.gwt.user.client.rpc.SerializationStreamReader streamReader, com.google.gwt.junit.client.impl.JUnitHost.TestInfo instance) throws com.google.gwt.user.client.rpc.SerializationException{
    setTestClass(instance, streamReader.readString());
    setTestMethod(instance, streamReader.readString());
    setTestModule(instance, streamReader.readString());
    
  }
  
  public static native com.google.gwt.junit.client.impl.JUnitHost.TestInfo instantiate(com.google.gwt.user.client.rpc.SerializationStreamReader streamReader) throws com.google.gwt.user.client.rpc.SerializationException/*-{
    return @com.google.gwt.junit.client.impl.JUnitHost.TestInfo::new()();
  }-*/;
  
  public static void serialize(com.google.gwt.user.client.rpc.SerializationStreamWriter streamWriter, com.google.gwt.junit.client.impl.JUnitHost.TestInfo instance) throws com.google.gwt.user.client.rpc.SerializationException {
    streamWriter.writeString(getTestClass(instance));
    streamWriter.writeString(getTestMethod(instance));
    streamWriter.writeString(getTestModule(instance));
    
  }
  
}
