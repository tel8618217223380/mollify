package com.allen_sauer.gwt.log.client;

import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.rpc.impl.Serializer;

public class RemoteLoggerService_TypeSerializer implements Serializer {
  private static final JavaScriptObject methodMap = createMethodMap();
  private static final JavaScriptObject signatureMap = createSignatureMap();
  
  @SuppressWarnings("restriction")
  private static native JavaScriptObject createMethodMap() /*-{
    return {
    "com.allen_sauer.gwt.log.client.ClientStackTraceElement/1554868076":[
      @com.allen_sauer.gwt.log.client.ClientStackTraceElement_FieldSerializer::instantiate(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;),
      @com.allen_sauer.gwt.log.client.ClientStackTraceElement_FieldSerializer::deserialize(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;Lcom/allen_sauer/gwt/log/client/ClientStackTraceElement;),
      @com.allen_sauer.gwt.log.client.ClientStackTraceElement_FieldSerializer::serialize(Lcom/google/gwt/user/client/rpc/SerializationStreamWriter;Lcom/allen_sauer/gwt/log/client/ClientStackTraceElement;)
    ],
    "[Lcom.allen_sauer.gwt.log.client.ClientStackTraceElement;/3074754734":[
      @com.allen_sauer.gwt.log.client.ClientStackTraceElement_Array_Rank_1_FieldSerializer::instantiate(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;),
      @com.allen_sauer.gwt.log.client.ClientStackTraceElement_Array_Rank_1_FieldSerializer::deserialize(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;[Lcom/allen_sauer/gwt/log/client/ClientStackTraceElement;),
      @com.allen_sauer.gwt.log.client.ClientStackTraceElement_Array_Rank_1_FieldSerializer::serialize(Lcom/google/gwt/user/client/rpc/SerializationStreamWriter;[Lcom/allen_sauer/gwt/log/client/ClientStackTraceElement;)
    ],
    "com.allen_sauer.gwt.log.client.WrappedClientThrowable/2731004232":[
      @com.allen_sauer.gwt.log.client.WrappedClientThrowable_FieldSerializer::instantiate(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;),
      @com.allen_sauer.gwt.log.client.WrappedClientThrowable_FieldSerializer::deserialize(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;Lcom/allen_sauer/gwt/log/client/WrappedClientThrowable;),
      @com.allen_sauer.gwt.log.client.WrappedClientThrowable_FieldSerializer::serialize(Lcom/google/gwt/user/client/rpc/SerializationStreamWriter;Lcom/allen_sauer/gwt/log/client/WrappedClientThrowable;)
    ],
    "com.google.gwt.user.client.rpc.IncompatibleRemoteServiceException/3936916533":[
      @com.google.gwt.user.client.rpc.IncompatibleRemoteServiceException_FieldSerializer::instantiate(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;),
      @com.google.gwt.user.client.rpc.IncompatibleRemoteServiceException_FieldSerializer::deserialize(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;Lcom/google/gwt/user/client/rpc/IncompatibleRemoteServiceException;),
      @com.google.gwt.user.client.rpc.IncompatibleRemoteServiceException_FieldSerializer::serialize(Lcom/google/gwt/user/client/rpc/SerializationStreamWriter;Lcom/google/gwt/user/client/rpc/IncompatibleRemoteServiceException;)
    ],
    "java.lang.String/2004016611":[
      @com.google.gwt.user.client.rpc.core.java.lang.String_CustomFieldSerializer::instantiate(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;),
      @com.google.gwt.user.client.rpc.core.java.lang.String_CustomFieldSerializer::deserialize(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;Ljava/lang/String;),
      @com.google.gwt.user.client.rpc.core.java.lang.String_CustomFieldSerializer::serialize(Lcom/google/gwt/user/client/rpc/SerializationStreamWriter;Ljava/lang/String;)
    ]
    };
  }-*/;
  
  private static native JavaScriptObject createSignatureMap() /*-{
    return {
    "com.allen_sauer.gwt.log.client.ClientStackTraceElement":"1554868076",
    "[Lcom.allen_sauer.gwt.log.client.ClientStackTraceElement;":"3074754734",
    "com.allen_sauer.gwt.log.client.WrappedClientThrowable":"2731004232",
    "com.google.gwt.user.client.rpc.IncompatibleRemoteServiceException":"3936916533",
    "java.lang.String":"2004016611"
    };
  }-*/;
  
  private static void raiseSerializationException(String msg) throws SerializationException {
    throw new SerializationException(msg);
  }
  
  public native void deserialize(SerializationStreamReader streamReader, Object instance, String typeSignature) throws SerializationException /*-{
    var methodTable = @com.allen_sauer.gwt.log.client.RemoteLoggerService_TypeSerializer::methodMap[typeSignature];
    if (!methodTable) {
      @com.allen_sauer.gwt.log.client.RemoteLoggerService_TypeSerializer::raiseSerializationException(Ljava/lang/String;)(typeSignature);
    }
    methodTable[1](streamReader, instance);
  }-*/;
  
  public native String getSerializationSignature(String typeName) /*-{
    return @com.allen_sauer.gwt.log.client.RemoteLoggerService_TypeSerializer::signatureMap[typeName];
  }-*/;
  
  public native Object instantiate(SerializationStreamReader streamReader, String typeSignature) throws SerializationException /*-{
    var methodTable = @com.allen_sauer.gwt.log.client.RemoteLoggerService_TypeSerializer::methodMap[typeSignature];
    if (!methodTable) {
      @com.allen_sauer.gwt.log.client.RemoteLoggerService_TypeSerializer::raiseSerializationException(Ljava/lang/String;)(typeSignature);
    }
    return methodTable[0](streamReader);
  }-*/;
  
  public native void serialize(SerializationStreamWriter streamWriter, Object instance, String typeSignature) throws SerializationException /*-{
    var methodTable = @com.allen_sauer.gwt.log.client.RemoteLoggerService_TypeSerializer::methodMap[typeSignature];
    if (!methodTable) {
      @com.allen_sauer.gwt.log.client.RemoteLoggerService_TypeSerializer::raiseSerializationException(Ljava/lang/String;)(typeSignature);
    }
    methodTable[2](streamWriter, instance);
  }-*/;
  
}
