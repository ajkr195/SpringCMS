package com.spring.boot.rocks.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.spring.boot.rocks.model.AppPaginationModel;
import com.spring.boot.rocks.model.AppRole;
import com.spring.boot.rocks.model.AppUser;
import com.spring.boot.rocks.model.export.GenerateCSVReport;
import com.spring.boot.rocks.model.export.GenerateExcelReport;
import com.spring.boot.rocks.model.export.GeneratePdfReport;
import com.spring.boot.rocks.repository.AppRoleJPARepository;
import com.spring.boot.rocks.repository.AppUserJPARepository;
import com.spring.boot.rocks.service.AppUserService;
import com.spring.boot.rocks.service.EmailService;
import com.spring.boot.rocks.validator.AppUserAddPublicValidator;
import com.spring.boot.rocks.validator.AppUserAddValidator;
import com.spring.boot.rocks.validator.AppUserEditValidator;

import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.HtmlExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleHtmlExporterOutput;

@Slf4j
@Controller
//@RequestMapping("/")
@SessionAttributes({ "roles", "programareas" })
@PropertySource("classpath:validation.properties")
public class AppUserController {
	@Autowired
	private AppUserService appUserService;

	@Autowired
	private AppRoleJPARepository appRoleJPARepository;

	@Autowired
	private AppUserJPARepository appUserJPARepository;

	@Autowired
	private AppUserAddValidator userAddValidator;

	@Autowired
	private AppUserEditValidator userEditValidator;

	@Autowired
	AppUserAddPublicValidator appUserAddPublicValidator;

	@Autowired
	EmailService emailService;

	@Value("${server.port}")
	private int serverPort;
	
	
	@Value("${user.newregistration.notice}")
	private String usernewregistrationnotice;
	

	@RequestMapping(value = { "/userlist" }, method = RequestMethod.GET)
	public String showuserList(@RequestParam("pageSize") Optional<Integer> pageSize,
			@RequestParam("page") Optional<Integer> page, Model model) {
		model.addAttribute("metaTitle", "Manage Users");

		int BUTTONS_TO_SHOW = 9;
		int INITIAL_PAGE = 0;
		int INITIAL_PAGE_SIZE = 10;
		int[] PAGE_SIZES = { 10, 15, 20, 25, 50, 100 };

		int evalPageSize = pageSize.orElse(INITIAL_PAGE_SIZE);
		int evalPage = page.filter(p -> p >= 1).map(p -> p - 1).orElse(INITIAL_PAGE);

		Page<AppUser> users = appUserJPARepository
				.findAll(PageRequest.of(evalPage, evalPageSize, Sort.by(Order.asc("id"))));
		AppPaginationModel pager = new AppPaginationModel(users.getTotalPages(), users.getNumber(), BUTTONS_TO_SHOW);

		model.addAttribute("users", users);
		model.addAttribute("totalusers", appUserJPARepository.count());
		model.addAttribute("selectedPageSize", evalPageSize);
		model.addAttribute("pageSizes", PAGE_SIZES);
		model.addAttribute("pager", pager);
		return "userlist";
	}

	@RequestMapping(value = { "/userEdit", "/userEdit/{id}" }, method = RequestMethod.GET)
	public String userRegistration(Model model, @PathVariable(required = false, name = "id") Long id) {
		model.addAttribute("usernewregistrationnotice", usernewregistrationnotice);
		if (null != id) {
			String editinguser = "editinguser";
			model.addAttribute("editinguser", editinguser);
			model.addAttribute("appuser", appUserJPARepository.findById(id));
		} else {
//			System.out.println("Creating new user....");
			String creatinguser = "creatinguser";
			model.addAttribute("creatinguser", creatinguser);
			model.addAttribute("appuser", new AppUser());
		}
		return "registration";
	}

	@RequestMapping(value = "/userEdit", method = RequestMethod.POST)
	public String userRegistration(@ModelAttribute("appuser") @Valid AppUser appuser, BindingResult bindingResult,
			HttpServletRequest request, Model model) {
		String appUrl = request.getScheme() + "://" + request.getServerName();

		String somemsg = getPrincipal();
		model.addAttribute("somemsg", somemsg);
		model.addAttribute("usernewregistrationnotice", usernewregistrationnotice);

		System.out.println("User ID is :: " + appuser.getId());

		if (null != appuser.getId()) {
//			System.out.println("Updating existng user....NOW ");
			userEditValidator.validate(appuser, bindingResult);
			model.addAttribute("appuser", appuser);
			String editinguser = "editinguser";
			model.addAttribute("editinguser", editinguser);

		} else {
//			System.out.println("Creating new user....NOW ");
			userAddValidator.validate(appuser, bindingResult);
			model.addAttribute("appuser", appuser);
			String creatinguser = "creatinguser";
			model.addAttribute("creatinguser", creatinguser);
		}

		if (bindingResult.hasErrors()) {
			return "registration";
		}

		if (null != appuser.getId()) {
//			System.out.println("Update in progress....");
			appUserService.updateUser(appuser);
		} else {
//			System.out.println("Create New in progress....");
			appUserService.save(appuser);
//			sendRegistrationConfirmationEmail(appuser, appUrl);
		}

		return "redirect:/userlist";
	}

	@RequestMapping(value = { "/userRegister" }, method = RequestMethod.GET)
	public String userPublicRegistration(Model model) {
//			System.out.println("Creating new user....");
		String creatinguser = "creatinguser";
		model.addAttribute("creatinguser", creatinguser);
		model.addAttribute("usernewregistrationnotice", usernewregistrationnotice);
		model.addAttribute("appuser", new AppUser());
		return "registrationpublic";
	}

	@RequestMapping(value = "/userRegister", method = RequestMethod.POST)
	public String userPublicRegistration(@ModelAttribute("appuser") @Valid AppUser appuser, BindingResult bindingResult,
			HttpServletRequest request, Model model) {
		appUserAddPublicValidator.validate(appuser, bindingResult);
		model.addAttribute("appuser", appuser);
		model.addAttribute("creatinguser", "creatinguser");
		model.addAttribute("usernewregistrationnotice", usernewregistrationnotice);

		if (bindingResult.hasErrors()) {
			return "registrationpublic";
		}

		appUserService.save(appuser);

		String appUrl = request.getScheme() + "://" + request.getServerName();

		sendRegistrationConfirmationEmail(appuser, appUrl);
		model.addAttribute("success", true);
		model.addAttribute("messageFromController", "Registration Successful !");
		return "registrationpublicsuccess";
	}

	@GetMapping("/verify")
	public String verifyUser(@Param("token") String token, Model model) {
		if (appUserService.verify(token)) {
			model.addAttribute("success", true);
			model.addAttribute("messageFromController", "Email verification Successful !");
			return "registrationpublicsuccess";
		} else {
			model.addAttribute("success", false);
			model.addAttribute("messageFromController", "Email verification Failed. OR it has been verified already. Try to Login. !!");
			return "registrationpublicsuccess";
		}
	}

	@ModelAttribute("roles")
	public List<AppRole> initializeRoles() {
		return (List<AppRole>) appRoleJPARepository.findAll();
	}

	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String login(Model model, String error, String logout) {
		if (error != null)
			model.addAttribute("error", "Your username and password is invalid.");

		if (logout != null)
			model.addAttribute("message", "You have been logged out successfully.");

		return "login";
	}

	@RequestMapping(value = "/alluserreportPDF", method = RequestMethod.GET, produces = MediaType.APPLICATION_PDF_VALUE)
	public ResponseEntity<InputStreamResource> allusersReport() throws IOException {

		List<AppUser> users = (List<AppUser>) appUserService.findAllUsers();

		ByteArrayInputStream bis = GeneratePdfReport.userReport(users);

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Disposition", "inline; filename=UsersReport.pdf");

		return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF)
				.body(new InputStreamResource(bis));
	}

//	@RequestMapping(value = { "export-user-pdf-{username}" }, method = RequestMethod.GET)
//	public ResponseEntity<InputStreamResource> exportUser(@PathVariable String username, Model model) {
//		AppUser user = appUserService.findByUsername(username);
//		model.addAttribute("userForm", user);
//		ByteArrayInputStream bis = GeneratePdfReport.oneuserReport(user);
//
//		HttpHeaders headers = new HttpHeaders();
//		headers.add("Content-Disposition", "inline; filename=" + username + ".pdf");
//
//		return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF)
//				.body(new InputStreamResource(bis));
//	}

	@RequestMapping(value = "/alluserreportCSV", method = RequestMethod.GET)
	public void csvUsers(HttpServletResponse response) throws IOException {
		List<AppUser> users = (List<AppUser>) appUserService.findAllUsers();
		GenerateCSVReport.writeUsers(response.getWriter(), users);
		response.setHeader("Content-Disposition", "attachment; filename=AllUsersCSVReport.csv");
	}

//	@RequestMapping(value = "/export-user-csv-{username}", method = RequestMethod.GET)
//	public void usercsvReport(@PathVariable String username, HttpServletResponse response) throws IOException {
////    	 HttpHeaders headers = new HttpHeaders();
////	        headers.add("Content-Disposition", "inline; filename=" +username+".csv");
//		response.setHeader("Content-Disposition", "attachment; filename=" + username + "CSVReport.csv");
//		AppUser user = appUserService.findByUsername(username);
//		GenerateCSVReport.writeUser(response.getWriter(), user);
//	}

//	@RequestMapping(value = "/export-user-xml-{username}", method = RequestMethod.GET)
//	public @ResponseBody AppUser getUser(@PathVariable String username) {
//		AppUser user = appUserService.findByUsername(username); // or set your own fields
//		// user.setId(userid);
//		// user.setUsername(username);
//		// and so on....
//
//		return user;
//	}

	@GetMapping(value = "/alluserreportExcel")
	public ResponseEntity<InputStreamResource> excelCustomersReport() throws IOException {
		List<AppUser> users = (List<AppUser>) appUserService.findAllUsers();
		ByteArrayInputStream in = GenerateExcelReport.usersToExcel(users);
		// return IO ByteArray(in);
		HttpHeaders headers = new HttpHeaders();
		// set filename in header
		headers.add("Content-Disposition", "attachment; filename=users.xlsx");
		return ResponseEntity.ok().headers(headers).body(new InputStreamResource(in));
	}

	@RequestMapping("/alluserreportJSON")
	public @ResponseBody String getusersJSON() {
		ObjectMapper objectMapper = new ObjectMapper();
		// Set pretty printing of json
		objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
		List<AppUser> userlist = null;
		@SuppressWarnings("unused")
		String exception = null;
		String arrayToJson = null;
		try {
			userlist = appUserService.findAllUsers();
			arrayToJson = objectMapper.writeValueAsString(userlist);
		} catch (Exception ex) {
			ex.printStackTrace();
			exception = ex.getMessage();
		}
		return arrayToJson;
	}

//	@RequestMapping("/export-user-json-{username}")
//	public @ResponseBody String getuserJSON(@PathVariable String username, HttpServletResponse response) {
//
//		ObjectMapper objectMapper = new ObjectMapper();
//		// Set pretty printing of json
//		objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
//		@SuppressWarnings("unused")
//		String exception = null;
//		String arrayToJson = null;
//		try {
//			AppUser user = appUserService.findByUsername(username);
//			arrayToJson = objectMapper.writeValueAsString(user);
//		} catch (Exception ex) {
//			ex.printStackTrace();
//			exception = ex.getMessage();
//		}
//		return arrayToJson;
//	}

	@RequestMapping(value = "jasper-HTMLEXPORT-report", method = RequestMethod.GET)
	public void report(HttpServletResponse response) throws Exception {
		response.setContentType("text/html");
		JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(appUserService.jasperhtmlreport());
		InputStream inputStream = this.getClass().getResourceAsStream("/reports/jasperreport.jrxml");
		JasperReport jasperReport = JasperCompileManager.compileReport(inputStream);
		JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, null, dataSource);
		HtmlExporter exporter = new HtmlExporter(DefaultJasperReportsContext.getInstance());
		exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
		exporter.setExporterOutput(new SimpleHtmlExporterOutput(response.getWriter()));
		exporter.exportReport();
	}

	public void sendRegistrationConfirmationEmail(AppUser appUser, String appURL) {
		SimpleMailMessage registrationEmail = new SimpleMailMessage();
		registrationEmail.setTo(appUser.getUseremail());
		registrationEmail.setSubject("Registration Confirmation");
		registrationEmail.setText("To confirm your e-mail address, please click the link below:\n" + appURL + ":"
				+ serverPort + "/verify?token=" + appUser.getUserconfirmationtoken());
//				appUser.getUserconfirmationtoken());
		registrationEmail.setFrom("noreply@domain.com");
		log.info("Confirmation URL for " + appUser.getUsername() + " is " + appURL + ":"+serverPort+"/verify?token="
				+ appUser.getUserconfirmationtoken());
		try {
			emailService.sendEmail(registrationEmail);
		} catch (Exception ex) {
			log.info("Email service is unavailable. Try after some time.");
		}
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
