package com.spring.boot.rocks.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.spring.boot.rocks.model.DBFiles;

public interface DBFilesService {
	
	Page<DBFiles> findAllPageable(Pageable pageable);
	
	Page<DBFiles> findByUserid(Long id, Pageable pageable);
	
	Page<DBFiles> findByFilename(String filename, Pageable pageable);
	
}
