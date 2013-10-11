package com.tensorwrench.shiro.jee.interceptors;


import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresGuest;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.authz.annotation.RequiresUser;
import org.apache.shiro.subject.Subject;

import com.tensorwrench.shiro.jee.annotations.Secured;
import com.tensorwrench.shiro.jee.exceptions.ApplicationAuthenticationException;
import com.tensorwrench.shiro.jee.exceptions.PermissionDeniedException;

@Interceptor @Secured
public class SecuredCallInterceptor {

	@Inject Subject subject;
	
	@AroundInvoke
	public Object check(InvocationContext ctx) throws Exception {
		checkAuthentication(ctx);
		checkGuest(ctx);
		checkPermissions(ctx);
		checkRoles(ctx);
		checkUser(ctx);
		return ctx.proceed();
	}

	//Requires the current Subject to be an application user for the annotated class/instance/method to be accessed or invoked
	private void checkUser(InvocationContext ctx) {
		RequiresUser anno=AnnotationUtils.findAnnotation(ctx.getMethod(), RequiresUser.class);
		if(anno == null || subject.isAuthenticated() || subject.isRemembered()) {
			return;
		}
		throw new ApplicationAuthenticationException("Must be logged in");
	}
	
	//Requires the currently executing Subject to have all of the specified roles
	private void checkRoles(InvocationContext ctx) {
		RequiresRoles anno=AnnotationUtils.findAnnotation(ctx.getMethod(), RequiresRoles.class);
		if(anno == null) {
			return;
		}
		boolean[] permsChecks=subject.hasRoles(Arrays.asList(anno.value()));
		
		switch(anno.logical()) {
			case AND:
				for(boolean b: permsChecks) {
					if(!b) {
						failPermission(anno.value(),permsChecks,true);
					}
				}
				break;
			case OR:
				for(boolean b: permsChecks) {
					if(b) {
						return;
					}
				}
				failPermission(anno.value(),permsChecks,false);
				break;
		}
	}

	//Requires the current executor's Subject to imply a particular permission in order to execute the annotated method
	private void checkPermissions(InvocationContext ctx) {
		RequiresPermissions anno=AnnotationUtils.findAnnotation(ctx.getMethod(), RequiresPermissions.class);
		if(anno == null) {
			return;
		}
		boolean[] permsChecks=subject.isPermitted(anno.value());
		
		switch(anno.logical()) {
			case AND:
				for(boolean b: permsChecks) {
					if(!b) {
						failPermission(anno.value(),permsChecks,true);
					}
				}
				break;
			case OR:
				for(boolean b: permsChecks) {
					if(b) {
						return;
					}
				}
				failPermission(anno.value(),permsChecks,false);
				break;
		}
	}
	protected void failPermission(String[] values, boolean[] checks,boolean allRequired) {
		Set<String> results=new HashSet<>();
		for(int i=0; i<values.length; ++i) {
			if(checks[i]) {
				results.add(values[i]);
			}
		}
		
		throw new PermissionDeniedException("User " + subject.getPrincipal() + " does not have necessary roles for this resource.",results,allRequired);
	}
	
	// Requires the current Subject to be a "guest", that is, they are not authenticated 
	// or remembered from a previous session for the annotated class/instance/method to be accessed or invoked
	private void checkGuest(InvocationContext ctx) throws ApplicationAuthenticationException{
		RequiresGuest anno=AnnotationUtils.findAnnotation(ctx.getMethod(), RequiresGuest.class);
		if(anno == null || subject.getPrincipal() == null) {
			return;
		}
		throw new ApplicationAuthenticationException("Only guests allowed");
	}
	
	// Requires the current Subject to have been authenticated during their current session for 
	// the annotated class/instance/method to be accessed or invoked
	private void checkAuthentication(InvocationContext ctx) {
		RequiresAuthentication anno=AnnotationUtils.findAnnotation(ctx.getMethod(), RequiresAuthentication.class);
		if(anno == null || subject.isAuthenticated()) {
			return;
		}
		throw new ApplicationAuthenticationException("Must have logged in during this session");
	}
}
