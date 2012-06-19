package controllers.forms;

import static play.data.validation.Constraints.*;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;

public class LoginForm {

	@Required
	@MinLength(1)
	private String userName;
	@Required
	@MinLength(6)
	private String password;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String _userName) {
		userName = _userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String _password) {
		password = _password;
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}

}
