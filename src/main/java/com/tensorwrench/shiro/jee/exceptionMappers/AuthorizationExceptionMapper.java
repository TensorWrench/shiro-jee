package com.tensorwrench.shiro.jee.exceptionMappers;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.subject.Subject;

@Provider
public class AuthorizationExceptionMapper implements ExceptionMapper<AuthorizationException> {

	
	@Inject Subject subject;
	
	@Override
	public Response toResponse(AuthorizationException throwable) {
			return Response
					.status(Status.FORBIDDEN)
					.entity("You are not permitted to access this resource.")
					.build();
	}

}
