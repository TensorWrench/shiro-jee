package com.tensorwrench.shiro.jee;

import javax.inject.Inject;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.testng.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.descriptor.api.Descriptors;
import org.jboss.shrinkwrap.descriptor.api.beans10.BeansDescriptor;
import org.jboss.shrinkwrap.descriptor.api.ejbjar31.EjbJarDescriptor;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.tensorwrench.shiro.jee.annotations.Secured;
import com.tensorwrench.shiro.jee.exceptions.ApplicationAuthenticationException;
import com.tensorwrench.shiro.jee.exceptions.ApplicationAuthorizationException;
import com.tensorwrench.shiro.jee.exceptions.PermissionDeniedException;
import com.tensorwrench.shiro.jee.helpers.SecuredClass;
import com.tensorwrench.shiro.jee.helpers.ShiroDriver;
import com.tensorwrench.shiro.jee.interceptors.SecuredCallInterceptor;

import static org.testng.Assert.*;


public class SecuredInterceptorTest extends Arquillian {
	@Deployment
	public static Archive<WebArchive> makeDeployment() {
		BeansDescriptor beans=Descriptors.create(BeansDescriptor.class)
				.getOrCreateInterceptors().clazz(SecuredCallInterceptor.class.getCanonicalName()).up();
		
		// Tomee doesn't seem to like the @ApplicationException annotation as a way of preventing
		// the interceptor from throwing a wrapped exception.  ejb-jar.xml seems to work, though
		
		EjbJarDescriptor ejbJar=Descriptors.create(EjbJarDescriptor.class)
				.getOrCreateAssemblyDescriptor()
					.createApplicationException()
						.exceptionClass(ApplicationAuthenticationException.class.getCanonicalName())
					.up()
					.createApplicationException()
						.exceptionClass(ApplicationAuthorizationException.class.getCanonicalName())
					.up()
				.up();
		
		return ShrinkWrap.create(WebArchive.class)
	      .addClasses(SecuredCallInterceptor.class,
	      		Secured.class,
	      		ShiroProducers.class,
	      		SecuredClass.class
	      		)
	      .addAsWebInfResource(new StringAsset(beans.exportAsString()),"beans.xml")
	      .addAsWebInfResource(new StringAsset(ejbJar.exportAsString()),"ejb-jar.xml");
	}

	@BeforeClass void setup() {
		ShiroDriver.setup();
	}
	
	@AfterClass void teardown() {
		ShiroDriver.tearDownShiro();
	}
	

	@BeforeMethod void unsetUser() {
		ShiroDriver.clearSubject();
		SecurityUtils.getSubject().logout();
	}
	
	@Inject SecuredClass testClass;
	
	void login(String username) {
		UsernamePasswordToken token=new UsernamePasswordToken(username,"password");
		SecurityUtils.getSubject().login(token);
	}
	
	void rememberMe(String username) {
		Subject s=new Subject.Builder()
		.principals(new SimplePrincipalCollection(username, "remembered"))
		.buildSubject();
	ShiroDriver.setSubject(s);
	}
	
	@Test public void userShouldBeGuest() throws Exception {
		assertNull(SecurityUtils.getSubject().getPrincipal(), "Principal of subject should be null");
	}
	
	
//	@RequiresUser	public boolean requiresUser() {return true;}
	@Test public void requiresUserSucceeds() throws Exception {
		login("user");
		assertTrue(testClass.requiresUser());
	}
	@Test(expectedExceptions={AuthenticationException.class}) 
	public void requiresUserFailsWithGuest()  throws Exception {
		testClass.requiresUser();
	}
	
//	@RequiresAuthentication	public boolean requiresAuthentication() {return true;}
	@Test public void requiresAuthenticationSucceeds()  throws Exception {
		login("user");

		assertTrue(testClass.requiresAuthentication());
	}
	
	@Test(expectedExceptions={AuthenticationException.class}) 
	public void requiresAuthenticationFailsGuest()  throws Exception {
		testClass.requiresAuthentication();
	}

	@Test(expectedExceptions={AuthenticationException.class}) 
	public void requiresAuthenticationFailsLoggedIn()  throws Exception {
		rememberMe("user");
		testClass.requiresAuthentication();
	}
	
	
//@RequiresGuest	public boolean requiresGuest() {return true;}
	@Test public void requiresGuestSucceeds()  throws Exception {

		assertTrue(testClass.requiresGuest());
	}
	
	@Test(expectedExceptions={AuthenticationException.class}) 
	public void requiresGuestFailsWithLoggedIn()  throws Exception {
		rememberMe("user");
		testClass.requiresGuest();
	}
	
	@Test(expectedExceptions={AuthenticationException.class}) 
	public void requiresGuestFailsWithAuthenticated()  throws Exception {
		login("user");
		assertTrue(testClass.requiresGuest());
	}


// @RequiresRoles("User")	public boolean requiresRoleUser() {return true;}
// @RequiresRoles("Admin")	public boolean requiresRoleAdmin() {return true;}
	@Test public void requiresRoleUserSucceeds()  throws Exception {
		login("user");
		assertTrue(testClass.requiresRoleUser());
	}
	
	@Test(expectedExceptions={AuthorizationException.class}) 
	public void requiresRoleUserFailsGuest()  throws Exception {
		assertTrue(testClass.requiresRoleUser());
	}

	@Test(expectedExceptions={AuthorizationException.class}) 
	public void requiresRoleUserFailsAdmin()  throws Exception {
		login("admin");
		assertTrue(testClass.requiresRoleUser());
	}
	
//	@RequiresRoles(value={"User","Admin"},logical=Logical.AND)	public boolean requiresRoleUserAndAdmin() {return true;}
	@Test public void requiresRoleUserAndAdminSucceeds()  throws Exception {
		login("userAdmin");
		assertTrue(testClass.requiresRoleUserAndAdmin());
	}
	
	@Test(expectedExceptions={AuthorizationException.class}) 
	public void requiresRoleUserAndAdminFailsGuest()  throws Exception {
		assertTrue(testClass.requiresRoleUserAndAdmin());
	}

	@Test(expectedExceptions={AuthorizationException.class}) 
	public void requiresRoleUserAndAdminFailsAdminOnly()  throws Exception {
		login("admin");
		assertTrue(testClass.requiresRoleUserAndAdmin());
	}
	
	@Test(expectedExceptions={AuthorizationException.class}) 
	public void requiresRoleUserAndAdminFailsUserOnly()  throws Exception {
		login("user");
		assertTrue(testClass.requiresRoleUserAndAdmin());
	}
	
//	@RequiresRoles(value={"User","Admin"},logical=Logical.OR)	public boolean requiresRoleUserOrAdmin() {return true;}
	@Test public void requiresRoleUserOrAdminSucceedsWithUser()  throws Exception {
		login("user");
		assertTrue(testClass.requiresRoleUserOrAdmin());
	}
	@Test public void requiresRoleUserOrAdminSucceedsWithAdmin()  throws Exception {
		login("admin");
		assertTrue(testClass.requiresRoleUserOrAdmin());
	}
	@Test public void requiresRoleUserOrAdminSucceedsWithBoth()  throws Exception {
		login("userAdmin");
		assertTrue(testClass.requiresRoleUserOrAdmin());
	}
	@Test(expectedExceptions={AuthorizationException.class}) 
	public void requiresRoleUserOrAdminFailsGuest()  throws Exception {
		assertTrue(testClass.requiresRoleUserOrAdmin());
	}
	@Test(expectedExceptions={AuthorizationException.class}) 
	public void requiresRoleUserOrAdminFailsWithNeither()  throws Exception {
		login("noroles");
		assertTrue(testClass.requiresRoleUserOrAdmin());
	}


//	@RequiresPermissions("doc:read:1")	public boolean requiresPermissionDoc1() {return true;}
	@Test public void requiresPermissionSucceeds()  throws Exception {
		login("doc1");
		SecurityUtils.getSubject().checkPermission("doc:read:1");
		assertTrue(testClass.requiresPermissionDoc1());
	}

	@Test(expectedExceptions={PermissionDeniedException.class}) 
	public void requiresPermissionFails()  throws Exception {
		login("doc2");
		assertTrue(testClass.requiresPermissionDoc1());
	}
	
	
//	@RequiresPermissions(value={"doc:read:1","doc:read:2"},logical=Logical.AND)	public boolean requiresPermissionDoc1AndDoc2() {return true;}
	@Test(expectedExceptions={PermissionDeniedException.class}) 
	public void requiresPermissionFailsWithDoc1()  throws Exception {
		login("doc1");
		assertTrue(testClass.requiresPermissionDoc1AndDoc2());
	}

	@Test(expectedExceptions={PermissionDeniedException.class}) 
	public void requiresPermissionFailsWithDoc2()  throws Exception {
		login("doc2");
		assertTrue(testClass.requiresPermissionDoc1AndDoc2());
	}

//	@RequiresPermissions(value={"doc:read:1","doc:read:2"},logical=Logical.OR)	public boolean requiresPermissionDoc1OrDoc2() {return true;}
	@Test public void requiresOrPermissionSucceedsWithBoth()  throws Exception {
		login("docBoth");
		assertTrue(testClass.requiresPermissionDoc1OrDoc2());
	}

	@Test public void requiresOrPermissionSucceedsWithDoc1()  throws Exception {
		login("doc1");
		assertTrue(testClass.requiresPermissionDoc1OrDoc2());
	}

	@Test public void requiresOrPermissionSucceedsWithDoc2()  throws Exception {
		login("doc2");
		assertTrue(testClass.requiresPermissionDoc1OrDoc2());
	}

	@Test(expectedExceptions={AuthorizationException.class}) 
	public void requiresPermissionFailsWithNeither()  throws Exception {
		login("noroles");
		assertTrue(testClass.requiresPermissionDoc1OrDoc2());
	}
}
