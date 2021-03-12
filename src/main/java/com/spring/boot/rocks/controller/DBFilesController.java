package com.spring.boot.rocks.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import com.spring.boot.rocks.model.AppPaginationModel;
import com.spring.boot.rocks.model.AppUser;
import com.spring.boot.rocks.model.DBFiles;
import com.spring.boot.rocks.model.DBFilesInfo;
import com.spring.boot.rocks.repository.DBFilesRepository;
import com.spring.boot.rocks.repository.AppUserJPARepository;
import com.spring.boot.rocks.service.DBFilesService;

import ch.qos.logback.classic.Logger;

@Controller
public class DBFilesController {

	Logger logger = (Logger) LoggerFactory.getLogger(DBFilesController.class);

	@Autowired
	DBFilesRepository dBFilesRepository;

	@Autowired
	AppUserJPARepository appUserRepository;

	@Autowired
	DBFilesService dbFilesService;

	private static final int BUTTONS_TO_SHOW = 9;
	private static final int INITIAL_PAGE = 0;
	private static final int INITIAL_PAGE_SIZE = 10;
	private static final int[] PAGE_SIZES = { 1, 5, 10, 20, 100 };

	@RequestMapping(value = { "/addDocument/{id}" }, method = RequestMethod.GET)
	public String addDocuments(@PathVariable Long id, ModelMap model) {
		return "userdocumentsupload";
	}

	@RequestMapping(value = { "/addDocument/{id}" }, method = RequestMethod.POST)
	public String uploadDocument(@RequestParam("files") MultipartFile[] files, @PathVariable Long id, ModelMap model)
			throws IOException {
		List<String> fileNames = new ArrayList<String>();
		AppUser appUser = appUserRepository.findById(id).get();
		// System.out.println("User ID is :: " + appUser.getId());
		// System.out.println("User EMail is :: " + appUser.getUseremail());
		try {
			List<DBFiles> storedFile = new ArrayList<DBFiles>();
			for (MultipartFile file : files) {
				final DBFiles fileModel = new DBFiles(UUID.randomUUID().toString(), file.getOriginalFilename(),
						file.getSize(), file.getContentType(), file.getBytes());
				fileModel.setAppUser(appUser);
				fileNames.add(file.getOriginalFilename());
				storedFile.add(fileModel);
			}
			dBFilesRepository.saveAll(storedFile);
			model.addAttribute("successMessage", "All files uploaded successfully!");
			model.addAttribute("files", fileNames);
		} catch (Exception e) {
			model.addAttribute("failedMessage", "ERROR. File Upload Failed !!");
			model.addAttribute("files", fileNames);
			e.printStackTrace();
		}

//		return "redirect:/myDocuments";
		return "userdocumentsupload";
	}

	@RequestMapping(value = { "/addDocumentModal/{id}" }, method = RequestMethod.POST)
	public String uploadDocument2(@RequestParam("files") MultipartFile[] files, @PathVariable Long id, ModelMap model)
			throws IOException {
		List<String> fileNames = new ArrayList<String>();
		AppUser appUser = appUserRepository.findById(id).get();
		// System.out.println("User ID is :: " + appUser.getId());
		// System.out.println("User EMail is :: " + appUser.getUseremail());
		try {
			List<DBFiles> storedFile = new ArrayList<DBFiles>();
			for (MultipartFile file : files) {
				final DBFiles fileModel = new DBFiles(UUID.randomUUID().toString(), file.getOriginalFilename(),
						file.getSize(), file.getContentType(), file.getBytes());
				fileModel.setAppUser(appUser);
				fileNames.add(file.getOriginalFilename());
				storedFile.add(fileModel);
			}
			dBFilesRepository.saveAll(storedFile);
			model.addAttribute("successMessage", "All files uploaded successfully!");
			model.addAttribute("files", fileNames);
		} catch (Exception e) {
			model.addAttribute("failedMessage", "ERROR. File Upload Failed !!");
			model.addAttribute("files", fileNames);
			e.printStackTrace();
		}

		return "redirect:/dbFilesList";
	}

	@GetMapping("/listDocument")
	public String getListFiles(Model model) {
		List<DBFilesInfo> fileInfos = dBFilesRepository.findAll().stream().map(fileModel -> {
			String filename = fileModel.getFilename();
			String filetype = fileModel.getMimetype();

			String createdBy = fileModel.getCreatedBy();
			String createdDate = fileModel.getCreatedDate().toString();
			String lastModifiedBy = fileModel.getLastModifiedBy();
			String lastModifiedDate = fileModel.getLastModifiedDate().toString();

			String documentUUID = fileModel.getDocumentuuid();

			String url = MvcUriComponentsBuilder
					.fromMethodName(DBFilesController.class, "downloadFile", fileModel.getFilename().toString()).build()
					.toString();
			return new DBFilesInfo(filename, documentUUID, filetype, url, createdBy, createdDate, lastModifiedBy,
					lastModifiedDate);
		}).collect(Collectors.toList());

		model.addAttribute("files", fileInfos);
		return "userdocumentslist";
	}

	@GetMapping("/listDocumentsByName")
	public String getListFilesByName(@RequestParam("filename") String filename, Model model) {
		List<DBFiles> filesByName = dBFilesRepository.findByFilenameIgnoreCaseContaining(filename).stream()
				.collect(Collectors.toList());

		model.addAttribute("filesbyname", filesByName);
		return "repository_db_filelist_by_name";
	}

	@RequestMapping(value = { "/dbFilesListByName" }, method = RequestMethod.GET)
	public String listDbfilesByName(@RequestParam("pageSize") Optional<Integer> pageSize,
			@RequestParam("page") Optional<Integer> page,
			@RequestParam(required = false, name = "filename") String filename, Model model) {
		
		AppUser appUser = appUserRepository.findByUsername(getPrincipal());

		model.addAttribute("appUserId", appUser.getId());

		String fileName = filename;

		model.addAttribute("fileName", fileName);

		int evalPageSize = pageSize.orElse(INITIAL_PAGE_SIZE);
		int evalPage = page.filter(p -> p >= 1).map(p -> p - 1).orElse(INITIAL_PAGE);

		Page<DBFiles> allfiles = dbFilesService.findByFilename(fileName, PageRequest.of(evalPage, evalPageSize));
		AppPaginationModel pager = new AppPaginationModel(allfiles.getTotalPages(), allfiles.getNumber(),
				BUTTONS_TO_SHOW);

		model.addAttribute("allfiles", allfiles);
		model.addAttribute("totaldbfiles", dBFilesRepository.countByFilenameIgnoreCaseContaining(fileName));
		model.addAttribute("selectedPageSize", evalPageSize);
		model.addAttribute("pageSizes", PAGE_SIZES);
		model.addAttribute("pager", pager);
		return "repository_db_filelist_byname";
	}

	@RequestMapping(value = { "/dbFilesList" }, method = RequestMethod.GET)
	public String listDbfiles2(@RequestParam("pageSize") Optional<Integer> pageSize,
			@RequestParam("page") Optional<Integer> page,
			@RequestParam(required = false, name = "filename") String filename, Model model) {

		AppUser appUser = appUserRepository.findByUsername(getPrincipal());

		model.addAttribute("appUserId", appUser.getId());

		String fileName = filename;

		model.addAttribute("fileName", fileName);

//		List<DBFiles> allfiles = dbFileRepository.findAll();
		int evalPageSize = pageSize.orElse(INITIAL_PAGE_SIZE);
		int evalPage = page.filter(p -> p >= 1).map(p -> p - 1).orElse(INITIAL_PAGE);

		Page<DBFiles> allfiles = dbFilesService.findAllPageable(PageRequest.of(evalPage, evalPageSize));
		AppPaginationModel pager = new AppPaginationModel(allfiles.getTotalPages(), allfiles.getNumber(),
				BUTTONS_TO_SHOW);

		model.addAttribute("allfiles", allfiles);
		model.addAttribute("totaldbfiles", dBFilesRepository.count());
		model.addAttribute("selectedPageSize", evalPageSize);
		model.addAttribute("pageSizes", PAGE_SIZES);
		model.addAttribute("pager", pager);
		return "repository_db_filelist";
	}

	@RequestMapping(value = { "/myDocuments" }, method = RequestMethod.GET)
	public String myDocuments2(@RequestParam("pageSize") Optional<Integer> pageSize,
			@RequestParam("page") Optional<Integer> page, @RequestParam(required = false, name = "filename") String filename, Model model) {

		AppUser appUser = appUserRepository.findByUsername(getPrincipal());

		model.addAttribute("appUserId", appUser.getId());

//		List<DBFiles> allfiles = dbFileRepository.findAll();
		int evalPageSize = pageSize.orElse(INITIAL_PAGE_SIZE);
		int evalPage = page.filter(p -> p >= 1).map(p -> p - 1).orElse(INITIAL_PAGE);

		Page<DBFiles> allfiles = dbFilesService.findByUserid(appUser.getId(), PageRequest.of(evalPage, evalPageSize));
		AppPaginationModel pager = new AppPaginationModel(allfiles.getTotalPages(), allfiles.getNumber(),
				BUTTONS_TO_SHOW);

		model.addAttribute("allfiles", allfiles);
		model.addAttribute("totaldbfiles", dBFilesRepository.getDocCountByUser(appUser));
		model.addAttribute("selectedPageSize", evalPageSize);
		model.addAttribute("pageSizes", PAGE_SIZES);
		model.addAttribute("pager", pager);
		return "repository_db_mydocuments";
	}

//	@RequestMapping(value = "/deleteDocument/{id}", method = RequestMethod.GET)
//	public String notesDelete(Model model, @PathVariable(required = true, name = "id") String id) {
//		AppUserDocument userFile = dBFilesRepository.findByDocumentUUID(id);
//		dBFilesRepository.delete(userFile);
//		return "redirect:/listDocument";
//	}

	@RequestMapping(value = "/deleteDocument/{id}", method = RequestMethod.GET)
	public String userDocumentDelete(Model model, @PathVariable(required = true, name = "id") String id) {

		DBFiles userFile = dBFilesRepository.findByDocumentuuid(id);

		dBFilesRepository.delete(userFile);
		return "redirect:/dbFilesList";
	}

	@RequestMapping(value = { "/deleteuserdocument/{docid}/{userid}" }, method = RequestMethod.GET)
	public String deleteUserDocument(@PathVariable("docid") Long docid, @PathVariable("userid") Long userid) {
//		dBFilesRepository.deleteById(docid);
		dBFilesRepository.deleteByIdandAppuser(docid, userid);
		return "redirect:/adddocument/" + userid;
	}

	/*
	 * Download Files
	 */
	@GetMapping("/files/{uuid}")
	public ResponseEntity<byte[]> downloadFile(@PathVariable String uuid) {
		DBFiles file = dBFilesRepository.findByDocumentuuid(uuid);
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
				.body(file.getPic());
	}

	@GetMapping("/viewTheFile/{uuid}")
	public ResponseEntity<Resource> viewFile(@PathVariable String uuid) {
		// Load file from database
		DBFiles appUserDocument = dBFilesRepository.findByDocumentuuid(uuid);

		return ResponseEntity.ok().contentType(MediaType.parseMediaType(appUserDocument.getMimetype()))
				.header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + appUserDocument.getFilename() + "\"")
				.body(new ByteArrayResource(appUserDocument.getPic()));
	}

	@ModelAttribute("loggedInUserName")
	public String userName() {
		return getPrincipal();
	}

	@RequestMapping(value = "/deleteFile/{id}", method = RequestMethod.GET)
	public String caseDocumentDelete(@PathVariable(required = true, name = "id") Long id, Model model) {
		DBFiles dbFile = dBFilesRepository.findById(id).get();
		dBFilesRepository.deleteById(id);
		return "redirect:/repository_db_filelist";
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
