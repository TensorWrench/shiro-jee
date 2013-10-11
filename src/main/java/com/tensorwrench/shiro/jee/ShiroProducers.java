package com.tensorwrench.shiro.jee;

import javax.enterprise.inject.Produces;
import javax.inject.Singleton;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.subject.Subject;

@Singleton
public class ShiroProducers {
	
  @Produces SecurityManager produceSecurityManager() {
  	return SecurityUtils.getSecurityManager();
  }
  
	@Produces
	public Subject getSubject() { 
		return SecurityUtils.getSubject();
	}
}
