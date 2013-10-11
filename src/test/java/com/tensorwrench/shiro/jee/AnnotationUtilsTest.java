package com.tensorwrench.shiro.jee;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.tensorwrench.shiro.jee.helpers.TestAnnotation;
import com.tensorwrench.shiro.jee.interceptors.AnnotationUtils;

import static org.testng.Assert.*;

class UnrelatedClass {
}

interface UnAnnotatedInterface {	
	void a();
}
class UnAnnotatedClass {
	public void a() {}
}

interface MethodAnnotatedInterface {	
	@TestAnnotation(MethodAnnotatedInterface.class) void a();
}

class MethodAnnotatedBase {
	@TestAnnotation(MethodAnnotatedBase.class) public void a() {}
}

@TestAnnotation(AnnotatedInterface.class) 
interface AnnotatedInterface {	
	public void a();
}

@TestAnnotation(AnnotatedBase.class) 
class AnnotatedBase {	
	public void a() {};
}


class AnnotatedExtends extends AnnotatedBase {}
class AnnotatedImplements implements AnnotatedInterface {
	@Override	public void a() {}
}

class MethodAnnotatedExtends extends MethodAnnotatedBase {}
class MethodAnnotatedImplements implements MethodAnnotatedInterface {
	@Override	public void a() {}
}

class DoubleExtends extends AnnotatedExtends {}
class ParentImplements extends AnnotatedImplements {}

class ExtendsUnrelated extends UnrelatedClass {
	public void a() {}
}

class MixedInterfaces implements UnAnnotatedInterface,AnnotatedInterface {
	@Override public void a() {}
}
class MixedMethodInterfaces implements UnAnnotatedInterface,MethodAnnotatedInterface {
	@Override public void a() {}
}
public class AnnotationUtilsTest {

	@DataProvider(name="hasNoAnnotations")
	public Object[][] hasNoAnnotations() {
		return new Object[][] {
				/*             Class to search for annotation , where the annotation lives */
				new Object[] { UnAnnotatedClass.class }, 
				new Object[] { UnAnnotatedInterface.class },
				new Object[] { ExtendsUnrelated.class}
				
		};	
	}

	@DataProvider(name="hasDirectAnnotations")
	public Object[][] hasDirectAnnotations() {
		return new Object[][] {
				/*             Class to search for annotation , where the annotation lives */
				new Object[] { MethodAnnotatedInterface.class }, 
				new Object[] { MethodAnnotatedBase.class },
				new Object[] { AnnotatedInterface.class },
				new Object[] { AnnotatedBase.class },
				
		};	
	}
	
	@DataProvider(name="hasIndirectAnnotations")
	public Object[][] hasInDirectAnnotations() {
		return new Object[][] {
				/*             Class to search for annotation , where the annotation lives */
				new Object[] { MethodAnnotatedImplements.class , MethodAnnotatedInterface.class}, 
				new Object[] { MethodAnnotatedExtends.class, MethodAnnotatedBase.class},
				new Object[] { AnnotatedImplements.class , AnnotatedInterface.class},
				new Object[] { AnnotatedExtends.class, AnnotatedBase.class },
				new Object[] { DoubleExtends.class, AnnotatedBase.class },
				new Object[] { ParentImplements.class, AnnotatedInterface.class },
				new Object[] { MixedInterfaces.class , AnnotatedInterface.class}
		};	
	}	
	@Test(dataProvider="hasNoAnnotations")
	public void doesNotFindOnUnannotated(Class<?> c) throws NoSuchMethodException, SecurityException {
		TestAnnotation a=AnnotationUtils.findAnnotation(c.getMethod("a"), TestAnnotation.class);
		assertNull(a);
	}
	@Test(dataProvider="hasDirectAnnotations")
	public void findsDirectlyAnnotated(Class<?> c) throws NoSuchMethodException, SecurityException {
		TestAnnotation a=AnnotationUtils.findAnnotation(c.getMethod("a"), TestAnnotation.class);
		assertNotNull(a);
		assertEquals(c,a.value());
	}
	
	@Test(dataProvider="hasIndirectAnnotations")
	public void findsIndirectlyAnnotated(Class<?> testClass,Class<?> annotatedClass) throws NoSuchMethodException, SecurityException {
		TestAnnotation a=AnnotationUtils.findAnnotation(testClass.getMethod("a"), TestAnnotation.class);
		assertNotNull(a);
		assertEquals(annotatedClass,a.value());
	}
	
	
}
