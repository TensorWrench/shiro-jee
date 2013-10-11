package com.tensorwrench.shiro.jee.exceptionMappers;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.apache.shiro.authc.AuthenticationException;

@Provider
public class AuthenticationExceptionMapper implements ExceptionMapper<AuthenticationException> {

	@Override
	public Response toResponse(AuthenticationException throwable) {
			return Response
					.status(Status.UNAUTHORIZED)
					.header("WWW-Authenticate", "Basic realm=\"This site\"")
					.entity("Unauthorized")
					.build();
	}

}
