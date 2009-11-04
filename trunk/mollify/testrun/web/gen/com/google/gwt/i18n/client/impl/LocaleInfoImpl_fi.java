package com.google.gwt.i18n.client.impl;

import com.google.gwt.core.client.JavaScriptObject;

public class LocaleInfoImpl_fi extends com.google.gwt.i18n.client.impl.LocaleInfoImpl {
  private JavaScriptObject nativeDisplayNames;
  
  public String[] getAvailableLocaleNames() {
    return new String[] {
      "de",
      "default",
      "en",
      "fi",
      "fr",
      "it",
      "pt",
    };
  }
  
  public String getLocaleName() {
    return "fi";
  }
  
  public native String getLocaleNativeDisplayName(String localeName) /*-{
    this.@com.google.gwt.i18n.client.impl.LocaleInfoImpl_fi::ensureNativeDisplayNames()();
    return this.@com.google.gwt.i18n.client.impl.LocaleInfoImpl_fi::nativeDisplayNames[localeName];
  }-*/;
  
  private native void ensureNativeDisplayNames() /*-{
    if (this.@com.google.gwt.i18n.client.impl.LocaleInfoImpl_fi::nativeDisplayNames != null) {
      return;
    }
    this.@com.google.gwt.i18n.client.impl.LocaleInfoImpl_fi::nativeDisplayNames = {
      "de": "Deutsch",
      "en": "English",
      "fi": "suomi",
      "fr": "français",
      "it": "italiano",
      "pt": "português"
    };
  }-*/;
}
