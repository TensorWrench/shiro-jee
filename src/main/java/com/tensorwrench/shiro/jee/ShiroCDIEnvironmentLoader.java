package com.tensorwrench.shiro.jee;


import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.ServletContext;

import org.apache.shiro.mgt.RealmSecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.web.env.DefaultWebEnvironment;
import org.apache.shiro.web.env.EnvironmentLoaderListener;
import org.apache.shiro.web.env.WebEnvironment;

import com.tensorwrench.shiro.jee.annotations.ShiroConfig;

public class ShiroCDIEnvironmentLoader extends EnvironmentLoaderListener {

	@Inject @ShiroConfig 
	List<Realm> realms=new ArrayList<>();
	
  @Override
  protected WebEnvironment createEnvironment(ServletContext pServletContext) {
      DefaultWebEnvironment environment = (DefaultWebEnvironment) super.createEnvironment(pServletContext);
      RealmSecurityManager securityManager = (RealmSecurityManager) environment.getSecurityManager();
            
      securityManager.setRealms(realms);
      environment.setSecurityManager(securityManager);

      return environment;
  }


}
