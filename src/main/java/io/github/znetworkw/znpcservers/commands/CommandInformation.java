package io.github.znetworkw.znpcservers.commands;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotates methods on {@link Command}s instances to expose them as a sub command.
 */
@Target(METHOD)
@Retention(RUNTIME)
@Documented
public @interface CommandInformation {
    /**
     * The possible command arguments.
     */
    String[] arguments();

    /**
     * The command help message.
     */
    String[] help() default {};

    /**
     * The command name.
     */
    String name();

    /**
     * The command permission.
     */
    String permission();

    /**
     * Determines if the command have multiple subcommands.
     */
    boolean isMultiple() default false;
}