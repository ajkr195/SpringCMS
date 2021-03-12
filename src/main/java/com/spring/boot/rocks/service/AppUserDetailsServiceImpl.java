package com.spring.boot.rocks.service;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.spring.boot.rocks.model.AppRole;
import com.spring.boot.rocks.model.AppUser;
import com.spring.boot.rocks.repository.AppUserJPARepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AppUserDetailsServiceImpl implements UserDetailsService {
	@Autowired
	private AppUserJPARepository appUserJPARepository;

	@Override
	@Transactional(readOnly = true)
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		AppUser user = appUserJPARepository.findByUsername(username);
		if (user == null || ! user.isUserenabled()) {
			log.info(username + " - Invalid login. The user does not exist or not activated yet.");
			throw new UsernameNotFoundException(username + " - The user does not exist or not activated yet.");
		}

		Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
		for (AppRole role : user.getRoles()) {
			grantedAuthorities.add(new SimpleGrantedAuthority(role.getName()));
		}
		
		log.info(username+ " - Logged in with roles - " + grantedAuthorities);

		return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getUserpassword(),
				grantedAuthorities);
	}
	
	
}
