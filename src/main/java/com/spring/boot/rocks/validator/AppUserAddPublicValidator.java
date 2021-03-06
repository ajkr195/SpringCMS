package com.spring.boot.rocks.validator;

import com.spring.boot.rocks.model.AppUser;
import com.spring.boot.rocks.service.AppUserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component
public class AppUserAddPublicValidator implements Validator {
	@Autowired
	private AppUserService userService;

	@Override
	public boolean supports(Class<?> aClass) {
		return AppUser.class.equals(aClass);
	}

	@Override
	public void validate(Object o, Errors errors) {
		AppUser user = (AppUser) o;

		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "username", "NotEmpty");
		if (user.getUsername().length() < 6 || user.getUsername().length() > 32) {
			errors.rejectValue("username", "Size.userForm.username");
		}
		if (userService.findByUsername(user.getUsername()) != null) {
			errors.rejectValue("username", "Duplicate.userForm.username");
		}

		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "useremail", "NotEmpty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "userfirstname", "NotEmpty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "userlastname", "NotEmpty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "useraddress", "NotEmpty");

		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "userpassword", "NotEmpty");
		if (user.getUserpassword().length() < 8 || user.getUserpassword().length() > 32) {
			errors.rejectValue("userpassword", "Size.userForm.userpassword");
		}

		if (!user.getPasswordConfirm().equals(user.getUserpassword())) {
			errors.rejectValue("passwordConfirm", "Diff.userForm.passwordConfirm");
		}

		
	}
}
