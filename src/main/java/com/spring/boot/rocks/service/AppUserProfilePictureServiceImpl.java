package com.spring.boot.rocks.service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.spring.boot.rocks.model.AppUserProfile;
import com.spring.boot.rocks.repository.AppUserProfileRepository;

//@Slf4j
@Service
public class AppUserProfilePictureServiceImpl implements AppUserProfilePictureService {

	@Autowired
	AppUserProfileRepository appUserProfileRepository;


	@Override
	@Transactional
	public void saveUserPictureFile(Long appUserId, MultipartFile file) {

		try {
			AppUserProfile appUserProfile = appUserProfileRepository.findById(appUserId).get();
			Byte[] byteObjects = new Byte[file.getBytes().length];
			int i = 0;
			for (byte b : file.getBytes()) {
				byteObjects[i++] = b;
			}

			appUserProfile.setProfilepic(byteObjects);
			appUserProfile.setDatemodified(getDate());
			//System.out.println("Loaded image :: "+byteObjects.toString());
			appUserProfileRepository.save(appUserProfile);

		} catch (IOException e) {
			//System.out.println("Error occurred");
//			log.error("Error occurred", e);
			e.printStackTrace();
		}
	}
	
	private String getDate() {
		Date profileModifiedDate = new Date();
		String profileModifyDate = new SimpleDateFormat("yyyy-MM-dd").format(profileModifiedDate);
		return profileModifyDate;
	}
}
