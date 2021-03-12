package com.spring.boot.rocks.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "persistent_logins")
public class PersistentLogins implements Serializable {

	private static final long serialVersionUID = 1L;
	@Basic(optional = false)
	@Column(name = "username")
	private String username;
	@Id
	@Basic(optional = false)
	@Column(name = "series")
	private String series;
	@Basic(optional = false)
	@Column(name = "token")
	private String token;
	@Basic(optional = false)
	@Column(name = "last_used")
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastUsed;


}
