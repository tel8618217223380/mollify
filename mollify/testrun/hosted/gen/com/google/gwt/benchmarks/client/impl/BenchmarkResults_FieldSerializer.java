package com.google.gwt.benchmarks.client.impl;

public class BenchmarkResults_FieldSerializer {
  private static native java.lang.String getSourceRef(com.google.gwt.benchmarks.client.impl.BenchmarkResults instance) /*-{
    return instance.@com.google.gwt.benchmarks.client.impl.BenchmarkResults::sourceRef;
  }-*/;
  
  private static native void  setSourceRef(com.google.gwt.benchmarks.client.impl.BenchmarkResults instance, java.lang.String value) /*-{
    instance.@com.google.gwt.benchmarks.client.impl.BenchmarkResults::sourceRef = value;
  }-*/;
  
  private static native java.util.List getTrials(com.google.gwt.benchmarks.client.impl.BenchmarkResults instance) /*-{
    return instance.@com.google.gwt.benchmarks.client.impl.BenchmarkResults::trials;
  }-*/;
  
  private static native void  setTrials(com.google.gwt.benchmarks.client.impl.BenchmarkResults instance, java.util.List value) /*-{
    instance.@com.google.gwt.benchmarks.client.impl.BenchmarkResults::trials = value;
  }-*/;
  
  public static void deserialize(com.google.gwt.user.client.rpc.SerializationStreamReader streamReader, com.google.gwt.benchmarks.client.impl.BenchmarkResults instance) throws com.google.gwt.user.client.rpc.SerializationException{
    setSourceRef(instance, streamReader.readString());
    setTrials(instance, (java.util.List) streamReader.readObject());
    
    com.google.gwt.junit.client.impl.JUnitResult_FieldSerializer.deserialize(streamReader, instance);
  }
  
  public static native com.google.gwt.benchmarks.client.impl.BenchmarkResults instantiate(com.google.gwt.user.client.rpc.SerializationStreamReader streamReader) throws com.google.gwt.user.client.rpc.SerializationException/*-{
    return @com.google.gwt.benchmarks.client.impl.BenchmarkResults::new()();
  }-*/;
  
  public static void serialize(com.google.gwt.user.client.rpc.SerializationStreamWriter streamWriter, com.google.gwt.benchmarks.client.impl.BenchmarkResults instance) throws com.google.gwt.user.client.rpc.SerializationException {
    streamWriter.writeString(getSourceRef(instance));
    streamWriter.writeObject(getTrials(instance));
    
    com.google.gwt.junit.client.impl.JUnitResult_FieldSerializer.serialize(streamWriter, instance);
  }
  
}
