package com.spring.boot.rocks.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.xml.bind.annotation.XmlElement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.spring.boot.rocks.model.AppUser;
import com.spring.boot.rocks.repository.AppUserJPARepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(timeout = 5)
public class AppUserServiceImpl implements AppUserService {
	@Autowired
	private AppUserJPARepository appUserJPARepository;
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@Override
	public void save(AppUser user) {
		user.setUsername(user.getUsername());
		user.setUserpassword(bCryptPasswordEncoder.encode(user.getUserpassword()));
		user.setRoles(user.getRoles());

		user.setUseremail(user.getUseremail());
		user.setUserfirstname(user.getUserfirstname());
		user.setUserlastname(user.getUserlastname());
		user.setUseraddress(user.getUseraddress());
		user.setUserconfirmationtoken(UUID.randomUUID().toString());
		appUserJPARepository.save(user);
	}

	@Override
	public AppUser findByUsername(String username) {
		return appUserJPARepository.findByUsername(username);
	}

	@Override
	public AppUser findByUserId(long userid) {
		AppUser obj = appUserJPARepository.findById(userid).get();
		return obj;
	}

	@Override
	@XmlElement(name = "employee")
	public List<AppUser> findAllUsers() {
		List<AppUser> list = new ArrayList<>();
		appUserJPARepository.findAll().forEach(e -> list.add(e));
		return list;
	}

	@Override
	public void updateUser(AppUser user) {
		AppUser entity = appUserJPARepository.findById(user.getId()).orElse(null);
		if (entity != null) {
			entity.setUsername(user.getUsername());
			entity.setUserpassword(bCryptPasswordEncoder.encode(user.getUserpassword()));
			entity.setPasswordConfirm(bCryptPasswordEncoder.encode(entity.getUserpassword()));
			entity.setUseremail(user.getUseremail());
			entity.setUserfirstname(user.getUserfirstname());
			entity.setUserlastname(user.getUserlastname());
			entity.setUseraddress(user.getUseraddress());
			entity.setRoles(user.getRoles());
		}
		appUserJPARepository.save(entity);
	}

	@Override
	public void deleteUserByUsername(String username) {
		appUserJPARepository.delete(findByUsername(username));
	}

	@Override
	public List<Map<String, Object>> jasperhtmlreport() {
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		for (AppUser user : appUserJPARepository.findAll()) {
			Map<String, Object> item = new HashMap<String, Object>();
			item.put("id", user.getId());
			item.put("username", user.getUsername());
			item.put("useremail", user.getUseremail());
			item.put("userfirstname", user.getUserfirstname());
			item.put("userlastname", user.getUserlastname());
			item.put("useraddress", user.getUseraddress());
			result.add(item);
		}
		return result;
	}

	@Override
	public boolean verify(String userconfirmationtoken) {
		AppUser user = appUserJPARepository.findByUserconfirmationtoken(userconfirmationtoken);
	     
	    if (user == null || user.isUserenabled()) {
	        return false;
	    } else {
	        user.setUserconfirmationtoken(null);
	        user.setUserenabled(true);
	        appUserJPARepository.save(user);
	         log.info(user.getUsername() + " - Activated successfully.!");
	        return true;
	    }
	}


}
