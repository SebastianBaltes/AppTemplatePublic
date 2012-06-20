package controllers.forms;

import static play.data.validation.Constraints.*;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;


public class PasswordChangeForm {
	@Required
	@Email
	private String email;
	@Required
	@MinLength(4)
	private String password1;
	@Required
	@MinLength(4)
	private String password2;
	
	public PasswordChangeForm() {
	}
	
	public PasswordChangeForm(String _email) {
		email = _email;
	}

	public boolean isPasswordMatching() {
		if (password1 == null || password2 == null) return false;
		return password1.equals(password2); 
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String _email) {
		email = _email;
	}
	
	public String getPassword1() {
		return password1;
	}
	
	public void setPassword1(String _password1) {
		password1 = _password1;
	}
	
	public String getPassword2() {
		return password2;
	}
	
	public void setPassword2(String _password2) {
		password2 = _password2;
	} 
	
	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}
	
}
