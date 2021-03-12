package com.spring.boot.rocks.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.spring.boot.rocks.model.AppUser;
import com.spring.boot.rocks.model.AppUserProfile;
import com.spring.boot.rocks.repository.AppUserJPARepository;
import com.spring.boot.rocks.repository.AppUserProfileRepository;
import com.spring.boot.rocks.service.AppUserProfileService;

import ch.qos.logback.classic.Logger;

@Controller
@PropertySource("classpath:validation.properties")
public class AppUserProfileController {
	
	@Value("${user.profile.termsconditions}")
	private String userprofiletermsconditions;
	
	Logger logger = (Logger) LoggerFactory.getLogger(AppUserProfileController.class);
	
	@Autowired
	AppUserProfileRepository appUserProfileRepository;

	@Autowired
	AppUserProfileService appUserProfileService;

	@Autowired
	AppUserJPARepository appUserRepository;

	@RequestMapping(value = { "/appUserProfile", "/appUserProfile/{id}" }, method = RequestMethod.GET)
	public String appUserProfileNew(Model model, @PathVariable(required = false, name = "id") Long id) {
		if (null != id) {
			model.addAttribute("appUserProfile", appUserProfileService.findById(id));
		} else {
		model.addAttribute("appUserProfile", new AppUserProfile());
		}
		model.addAttribute("userprofiletermsconditions", userprofiletermsconditions);
		return "profileappuser";
	}
	@RequestMapping(value = "/appUserProfile", method = RequestMethod.POST)
	public String appUserProfileRegistration(
			@ModelAttribute("appappUserProfile") @Valid AppUserProfile appappUserProfile, BindingResult bindingResult,
			HttpServletRequest request, Model model) {
		appUserProfileService.saveAppUserProfile(appappUserProfile);
		if (bindingResult.hasErrors()) {
			return "profileappuser";
		}
		model.addAttribute("userprofiletermsconditions", userprofiletermsconditions);
		return "redirect:/profile";
	}

	@RequestMapping(value = { "/profile" }, method = RequestMethod.GET)
	@GetMapping("/")
	public String profilePage(Model model) {
		
		model.addAttribute("userprofiletermsconditions", userprofiletermsconditions);
		
		AppUser currentUserId = appUserRepository.getAppuserIdByUsername(getPrincipal());
		model.addAttribute("currentUserId", appUserProfileRepository.findProfileIdByAppUser(currentUserId));
		
		
		if(null != appUserProfileRepository.findByAppUser(appUserRepository.findByUsername(getPrincipal()))) {
			String noProfileFound = "noProfileFound";
			model.addAttribute("noProfileFound", noProfileFound);
		model.addAttribute("appuserprofile", appUserProfileRepository.findByAppUser(appUserRepository.findByUsername(getPrincipal())));
		} else {
			model.addAttribute("noProfileFound", "");
		}
		
		return "profile";
	}

//	@RequestMapping(value = { "/deleteuserdocument/{docid}/{userid}" }, method = RequestMethod.GET)
//	public String deleteUserDocument(@PathVariable("docid") Long docid, @PathVariable("userid") Long userid) {
//		appUserDocumentRepository.deleteById(docid);
//		// appUserDocumentRepository.deleteByIdandAppuser(docid, userid);
//		return "redirect:/adddocument/" + userid;
//	}
	
	
	@ModelAttribute("loggedInUserName")
	public String userName() {
		return getPrincipal();
	}

	private String getPrincipal() {
		String userName = null;
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		if (principal instanceof UserDetails) {
			userName = ((UserDetails) principal).getUsername();
		} else {
			userName = principal.toString();
		}
		return userName;
	}

}
