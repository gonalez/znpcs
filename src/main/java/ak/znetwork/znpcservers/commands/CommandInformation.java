package ak.znetwork.znpcservers.commands;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * An annotation for the command as described in {@link Command}.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface CommandInformation {
    /**
     * The possible command arguments.
     */
    String[] aliases();

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