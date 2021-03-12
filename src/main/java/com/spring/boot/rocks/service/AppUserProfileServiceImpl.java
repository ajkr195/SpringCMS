package com.spring.boot.rocks.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.spring.boot.rocks.model.AppUserProfile;
import com.spring.boot.rocks.repository.AppUserJPARepository;
import com.spring.boot.rocks.repository.AppUserProfileRepository;

import groovy.util.logging.Slf4j;

@Slf4j
@Service
public class AppUserProfileServiceImpl implements AppUserProfileService {

	@Autowired
	AppUserProfileRepository appUserProfileRepository;
	
	@Autowired
	AppUserJPARepository  appUserRepository;

	@Override
	public Set<AppUserProfile> getAppUserProfiles() {
		//System.out.println("I'm in the profile service");

		Set<AppUserProfile> userProfileSet = new HashSet<>();
		appUserProfileRepository.findAll().iterator().forEachRemaining(userProfileSet::add);
		return userProfileSet;
	}

	@Override
	public AppUserProfile findById(Long id) {

		Optional<AppUserProfile> appUserProfile = appUserProfileRepository.findById(id);

		if (!appUserProfile.isPresent()) {
			throw new RuntimeException("AppUser Not Found! Invalid AppUser's profile was tried to update..");
		}

		return appUserProfile.get();
	}


	@Override
	@Transactional
	public AppUserProfile saveAppUserProfile(AppUserProfile appUserProfile) {
		//System.out.println("Saved AppUserId : " + appUserProfile.getId());
		appUserProfile.setDatemodified((getDate()).toString());
		appUserProfile.setUser(appUserRepository.findByUsername(getPrincipal()));
		return appUserProfileRepository.save(appUserProfile);
	}

	@Override
	public void deleteById(Long idToDelete) {
		appUserProfileRepository.deleteById(idToDelete);
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
	
	private String getDate() {
		Date profileModifiedDate = new Date();
		String profileModifyDate = new SimpleDateFormat("yyyy-MM-dd").format(profileModifiedDate);
		return profileModifyDate;
	}

	@Override
	public Page<AppUserProfile> findAllPageable(Pageable pageable) {
		// TODO Auto-generated method stub
		return appUserProfileRepository.findAll(pageable);
	}
}
