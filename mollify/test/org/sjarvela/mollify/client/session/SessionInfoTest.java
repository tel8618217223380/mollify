/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.session;

import org.junit.Test;
import org.sjarvela.mollify.client.service.environment.demo.DemoData;
import org.sjarvela.mollify.client.util.JsUtil;

import com.google.gwt.junit.client.GWTTestCase;

public class SessionInfoTest extends GWTTestCase {
	@Override
	public String getModuleName() {
		return "org.sjarvela.mollify.Client";
	}

	@Test
	public void testCreateDemoData() {
		DemoData data = new DemoData();
		assertEquals(
				"{\"authentication_required\":true, \"authenticated\":false, \"session_name\":\"\", \"session_id\":\"\", \"user_id\":\"\", \"username\":\"\", \"default_permission_mode\":\"a\", \"features\":{\"folder_actions\":true, \"file_upload\":true, \"file_upload_progress\":true, \"zip_download\":true, \"change_password\":true, \"description_update\":true, \"permission_update\":true, \"configuration_update\":true}, \"filesystem\":{\"max_upload_file_size\":1024, \"max_upload_total_size\":1024, \"allowed_file_upload_types\":[\"txt\",\"gif\"]}, \"roots\":[{\"id\":\"r1\", \"name\":\"Folder A\", \"parent_id\":\"\"},{\"id\":\"r2\", \"name\":\"Folder B\", \"parent_id\":\"\"}]}",
				JsUtil.asJsonString(data.getSessionInfo()));
		assertEquals(
				"{\"authentication_required\":true, \"authenticated\":true, \"session_name\":\"\", \"session_id\":\"\", \"user_id\":\"User\", \"username\":\"User\", \"default_permission_mode\":\"a\", \"features\":{\"folder_actions\":true, \"file_upload\":true, \"file_upload_progress\":true, \"zip_download\":true, \"change_password\":true, \"description_update\":true, \"permission_update\":true, \"configuration_update\":true}, \"filesystem\":{\"max_upload_file_size\":1024, \"max_upload_total_size\":1024, \"allowed_file_upload_types\":[\"txt\",\"gif\"]}, \"roots\":[{\"id\":\"r1\", \"name\":\"Folder A\", \"parent_id\":\"\"},{\"id\":\"r2\", \"name\":\"Folder B\", \"parent_id\":\"\"}]}",
				JsUtil.asJsonString(data.getSessionInfo("User")));
	}
}