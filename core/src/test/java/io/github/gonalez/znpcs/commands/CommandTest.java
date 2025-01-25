package io.github.gonalez.znpcs.commands;

import static com.google.common.truth.Truth.assertThat;

import com.google.common.collect.ImmutableClassToInstanceMap;
import com.google.common.collect.ImmutableList;
import io.github.gonalez.znpcs.command.Command;
import io.github.gonalez.znpcs.command.CommandEnvironment;
import io.github.gonalez.znpcs.command.CommandException;
import io.github.gonalez.znpcs.command.CommandHooks;
import io.github.gonalez.znpcs.command.CommandResult;
import io.github.gonalez.znpcs.command.BaseCommandEnvironment;
import io.github.gonalez.znpcs.command.MissingConfigurationCommandException;
import io.github.gonalez.znpcs.configuration.Configuration;
import io.github.gonalez.znpcs.configuration.SimpleConfigurationProvider;
import java.util.Collection;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/** Unit tests for {@link Command}. */
@RunWith(JUnit4.class)
public class CommandTest {

  private static CommandEnvironment commandEnvironment;

  @BeforeAll
  public static void setUp() throws Exception {
    commandEnvironment = new BaseCommandEnvironment(
        ImmutableClassToInstanceMap.of(), SimpleConfigurationProvider.newBuilder().build());
  }

  public static class ExampleCommand extends Command {

    private final String name;

    public ExampleCommand(String name) {
      this.name = name;
    }

    @Override
    public String getName() {
      return name;
    }

    @Override
    protected CommandResult execute(
        ImmutableList<String> args, CommandEnvironment commandEnvironment, CommandHooks hooks) {
      return newCommandResult();
    }

    @Override
    protected int getMandatoryArguments() {
      return 1;
    }

    @Override
    protected Collection<Command> getChildren() {
      return ImmutableList.of();
    }
  }

  @Test
  public void testExecuteCommand_commandResult() throws Exception {
    ExampleCommand exampleCommand = new ExampleCommand("hello");
    CommandResult commandResult = commandEnvironment.executeCommand(
        exampleCommand, ImmutableList.of());
    assertThat(commandResult.getActualCommand()).isEqualTo(exampleCommand);
    assertThat(commandResult.getActualCommand().getName()).isEqualTo("hello");
  }

  public static final class ExampleCommandUsesConfig extends Command {

    @Override
    public String getName() {
      return "";
    }

    private static class RequiredConfig extends Configuration {

    }

    @Override
    protected CommandResult execute(ImmutableList<String> args,
        CommandEnvironment commandEnvironment, CommandHooks hooks) throws CommandException {
      hooks.getConfigurationOrThrow(RequiredConfig.class);
      return CommandResult.create(this);
    }

    @Override
    protected int getMandatoryArguments() {
      return 0;
    }
  }

  @Test
  public void testExecuteCommand_requiresConfig_throwsMissingConfigurationCommandException() throws Exception {
    Command command = new ExampleCommandUsesConfig();
    CommandResult commandResult = commandEnvironment.executeCommand(command, ImmutableList.of());
    assertThat(commandResult.getError()).isNotNull();
    assertThat(commandResult.getError()).isInstanceOf(MissingConfigurationCommandException.class);
    assertThat(commandResult.getError()).hasMessageThat()
        .matches("Missing configuration dependency: .*");
  }

  @Test
  public void testExecuteCommand_requiresConfig_succeeds() throws Exception {
    Command command = new ExampleCommandUsesConfig();
    CommandResult commandResult =
        new BaseCommandEnvironment(ImmutableClassToInstanceMap.of(),
            SimpleConfigurationProvider.newBuilder()
                .addConfiguration(
                    ExampleCommandUsesConfig.RequiredConfig.class,
                    new ExampleCommandUsesConfig.RequiredConfig())
                .build())
       .executeCommand(command, ImmutableList.of());
    assertThat(commandResult.hasError()).isFalse();
  }
}