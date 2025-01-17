package io.github.gonalez.znpcs.commands;

import static com.google.common.truth.Truth.assertThat;

import com.google.common.collect.ImmutableClassToInstanceMap;
import com.google.common.collect.ImmutableList;
import io.github.gonalez.znpcs.command.Command;
import io.github.gonalez.znpcs.command.CommandEnvironment;
import io.github.gonalez.znpcs.command.CommandResult;
import io.github.gonalez.znpcs.command.BaseCommandEnvironment;
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
    commandEnvironment = new BaseCommandEnvironment(ImmutableClassToInstanceMap.of());
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
        ImmutableList<String> args, CommandEnvironment commandEnvironment) {
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
    CommandResult commandResult = commandEnvironment.executeCommand(exampleCommand, ImmutableList.of());
    assertThat(commandResult.getActualCommand()).isEqualTo(exampleCommand);
    assertThat(commandResult.getActualCommand().getName()).isEqualTo("hello");
  }

  public static final class ExampleTreeCommand extends Command {

    @Override
    public String getName() {
      return "";
    }

    @Override
    protected CommandResult execute(
        ImmutableList<String> args, CommandEnvironment commandEnvironment) {
      return newCommandResult();
    }

    @Override
    protected int getMandatoryArguments() {
      return 1;
    }

    @Override
    protected Collection<Command> getChildren() {
      return ImmutableList.of(new ExampleCommand("foo") {
        @Override
        protected Collection<Command> getChildren() {
          return ImmutableList.of(new ExampleCommand("bar"));
        }
      });
    }
  }

  @Test
  public void testExecuteCommand_withChildren_returnsCorrectCommandInstance() throws Exception {
    Command treeCommand = new ExampleTreeCommand();
    CommandResult commandResult = commandEnvironment.executeCommand(treeCommand, ImmutableList.of("foo", "bar"));
    assertThat(commandResult.getActualCommand()).isInstanceOf(CommandTest.ExampleCommand.class);
    assertThat(commandResult.getActualCommand().getName()).isEqualTo("bar");
    commandResult = commandEnvironment.executeCommand(treeCommand, ImmutableList.of("bar", "foo"));
    assertThat(commandResult.getActualCommand()).isNotInstanceOf(ExampleCommand.class);
  }
}