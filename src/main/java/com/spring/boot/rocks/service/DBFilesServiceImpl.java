package com.spring.boot.rocks.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.spring.boot.rocks.model.DBFiles;
import com.spring.boot.rocks.repository.DBFilesRepository;

@Service
public class DBFilesServiceImpl implements DBFilesService {

	@Autowired
	DBFilesRepository dBFilesRepository;

	@Override
	public Page<DBFiles> findAllPageable(Pageable pageable) {
		// TODO Auto-generated method stub
		return dBFilesRepository.findAll(pageable);
	}

	@Override
	public Page<DBFiles> findByUserid(Long id, Pageable pageable) {
		// TODO Auto-generated method stub
		return dBFilesRepository.findByUserid(id, pageable);
	}

	@Override
	public Page<DBFiles> findByFilename(String filename, Pageable pageable) {
		// TODO Auto-generated method stub
		return dBFilesRepository.findByFilenameIgnoreCaseContaining(filename, pageable);
	}

}
