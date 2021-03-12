package com.spring.boot.rocks.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.spring.boot.rocks.model.AppUser;

@Component
public class AppUserEditValidator implements Validator {

	@Override
	public boolean supports(Class<?> aClass) {
		return AppUser.class.equals(aClass);
	}

	@Override
	public void validate(Object o, Errors errors) {
		AppUser user = (AppUser) o;

		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "userpassword", "NotEmpty");
		if (user.getUserpassword().length() < 8 || user.getUserpassword().length() > 32) {
			errors.rejectValue("userpassword", "Size.userForm.userpassword");
		}

		if (!user.getPasswordConfirm().equals(user.getUserpassword())) {
			errors.rejectValue("passwordConfirm", "Diff.userForm.passwordConfirm");
		}

		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "useremail", "NotEmpty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "userfirstname", "NotEmpty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "userlastname", "NotEmpty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "useraddress", "NotEmpty");

		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "roles", "NotEmpty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "roles", "NoRoleSelected");
		
	}
}
