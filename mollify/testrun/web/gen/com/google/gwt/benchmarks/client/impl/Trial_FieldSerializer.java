package com.google.gwt.benchmarks.client.impl;

public class Trial_FieldSerializer {
  private static native double getRunTimeMillis(com.google.gwt.benchmarks.client.impl.Trial instance) /*-{
    return instance.@com.google.gwt.benchmarks.client.impl.Trial::runTimeMillis;
  }-*/;
  
  private static native void  setRunTimeMillis(com.google.gwt.benchmarks.client.impl.Trial instance, double value) /*-{
    instance.@com.google.gwt.benchmarks.client.impl.Trial::runTimeMillis = value;
  }-*/;
  
  private static native java.util.Map getVariables(com.google.gwt.benchmarks.client.impl.Trial instance) /*-{
    return instance.@com.google.gwt.benchmarks.client.impl.Trial::variables;
  }-*/;
  
  private static native void  setVariables(com.google.gwt.benchmarks.client.impl.Trial instance, java.util.Map value) /*-{
    instance.@com.google.gwt.benchmarks.client.impl.Trial::variables = value;
  }-*/;
  
  public static void deserialize(com.google.gwt.user.client.rpc.SerializationStreamReader streamReader, com.google.gwt.benchmarks.client.impl.Trial instance) throws com.google.gwt.user.client.rpc.SerializationException{
    setRunTimeMillis(instance, streamReader.readDouble());
    setVariables(instance, (java.util.Map) streamReader.readObject());
    
  }
  
  public static native com.google.gwt.benchmarks.client.impl.Trial instantiate(com.google.gwt.user.client.rpc.SerializationStreamReader streamReader) throws com.google.gwt.user.client.rpc.SerializationException/*-{
    return @com.google.gwt.benchmarks.client.impl.Trial::new()();
  }-*/;
  
  public static void serialize(com.google.gwt.user.client.rpc.SerializationStreamWriter streamWriter, com.google.gwt.benchmarks.client.impl.Trial instance) throws com.google.gwt.user.client.rpc.SerializationException {
    streamWriter.writeDouble(getRunTimeMillis(instance));
    streamWriter.writeObject(getVariables(instance));
    
  }
  
}
