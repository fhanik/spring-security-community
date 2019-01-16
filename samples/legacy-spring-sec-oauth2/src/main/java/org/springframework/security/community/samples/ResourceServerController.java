package org.springframework.security.community.samples;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ResourceServerController {

	@PreAuthorize("hasRole('ADMIN')")
	@RequestMapping(value = "/hello")
	@ResponseBody
	public String hello(Authentication principal) {
		return "Hello to " + principal.getName();
	}

}
