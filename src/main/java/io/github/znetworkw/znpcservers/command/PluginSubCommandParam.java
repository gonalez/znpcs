package io.github.znetworkw.znpcservers.command;

import java.lang.annotation.*;

/**
 * Metadata information for sub-commands.
 *
 * @author Gaston Gonzalez {@literal <znetworkw.dev@gmail.com>}
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface PluginSubCommandParam {
    /**
     * The subcommand name.
     *
     * @return subcommand name.
     */
    String name();

    /**
     * The sub command permission.
     *
     * @return sub command permission or default {@code ""}.
     */
    String permission() default "";

    /**
     * The subcommand arguments.
     *
     * @return subcommand arguments.
     */
    String[] args() default {};
}
