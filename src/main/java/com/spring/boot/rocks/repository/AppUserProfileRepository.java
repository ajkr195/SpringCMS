package com.spring.boot.rocks.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.spring.boot.rocks.model.AppUser;
import com.spring.boot.rocks.model.AppUserProfile;

@Repository
public interface AppUserProfileRepository extends JpaRepository<AppUserProfile, Long> {
	@Query("select auf from AppUserProfile auf where user = ?1")
	AppUserProfile findByAppUser(AppUser appUser);
	
	@Query("select auf.id from AppUserProfile auf where user = ?1")
	Long findProfileIdByAppUser(AppUser appUser);
	
	
}
