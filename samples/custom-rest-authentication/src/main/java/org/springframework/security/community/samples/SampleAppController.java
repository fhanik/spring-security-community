/*
 * Copyright 2002-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package org.springframework.security.community.samples;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@Controller
public class SampleAppController {
	private static final Log logger = LogFactory.getLog(SampleAppController.class);

	@RequestMapping(value = {"/api-key-only"}, produces = "text/plain")
	@ResponseBody
	public String apiKeyOnly(HttpServletRequest request,
							 HttpServletResponse response,
							 Authentication authentication) {
		logger.info("Sample Application - Only API Key is Required");
		return "API KEY ONLY";
	}

	@RequestMapping(value = {"/dual-auth"}, produces = "text/plain")
	@ResponseBody
	@PreAuthorize("hasAuthority('USER_CREDENTIALS')")
	public String twoLayersOfAuth(HttpServletRequest request,
							 HttpServletResponse response,
							 Authentication authentication) {
		logger.info("Sample Application - Two Roles Required");
		return "DUAL AUTH";
	}
}
