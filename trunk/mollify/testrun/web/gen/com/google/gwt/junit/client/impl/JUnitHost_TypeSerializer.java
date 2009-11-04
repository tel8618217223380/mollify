package com.google.gwt.junit.client.impl;

import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.rpc.impl.Serializer;

public class JUnitHost_TypeSerializer implements Serializer {
  private static final JavaScriptObject methodMap = createMethodMap();
  private static final JavaScriptObject signatureMap = createSignatureMap();
  
  private static native java.util.ArrayList create_com_google_gwt_user_client_rpc_core_java_util_ArrayList_CustomFieldSerializer(SerializationStreamReader streamReader) throws SerializationException /*-{
    return @java.util.ArrayList::new()();
  }-*/;
  
  private static native java.util.HashMap create_com_google_gwt_user_client_rpc_core_java_util_HashMap_CustomFieldSerializer(SerializationStreamReader streamReader) throws SerializationException /*-{
    return @java.util.HashMap::new()();
  }-*/;
  
  private static native java.util.IdentityHashMap create_com_google_gwt_user_client_rpc_core_java_util_IdentityHashMap_CustomFieldSerializer(SerializationStreamReader streamReader) throws SerializationException /*-{
    return @java.util.IdentityHashMap::new()();
  }-*/;
  
  private static native java.util.LinkedList create_com_google_gwt_user_client_rpc_core_java_util_LinkedList_CustomFieldSerializer(SerializationStreamReader streamReader) throws SerializationException /*-{
    return @java.util.LinkedList::new()();
  }-*/;
  
  private static native java.util.Vector create_com_google_gwt_user_client_rpc_core_java_util_Vector_CustomFieldSerializer(SerializationStreamReader streamReader) throws SerializationException /*-{
    return @java.util.Vector::new()();
  }-*/;
  
  @SuppressWarnings("restriction")
  private static native JavaScriptObject createMethodMap() /*-{
    return {
    "com.google.gwt.benchmarks.client.impl.BenchmarkResults/3493623592":[
      @com.google.gwt.benchmarks.client.impl.BenchmarkResults_FieldSerializer::instantiate(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;),
      @com.google.gwt.benchmarks.client.impl.BenchmarkResults_FieldSerializer::deserialize(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;Lcom/google/gwt/benchmarks/client/impl/BenchmarkResults;),
      @com.google.gwt.benchmarks.client.impl.BenchmarkResults_FieldSerializer::serialize(Lcom/google/gwt/user/client/rpc/SerializationStreamWriter;Lcom/google/gwt/benchmarks/client/impl/BenchmarkResults;)
    ],
    "com.google.gwt.benchmarks.client.impl.Trial/3683467588":[
      @com.google.gwt.benchmarks.client.impl.Trial_FieldSerializer::instantiate(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;),
      @com.google.gwt.benchmarks.client.impl.Trial_FieldSerializer::deserialize(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;Lcom/google/gwt/benchmarks/client/impl/Trial;),
      @com.google.gwt.benchmarks.client.impl.Trial_FieldSerializer::serialize(Lcom/google/gwt/user/client/rpc/SerializationStreamWriter;Lcom/google/gwt/benchmarks/client/impl/Trial;)
    ],
    "[Lcom.google.gwt.benchmarks.client.impl.Trial;/1895362280":[
      @com.google.gwt.benchmarks.client.impl.Trial_Array_Rank_1_FieldSerializer::instantiate(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;),
      @com.google.gwt.benchmarks.client.impl.Trial_Array_Rank_1_FieldSerializer::deserialize(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;[Lcom/google/gwt/benchmarks/client/impl/Trial;),
      @com.google.gwt.benchmarks.client.impl.Trial_Array_Rank_1_FieldSerializer::serialize(Lcom/google/gwt/user/client/rpc/SerializationStreamWriter;[Lcom/google/gwt/benchmarks/client/impl/Trial;)
    ],
    "com.google.gwt.junit.client.TimeoutException/1599913304":[
      @com.google.gwt.junit.client.TimeoutException_FieldSerializer::instantiate(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;),
      @com.google.gwt.junit.client.TimeoutException_FieldSerializer::deserialize(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;Lcom/google/gwt/junit/client/TimeoutException;),
      @com.google.gwt.junit.client.TimeoutException_FieldSerializer::serialize(Lcom/google/gwt/user/client/rpc/SerializationStreamWriter;Lcom/google/gwt/junit/client/TimeoutException;)
    ],
    "com.google.gwt.junit.client.impl.ExceptionWrapper/3253102587":[
      @com.google.gwt.junit.client.impl.ExceptionWrapper_FieldSerializer::instantiate(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;),
      @com.google.gwt.junit.client.impl.ExceptionWrapper_FieldSerializer::deserialize(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;Lcom/google/gwt/junit/client/impl/ExceptionWrapper;),
      @com.google.gwt.junit.client.impl.ExceptionWrapper_FieldSerializer::serialize(Lcom/google/gwt/user/client/rpc/SerializationStreamWriter;Lcom/google/gwt/junit/client/impl/ExceptionWrapper;)
    ],
    "com.google.gwt.junit.client.impl.JUnitHost$TestInfo/393346509":[
      @com.google.gwt.junit.client.impl.JUnitHost_TestInfo_FieldSerializer::instantiate(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;),
      @com.google.gwt.junit.client.impl.JUnitHost_TestInfo_FieldSerializer::deserialize(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;Lcom/google/gwt/junit/client/impl/JUnitHost$TestInfo;),
      @com.google.gwt.junit.client.impl.JUnitHost_TestInfo_FieldSerializer::serialize(Lcom/google/gwt/user/client/rpc/SerializationStreamWriter;Lcom/google/gwt/junit/client/impl/JUnitHost$TestInfo;)
    ],
    "com.google.gwt.junit.client.impl.JUnitResult/2699351021":[
      @com.google.gwt.junit.client.impl.JUnitResult_FieldSerializer::instantiate(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;),
      @com.google.gwt.junit.client.impl.JUnitResult_FieldSerializer::deserialize(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;Lcom/google/gwt/junit/client/impl/JUnitResult;),
      @com.google.gwt.junit.client.impl.JUnitResult_FieldSerializer::serialize(Lcom/google/gwt/user/client/rpc/SerializationStreamWriter;Lcom/google/gwt/junit/client/impl/JUnitResult;)
    ],
    "com.google.gwt.junit.client.impl.StackTraceWrapper/3029078162":[
      @com.google.gwt.junit.client.impl.StackTraceWrapper_FieldSerializer::instantiate(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;),
      @com.google.gwt.junit.client.impl.StackTraceWrapper_FieldSerializer::deserialize(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;Lcom/google/gwt/junit/client/impl/StackTraceWrapper;),
      @com.google.gwt.junit.client.impl.StackTraceWrapper_FieldSerializer::serialize(Lcom/google/gwt/user/client/rpc/SerializationStreamWriter;Lcom/google/gwt/junit/client/impl/StackTraceWrapper;)
    ],
    "[Lcom.google.gwt.junit.client.impl.StackTraceWrapper;/2340882158":[
      @com.google.gwt.junit.client.impl.StackTraceWrapper_Array_Rank_1_FieldSerializer::instantiate(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;),
      @com.google.gwt.junit.client.impl.StackTraceWrapper_Array_Rank_1_FieldSerializer::deserialize(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;[Lcom/google/gwt/junit/client/impl/StackTraceWrapper;),
      @com.google.gwt.junit.client.impl.StackTraceWrapper_Array_Rank_1_FieldSerializer::serialize(Lcom/google/gwt/user/client/rpc/SerializationStreamWriter;[Lcom/google/gwt/junit/client/impl/StackTraceWrapper;)
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
    ],
    "java.util.ArrayList/3821976829":[
      @com.google.gwt.junit.client.impl.JUnitHost_TypeSerializer::create_com_google_gwt_user_client_rpc_core_java_util_ArrayList_CustomFieldSerializer(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;),
      @com.google.gwt.user.client.rpc.core.java.util.ArrayList_CustomFieldSerializer::deserialize(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;Ljava/util/ArrayList;),
      @com.google.gwt.user.client.rpc.core.java.util.ArrayList_CustomFieldSerializer::serialize(Lcom/google/gwt/user/client/rpc/SerializationStreamWriter;Ljava/util/ArrayList;)
    ],
    "java.util.Arrays$ArrayList/1243019747":[
      @com.google.gwt.user.client.rpc.core.java.util.Arrays.ArrayList_CustomFieldSerializer::instantiate(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;),
      @com.google.gwt.user.client.rpc.core.java.util.Arrays.ArrayList_CustomFieldSerializer::deserialize(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;Ljava/util/List;),
      @com.google.gwt.user.client.rpc.core.java.util.Arrays.ArrayList_CustomFieldSerializer::serialize(Lcom/google/gwt/user/client/rpc/SerializationStreamWriter;Ljava/util/List;)
    ],
    "java.util.HashMap/962170901":[
      @com.google.gwt.junit.client.impl.JUnitHost_TypeSerializer::create_com_google_gwt_user_client_rpc_core_java_util_HashMap_CustomFieldSerializer(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;),
      @com.google.gwt.user.client.rpc.core.java.util.HashMap_CustomFieldSerializer::deserialize(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;Ljava/util/HashMap;),
      @com.google.gwt.user.client.rpc.core.java.util.HashMap_CustomFieldSerializer::serialize(Lcom/google/gwt/user/client/rpc/SerializationStreamWriter;Ljava/util/HashMap;)
    ],
    "java.util.IdentityHashMap/3881143367":[
      @com.google.gwt.junit.client.impl.JUnitHost_TypeSerializer::create_com_google_gwt_user_client_rpc_core_java_util_IdentityHashMap_CustomFieldSerializer(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;),
      @com.google.gwt.user.client.rpc.core.java.util.IdentityHashMap_CustomFieldSerializer::deserialize(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;Ljava/util/IdentityHashMap;),
      @com.google.gwt.user.client.rpc.core.java.util.IdentityHashMap_CustomFieldSerializer::serialize(Lcom/google/gwt/user/client/rpc/SerializationStreamWriter;Ljava/util/IdentityHashMap;)
    ],
    "java.util.LinkedHashMap/1551059846":[
      @com.google.gwt.user.client.rpc.core.java.util.LinkedHashMap_CustomFieldSerializer::instantiate(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;),
      @com.google.gwt.user.client.rpc.core.java.util.LinkedHashMap_CustomFieldSerializer::deserialize(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;Ljava/util/LinkedHashMap;),
      @com.google.gwt.user.client.rpc.core.java.util.LinkedHashMap_CustomFieldSerializer::serialize(Lcom/google/gwt/user/client/rpc/SerializationStreamWriter;Ljava/util/LinkedHashMap;)
    ],
    "java.util.LinkedList/1060625595":[
      @com.google.gwt.junit.client.impl.JUnitHost_TypeSerializer::create_com_google_gwt_user_client_rpc_core_java_util_LinkedList_CustomFieldSerializer(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;),
      @com.google.gwt.user.client.rpc.core.java.util.LinkedList_CustomFieldSerializer::deserialize(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;Ljava/util/LinkedList;),
      @com.google.gwt.user.client.rpc.core.java.util.LinkedList_CustomFieldSerializer::serialize(Lcom/google/gwt/user/client/rpc/SerializationStreamWriter;Ljava/util/LinkedList;)
    ],
    "java.util.Stack/1031431137":[
      @com.google.gwt.user.client.rpc.core.java.util.Stack_FieldSerializer::instantiate(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;),
      @com.google.gwt.user.client.rpc.core.java.util.Stack_FieldSerializer::deserialize(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;Ljava/util/Stack;),
      @com.google.gwt.user.client.rpc.core.java.util.Stack_FieldSerializer::serialize(Lcom/google/gwt/user/client/rpc/SerializationStreamWriter;Ljava/util/Stack;)
    ],
    "java.util.TreeMap/1575826026":[
      @com.google.gwt.user.client.rpc.core.java.util.TreeMap_CustomFieldSerializer::instantiate(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;),
      @com.google.gwt.user.client.rpc.core.java.util.TreeMap_CustomFieldSerializer::deserialize(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;Ljava/util/TreeMap;),
      @com.google.gwt.user.client.rpc.core.java.util.TreeMap_CustomFieldSerializer::serialize(Lcom/google/gwt/user/client/rpc/SerializationStreamWriter;Ljava/util/TreeMap;)
    ],
    "java.util.Vector/3125574444":[
      @com.google.gwt.junit.client.impl.JUnitHost_TypeSerializer::create_com_google_gwt_user_client_rpc_core_java_util_Vector_CustomFieldSerializer(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;),
      @com.google.gwt.user.client.rpc.core.java.util.Vector_CustomFieldSerializer::deserialize(Lcom/google/gwt/user/client/rpc/SerializationStreamReader;Ljava/util/Vector;),
      @com.google.gwt.user.client.rpc.core.java.util.Vector_CustomFieldSerializer::serialize(Lcom/google/gwt/user/client/rpc/SerializationStreamWriter;Ljava/util/Vector;)
    ]
    };
  }-*/;
  
  private static native JavaScriptObject createSignatureMap() /*-{
    return {
    "com.google.gwt.benchmarks.client.impl.BenchmarkResults":"3493623592",
    "com.google.gwt.benchmarks.client.impl.Trial":"3683467588",
    "[Lcom.google.gwt.benchmarks.client.impl.Trial;":"1895362280",
    "com.google.gwt.junit.client.TimeoutException":"1599913304",
    "com.google.gwt.junit.client.impl.ExceptionWrapper":"3253102587",
    "com.google.gwt.junit.client.impl.JUnitHost$TestInfo":"393346509",
    "com.google.gwt.junit.client.impl.JUnitResult":"2699351021",
    "com.google.gwt.junit.client.impl.StackTraceWrapper":"3029078162",
    "[Lcom.google.gwt.junit.client.impl.StackTraceWrapper;":"2340882158",
    "com.google.gwt.user.client.rpc.IncompatibleRemoteServiceException":"3936916533",
    "java.lang.String":"2004016611",
    "java.util.ArrayList":"3821976829",
    "java.util.Arrays$ArrayList":"1243019747",
    "java.util.HashMap":"962170901",
    "java.util.IdentityHashMap":"3881143367",
    "java.util.LinkedHashMap":"1551059846",
    "java.util.LinkedList":"1060625595",
    "java.util.Stack":"1031431137",
    "java.util.TreeMap":"1575826026",
    "java.util.Vector":"3125574444"
    };
  }-*/;
  
  private static void raiseSerializationException(String msg) throws SerializationException {
    throw new SerializationException(msg);
  }
  
  public native void deserialize(SerializationStreamReader streamReader, Object instance, String typeSignature) throws SerializationException /*-{
    var methodTable = @com.google.gwt.junit.client.impl.JUnitHost_TypeSerializer::methodMap[typeSignature];
    if (!methodTable) {
      @com.google.gwt.junit.client.impl.JUnitHost_TypeSerializer::raiseSerializationException(Ljava/lang/String;)(typeSignature);
    }
    methodTable[1](streamReader, instance);
  }-*/;
  
  public native String getSerializationSignature(String typeName) /*-{
    return @com.google.gwt.junit.client.impl.JUnitHost_TypeSerializer::signatureMap[typeName];
  }-*/;
  
  public native Object instantiate(SerializationStreamReader streamReader, String typeSignature) throws SerializationException /*-{
    var methodTable = @com.google.gwt.junit.client.impl.JUnitHost_TypeSerializer::methodMap[typeSignature];
    if (!methodTable) {
      @com.google.gwt.junit.client.impl.JUnitHost_TypeSerializer::raiseSerializationException(Ljava/lang/String;)(typeSignature);
    }
    return methodTable[0](streamReader);
  }-*/;
  
  public native void serialize(SerializationStreamWriter streamWriter, Object instance, String typeSignature) throws SerializationException /*-{
    var methodTable = @com.google.gwt.junit.client.impl.JUnitHost_TypeSerializer::methodMap[typeSignature];
    if (!methodTable) {
      @com.google.gwt.junit.client.impl.JUnitHost_TypeSerializer::raiseSerializationException(Ljava/lang/String;)(typeSignature);
    }
    methodTable[2](streamWriter, instance);
  }-*/;
  
}
