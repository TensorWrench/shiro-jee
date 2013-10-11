package com.tensorwrench.shiro.jee.helpers;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;


// Intentionally not @Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({METHOD,FIELD,PARAMETER,TYPE})
public @interface TestAnnotation {
	public Class<?> value();
}
