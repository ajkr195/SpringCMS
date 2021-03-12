package com.spring.boot.rocks.service;

import org.springframework.web.multipart.MultipartFile;

public interface AppUserProfilePictureService {

    void saveUserPictureFile(Long recipeId, MultipartFile file);
}
