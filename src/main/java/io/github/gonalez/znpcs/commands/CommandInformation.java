package io.github.gonalez.znpcs.commands;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CommandInformation {
  String[] arguments();
  
  String[] help() default {};
  
  String name();
  
  String permission();
  
  boolean isMultiple() default false;
}
