package com.tensorwrench.shiro.jee.exceptions;

import javax.ejb.ApplicationException;

import org.apache.shiro.authz.AuthorizationException;

@ApplicationException
public class ApplicationAuthorizationException extends AuthorizationException {

	private static final long	serialVersionUID	= -265736087945250784L;

	public ApplicationAuthorizationException() {
		// no action
	}

	public ApplicationAuthorizationException(String message, Throwable cause) {
		super(message, cause);
	}

	public ApplicationAuthorizationException(String message) {
		super(message);
	}

	public ApplicationAuthorizationException(Throwable cause) {
		super(cause);
	}
	
}
