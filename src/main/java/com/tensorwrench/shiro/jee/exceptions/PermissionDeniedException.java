package com.tensorwrench.shiro.jee.exceptions;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.shiro.util.StringUtils;

public class PermissionDeniedException extends ApplicationAuthorizationException {


	private static final long	serialVersionUID	= -1681601500385861751L;

	Set<String> missingPermissions=new HashSet<>();
	boolean requiresAll=true;
	
	public PermissionDeniedException() {
		super();
	}

	public PermissionDeniedException(String message, Throwable cause) {
		super(message, cause);
	}

	public PermissionDeniedException(String message) {
		super(message);
	}

	public PermissionDeniedException(Throwable cause) {
		super(cause);
	}

	public PermissionDeniedException(String message,Collection<String> missingPermissions,boolean requiresAll) {
		super(message);
		this.missingPermissions.addAll(missingPermissions);
		this.requiresAll=requiresAll;
	}

	public Set<String> getMissingPermissions() {
		return missingPermissions;
	}

	public boolean isRequiresAll() {
		return requiresAll;
	}
	
	@Override
	public String toString() {
		return super.toString() + " requires " + (requiresAll?"ALL OF ":"AT LEAST ONE OF ") + StringUtils.join(missingPermissions.iterator(), ", ");
	}
	
}
