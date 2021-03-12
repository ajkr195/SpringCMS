package com.spring.boot.rocks.service;

import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.spring.boot.rocks.model.AppUserProfile;

public interface AppUserProfileService {

    Set<AppUserProfile> getAppUserProfiles();

    AppUserProfile findById(Long id);

    AppUserProfile saveAppUserProfile(AppUserProfile appUserProfile);

    void deleteById(Long id);
    
    Page<AppUserProfile> findAllPageable(Pageable pageable);
}
