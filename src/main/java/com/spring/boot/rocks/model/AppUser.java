package com.spring.boot.rocks.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

//import org.springframework.data.annotation.Transient;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "app_user")

public class AppUser extends Auditable<String> implements Serializable {

	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "id")
	private Long id;
	@Basic(optional = false)
	@Column(name = "username")
	private String username;
	@Basic(optional = false)
	@Column(name = "userpassword")
	private String userpassword;
	@Transient
	private String passwordConfirm;
	@Basic(optional = false)
	@Column(name = "useremail")
	private String useremail;
	@Basic(optional = false)
	@Column(name = "userfirstname")
	private String userfirstname;
	@Basic(optional = false)
	@Column(name = "userlastname")
	private String userlastname;
	@Basic(optional = false)
	@Column(name = "useraddress")
	private String useraddress;
	@NotNull
	@Column(name = "userenabled", columnDefinition = "boolean default false")
	private boolean userenabled;
	@Column(name = "userconfirmationtoken")
	private String userconfirmationtoken;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "app_user_role", joinColumns = @JoinColumn(name = "userid"), inverseJoinColumns = @JoinColumn(name = "roleid"))

	private List<AppRole> roles;

}
