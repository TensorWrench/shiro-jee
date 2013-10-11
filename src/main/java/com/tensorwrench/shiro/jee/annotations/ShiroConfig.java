package com.tensorwrench.shiro.jee.annotations;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.inject.Qualifier;

import static java.lang.annotation.ElementType.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({METHOD,FIELD,PARAMETER,TYPE})
@Qualifier
public @interface ShiroConfig {}
