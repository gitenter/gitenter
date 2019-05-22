package com.gitenter.capsid.web;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController {
	@RequestMapping("/health_check")
	public String showHealthCheckPage() throws Exception {
		return "GitEnter";
	}
}
