package com.tensorwrench.shiro.jee.exceptions;

import javax.ejb.ApplicationException;

import org.apache.shiro.authc.AuthenticationException;

@ApplicationException
public class ApplicationAuthenticationException extends AuthenticationException {

	private static final long	serialVersionUID	= 2094039852399614304L;

	public ApplicationAuthenticationException() {
		// no action
	}

	public ApplicationAuthenticationException(String message, Throwable cause) {
		super(message, cause);
	}

	public ApplicationAuthenticationException(String message) {
		super(message);
	}

	public ApplicationAuthenticationException(Throwable cause) {
		super(cause);
	}
	
}
