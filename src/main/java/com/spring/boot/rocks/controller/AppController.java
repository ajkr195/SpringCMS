package com.spring.boot.rocks.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
//@RequestMapping("/")
@PropertySource("classpath:validation.properties")
public class AppController {

	@Value("${application.name}")
	private String appName;

	@RequestMapping(value = { "/", "/home" }, method = RequestMethod.GET)
	public String landingPage(Model model) {
		model.addAttribute("metaTitle", "Home");
		model.addAttribute("appUserName", System.getProperty("user.name"));
		model.addAttribute("appName", appName);
		return "home";
	}

	@RequestMapping(value = { "/dashboard" }, method = RequestMethod.GET)
	public String dashboardPage(Model model) {
		model.addAttribute("metaTitle", "Dashboard");
		return "dashboard";
	}

	@RequestMapping(value = { "/orgChart" }, method = RequestMethod.GET)
	public String orgChartPage(Model model) {
		model.addAttribute("metaTitle", "Org-Chart");
		return "orgchart";
	}

	@RequestMapping(value = { "/blank" }, method = RequestMethod.GET)
	public String blankPage(Model model) {
		model.addAttribute("metaTitle", "Blank");
		return "blank";
	}

	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	public String logoutPage(HttpServletRequest request, HttpServletResponse response) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null) {
			new SecurityContextLogoutHandler().logout(request, response, auth);

			SecurityContextHolder.getContext().setAuthentication(null);
		}
		return "redirect:/home";
	}

}
