package io.github.gonalez.znpcservers.command;

/**
 * @author Gaston Gonzalez {@literal <znetworkw.dev@gmail.com>}
 */
public abstract class AnnotatedPluginSubCommand implements PluginSubCommand {
    private final PluginSubCommandParam subCommandSpec;

    protected AnnotatedPluginSubCommand() {
        PluginSubCommandParam subCommandSpec = getClass().getAnnotation(PluginSubCommandParam.class);
        if (subCommandSpec == null) {
            throw new IllegalStateException("no PluginSubCommandParam annotation found");
        }
        this.subCommandSpec = getClass().getAnnotation(PluginSubCommandParam.class);
    }

    @Override
    public String name() {
        return subCommandSpec.name();
    }

    @Override
    public String[] args() {
        return subCommandSpec.args();
    }

    @Override
    public String permission() {
        return subCommandSpec.permission();
    }
}
