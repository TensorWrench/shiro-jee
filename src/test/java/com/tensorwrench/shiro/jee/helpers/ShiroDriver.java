package com.tensorwrench.shiro.jee.helpers;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.UnavailableSecurityManagerException;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.subject.support.SubjectThreadState;
import org.apache.shiro.util.Factory;
import org.apache.shiro.util.LifecycleUtils;
import org.apache.shiro.util.ThreadState;
import org.apache.shiro.mgt.SecurityManager;

public class ShiroDriver {

  private static ThreadState subjectThreadState;
  
	public static void setup() {
		Factory<SecurityManager> factory = new IniSecurityManagerFactory("classpath:test.shiro.ini");
		setSecurityManager(factory.getInstance());
	}
  /**
   * Allows subclasses to set the currently executing {@link Subject} instance.
   *
   * @param subject the Subject instance
   */
	public static void setSubject(Subject subject) {
      clearSubject();
      subjectThreadState = createThreadState(subject);
      subjectThreadState.bind();
  }

  public static Subject getSubject() {
      return SecurityUtils.getSubject();
  }

  protected static ThreadState createThreadState(Subject subject) {
      return new SubjectThreadState(subject);
  }

  /**
   * Clears Shiro's thread state, ensuring the thread remains clean for future test execution.
   */
  public static void clearSubject() {
      doClearSubject();
  }

  private static void doClearSubject() {
      if (subjectThreadState != null) {
          subjectThreadState.clear();
          subjectThreadState = null;
      }
  }

  protected static void setSecurityManager(SecurityManager securityManager) {
      SecurityUtils.setSecurityManager(securityManager);
  }

  public static SecurityManager getSecurityManager() {
      return SecurityUtils.getSecurityManager();
  }

  public static void tearDownShiro() {
      doClearSubject();
      try {
          SecurityManager securityManager = getSecurityManager();
          LifecycleUtils.destroy(securityManager);
      } catch (UnavailableSecurityManagerException e) {
          //we don't care about this when cleaning up the test environment
          //(for example, maybe the subclass is a unit test and it didn't
          // need a SecurityManager instance because it was using only 
          // mock Subject instances)
      }
      setSecurityManager(null);
  }
}
