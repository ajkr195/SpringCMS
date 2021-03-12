package com.spring.boot.rocks.model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "app_user_profile")

public class AppUserProfile implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "designation")
	private String designation;

	@Column(name = "followers")
	private String followers;

	@Column(name = "friends")
	private String friends;

	@Column(name = "fans")
	private String fans;

	@Column(name = "datemodified")
	private String datemodified;

	@Column(name = "nickname")
	private String nickname;

	@Column(name = "education")
	private String education;

	@Column(name = "location")
	private String location;

	@Column(name = "notes")
	private String notes;

	@Column(name = "whitepapers")
	@Lob
	private String whitepapers;

	@Column(name = "testimonials")
	@Lob
	private String testimonials;

	@Column(name = "inventions")
	@Lob
	private String inventions;

	@Column(name = "awards")
	@Lob
	private String awards;

	@Column(name = "profilepic")
	@Lob
	private Byte[] profilepic;

	@Column(name = "skills")
	private String skills;

	@OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;


}
