package ak.znetwork.znpcservers.commands.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface CMDInfo {

    String[] getArguments();
}
