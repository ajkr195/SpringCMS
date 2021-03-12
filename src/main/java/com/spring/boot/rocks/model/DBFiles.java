package com.spring.boot.rocks.model;

import java.io.Serializable;



import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import com.spring.boot.rocks.model.audit.Auditable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "repository_db")
public class DBFiles extends Auditable<String> {

	@Id
	@GeneratedValue
	@Column(name = "id")
	private Long id;
	
	@Column(name = "documentuuid")
	@GenericGenerator(name = "documentuuid", strategy = "uuid4")
	private String documentuuid;

	@Column(name = "filename")
	private String filename;
	
	@Column(name = "filesize")
	private Long filesize;

	@Column(name = "mimetype")
	private String mimetype;

	@Lob
	@Column(name = "pic")
	private byte[] pic;

	@JoinColumn(name = "userid", referencedColumnName = "id")
	@ManyToOne(optional = false)
	private AppUser appUser;

	public DBFiles(String documentuuid, String filename, Long filesize, String mimetype, byte[] pic) {
		this.documentuuid = documentuuid;
		this.filename = filename;
		this.filesize = filesize;
		this.mimetype = mimetype;
		this.pic = pic;
	}

}