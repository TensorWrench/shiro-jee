package com.tensorwrench.shiro.jee.interceptors;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public abstract class AnnotationUtils {

	static public <T extends Annotation> T findAnnotation(Method method,Class<T> annotationType) {
		T anno = method.getAnnotation(annotationType);
		if(anno !=null) {
			return anno;
		}
		return findAnnotation(method.getDeclaringClass(), method, annotationType);
	}
	
	static public <T extends Annotation> T findAnnotation(Class<?> inspectedClass,Method prototype,Class<T> annotationType) {
		T anno=null;
		if(inspectedClass==null) {
			return null;
		}
		// look for a matching method, first
		try {
			Method candidateMethod = inspectedClass.getMethod(prototype.getName(), prototype.getParameterTypes());
			anno=candidateMethod.getAnnotation(annotationType);
			if(anno != null) {
				return anno;
			}
		} catch (NoSuchMethodException | SecurityException e) {
			// fall through
		}
		
		// see if the class has an annotation
		anno=inspectedClass.getAnnotation(annotationType);
		if(anno != null) {
			return anno;
		}
		
		// see if any of our interfaces have the annotation
		for(Class<?> i: inspectedClass.getInterfaces()) {
			anno=findAnnotation(i, prototype, annotationType);
			if(anno != null) {
				return anno;
			}
		}
		
		// climb the inheritance tree
		if(inspectedClass.getSuperclass() != Object.class){
			return findAnnotation(inspectedClass.getSuperclass(), prototype, annotationType);
		}
		return null;
	}
	private AnnotationUtils() {}
}
