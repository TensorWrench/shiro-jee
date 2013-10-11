package com.tensorwrench.shiro.jee.annotations;


import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.interceptor.InterceptorBinding;

import static java.lang.annotation.ElementType.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({METHOD,TYPE})
@InterceptorBinding @Inherited
public @interface Secured {}
