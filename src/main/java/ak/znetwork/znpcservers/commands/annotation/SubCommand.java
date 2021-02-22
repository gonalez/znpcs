package ak.znetwork.znpcservers.commands.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * <p>Copyright (c) ZNetwork, 2020.</p>
 *
 * @author ZNetwork
 * @since 07/02/2020
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface SubCommand {

    /**
     * The possible command arguments.
     */
    String[] aliases();

    /**
     * The subcommand name.
     */
    String required();

    /**
     * The command permission.
     */
    String permission();
}
