package com.google.gwt.junit.client.impl;

import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.core.client.GWT;

public class GWTRunnerImpl extends com.google.gwt.junit.client.impl.GWTRunner {
  
  protected final GWTTestCase createNewTestCase(String testClass) {
    if (testClass.equals("org.sjarvela.mollify.client.session.SessionInfoTest")) {
      return GWT.create(org.sjarvela.mollify.client.session.SessionInfoTest.class);
    }
    else if (testClass.equals("org.sjarvela.mollify.client.session.user.DefaultPasswordGeneratorTest")) {
      return GWT.create(org.sjarvela.mollify.client.session.user.DefaultPasswordGeneratorTest.class);
    }
    else if (testClass.equals("org.sjarvela.mollify.client.ui.permissions.PermissionEditorModelTest")) {
      return GWT.create(org.sjarvela.mollify.client.ui.permissions.PermissionEditorModelTest.class);
    }
    else if (testClass.equals("org.sjarvela.mollify.client.util.HtmlTest")) {
      return GWT.create(org.sjarvela.mollify.client.util.HtmlTest.class);
    }
    else if (testClass.equals("org.sjarvela.mollify.client.util.JsUtilTest")) {
      return GWT.create(org.sjarvela.mollify.client.util.JsUtilTest.class);
    }
    return null;
  }
}
