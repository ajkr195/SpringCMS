package com.spring.boot.rocks.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.spring.boot.rocks.model.AppPaginationModel;
import com.spring.boot.rocks.model.AppUserProfile;
import com.spring.boot.rocks.repository.AppUserProfileRepository;
import com.spring.boot.rocks.service.AppUserProfilePictureService;
import com.spring.boot.rocks.service.AppUserProfileService;

import ch.qos.logback.classic.Logger;

@Controller
public class AppUserPictureController {

	Logger logger = (Logger) LoggerFactory.getLogger(AppUserPictureController.class);

	private final AppUserProfilePictureService appUserProfileProfilePictureService;
	private final AppUserProfileService appUserProfileProfileService;

	private static final int BUTTONS_TO_SHOW = 9;
	private static final int INITIAL_PAGE = 0;
	private static final int INITIAL_PAGE_SIZE = 10;
	private static final int[] PAGE_SIZES = { 5, 10, 20 };

	@Autowired
	AppUserProfileRepository appUserProfileProfileRepository;

	@Autowired
	AppUserProfileService appUserProfileService;

	public AppUserPictureController(AppUserProfilePictureService appUserProfileProfilePictureService,
			AppUserProfileService appUserProfileProfileService) {
		this.appUserProfileProfilePictureService = appUserProfileProfilePictureService;
		this.appUserProfileProfileService = appUserProfileProfileService;
	}

	@GetMapping("appUserProfile/{id}/image")
	public String showUploadForm(@PathVariable Long id, Model model) {
		model.addAttribute("appUserProfile", appUserProfileProfileService.findById(id));

		return "profileimageupload";
	}

	@PostMapping("appUserProfile/{id}/image")
	public String handleImagePost(@PathVariable Long id, @RequestParam("userPicturefile") MultipartFile file) {

		appUserProfileProfilePictureService.saveUserPictureFile(Long.valueOf(id), file);

		return "redirect:/profile";
	}

	@GetMapping("appUserProfile/{id}/appUserProfileimage")
	public void renderImageFromDB(@PathVariable Long id, HttpServletResponse response) throws IOException {
		AppUserProfile appUserProfile = appUserProfileProfileService.findById(id);

		if (appUserProfile.getProfilepic() != null) {
			byte[] byteArray = new byte[appUserProfile.getProfilepic().length];
			int i = 0;

			for (Byte wrappedByte : appUserProfile.getProfilepic()) {
				byteArray[i++] = wrappedByte; // auto unboxing
			}

			response.setContentType("image/jpeg");
			InputStream is = new ByteArrayInputStream(byteArray);
			IOUtils.copy(is, response.getOutputStream());
		}
	}

	@RequestMapping({ "/userProfilesList" })
	public String getIndexPage(@RequestParam("pageSize") Optional<Integer> pageSize,
			@RequestParam("page") Optional<Integer> page, Model model) {

		int evalPageSize = pageSize.orElse(INITIAL_PAGE_SIZE);
		int evalPage = page.filter(p -> p >= 1).map(p -> p - 1).orElse(INITIAL_PAGE);

		Page<AppUserProfile> allfiles = appUserProfileService.findAllPageable(PageRequest.of(evalPage, evalPageSize));
		AppPaginationModel pager = new AppPaginationModel(allfiles.getTotalPages(), allfiles.getNumber(),
				BUTTONS_TO_SHOW);

		model.addAttribute("appUserProfiles", allfiles);
		model.addAttribute("totalappUserProfiles", appUserProfileProfileRepository.count());
		model.addAttribute("selectedPageSize", evalPageSize);
		model.addAttribute("pageSizes", PAGE_SIZES);
		model.addAttribute("pager", pager);

//		model.addAttribute("appUserProfiles", appUserProfileProfileRepository.findAll());
		return "profileslist";
	}

	@RequestMapping(value = "/deleteUserProfile/{id}", method = RequestMethod.GET)
	public String caseDocumentDelete(@PathVariable(required = true, name = "id") Long id, Model model) {
		AppUserProfile appUserProfile = appUserProfileProfileService.findById(id);
		appUserProfileProfileRepository.delete(appUserProfile);
		return "redirect:/userProfilesList";
	}

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
