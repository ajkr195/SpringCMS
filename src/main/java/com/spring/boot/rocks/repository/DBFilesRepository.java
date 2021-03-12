package com.spring.boot.rocks.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.spring.boot.rocks.model.AppUser;
import com.spring.boot.rocks.model.DBFiles;

@Repository
public interface DBFilesRepository extends JpaRepository<DBFiles, Long> {

	public DBFiles findByFilename(String name);

	DBFiles findByDocumentuuid(String useruuid);

	@Query("select aud from DBFiles aud where lower(aud.appUser) = lower(:appUser)")
	public List<DBFiles> findByUserid(@Param("appUser") long appUser);

	@Query("SELECT aud FROM DBFiles aud WHERE aud.id = ?1 AND aud.appUser = ?2")
	DBFiles findByIdandAppuser(Long id, Long userid);

	@Query("SELECT aud FROM DBFiles aud WHERE aud.id = ?1 AND aud.appUser = ?2")
	DBFiles deleteByIdandAppuser(Long id, Long userid);

	Long countByAppUser(Long userid);

	@Query("SELECT df FROM DBFiles df WHERE df.filename LIKE %:filename%")
	List<DBFiles> findByFilenameLike(String filename);
	
	
	List<DBFiles> findByFilenameIgnoreCaseContaining(String filename);

	@Query("select aud from DBFiles aud where lower(aud.appUser) = lower(:appUser)")
	public Page<DBFiles> findByUserid(@Param("appUser") long appUser, Pageable pageable);
	
//	@Query("select aud from DBFiles aud where lower(aud.filename) = lower(:filename)")
	Page<DBFiles> findByFilenameIgnoreCaseContaining(@Param("filename") String filename, Pageable pageable);

	@Query("SELECT COUNT(aud) FROM DBFiles aud WHERE aud.appUser = ?1")
	Long getDocCountByUser(AppUser appUser);
	
//	@Query("SELECT COUNT(aud) FROM DBFiles aud WHERE lower(aud.filename) = lower(:filename)")
	Long countByFilenameIgnoreCaseContaining(@Param("filename") String filename);
	
}