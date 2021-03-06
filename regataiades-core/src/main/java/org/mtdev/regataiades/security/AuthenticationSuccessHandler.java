package org.mtdev.regataiades.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mtdev.regataiades.tools.Toolbox;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

public class AuthenticationSuccessHandler extends
		SimpleUrlAuthenticationSuccessHandler {

	@Override
	public void onAuthenticationSuccess(HttpServletRequest pRequest,
			HttpServletResponse pResponse, Authentication pAuthentication)
			throws IOException, ServletException {
		User lUserDetails = (User) (pAuthentication.getPrincipal());
		pResponse = Toolbox.enrichResponse(pResponse);
		pResponse.getWriter().println(
				"{\"status\": \"success\", \"username\" : \""
						+ lUserDetails.getUsername() + "\"}");
	}
}