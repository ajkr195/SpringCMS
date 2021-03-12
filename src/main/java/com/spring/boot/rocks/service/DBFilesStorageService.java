package com.spring.boot.rocks.service;

import java.io.IOException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.spring.boot.rocks.exception.FileStorageException;
import com.spring.boot.rocks.exception.MyFileNotFoundException;
import com.spring.boot.rocks.model.DBFiles;
import com.spring.boot.rocks.repository.DBFilesRepository;

@Service
public class DBFilesStorageService {

	@Autowired
	DBFilesRepository dBFilesRepository;

	public DBFiles storeFile(MultipartFile file) {
		// Normalize file name
		String fileName = StringUtils.cleanPath(file.getOriginalFilename());
		try {
			// Check if the file's name contains invalid characters
			if (fileName.contains("..")) {
				throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
			}

			DBFiles dbFile = new DBFiles(UUID.randomUUID().toString(), file.getOriginalFilename(), file.getSize(), file.getContentType(), file.getBytes());

			return dBFilesRepository.save(dbFile);
		} catch (IOException ex) {
			throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
		}
	}

	public DBFiles getFile(Long fileId) {
		return dBFilesRepository.findById(fileId)
				.orElseThrow(() -> new MyFileNotFoundException("File not found with id " + fileId)); 
	}
	
	
}
