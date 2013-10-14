JEE integration for Apache Shiro
================================
This lib provides a CDI interceptor that inspects the Shiro annotations on a bean.  It also provides beans for Subject, Session, and SecurityManager.
It's based off the patch in https://issues.apache.org/jira/browse/SHIRO-422 https://issues.apache.org/jira/browse/SHIRO-337, but exists as a stand-alone project.

In addition, shiro-jee can be used to use CDI injections on the security manager.

Annotation-based security Usage
===============================
Use the com.tensorwrench.shiro.jee.@Secured annotation to flag a bean as being secured with Shrio's @Requires* annotations.  The interceptor will validate the 
@Requires* annotations.

Add the interceptor to your beans.xml:

	<?xml version="1.0" encoding="UTF-8"?>
	<beans xmlns="http://java.sun.com/xml/ns/javaee" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
	   <interceptors>
	      <class>com.tensorwrench.shiro.jee.interceptors.SecuredCallInterceptor</class>
	   </interceptors>
	</beans>

CDI Injected Configuration
==========================
The com.tensorwrench.shiro.environment.ShiroCDIEnvironmentLoader replaces the Shiro Environment Load Listener to allow for CDI based injection of realms.

	<listener>
    <listener-class>com.tensorwrench.shiro.environment.ShiroCDIEnvironmentLoader</listener-class>
	</listener>

Currently, realms can be configured by annotation a producer that returns List<Realm> with @ShiroConfig.  Everything else comes from the ini file.


Notes
=====
Since the @Requires* annotations are not marked as inherited, the code does a recursive search for them on:
- The method being invoked.
- The class of the object being invoked.
- The method and class of each interface the class implements.
- The method, class, and interfaces of the superclass all the way back to java.lang.Object.

The session, subject, and security manager are the ones that exist at the time of injection.  If the subject changes during a call, the injection will be out of date.


