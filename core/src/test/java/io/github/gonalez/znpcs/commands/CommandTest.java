package io.github.gonalez.znpcs.commands;

import static com.google.common.truth.Truth.assertThat;

import com.google.common.collect.ImmutableList;
import io.github.gonalez.znpcs.command.Command;
import io.github.gonalez.znpcs.command.CommandProvider;
import io.github.gonalez.znpcs.command.CommandResult;
import java.util.Collection;
import org.junit.jupiter.api.Test;

/** Unit tests for {@link Command}. */
public class CommandTest {

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
    protected CommandResult execute(CommandProvider commandProvider, ImmutableList<String> args) {
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
    CommandResult commandResult = exampleCommand.executeCommand(CommandProvider.NOOP, ImmutableList.of());
    assertThat(commandResult.getActualCommand()).isEqualTo(exampleCommand);
    assertThat(commandResult.getActualCommand().getName()).isEqualTo("hello");
  }

  public static final class ExampleTreeCommand extends Command {

    @Override
    public String getName() {
      return "";
    }

    @Override
    public CommandResult execute(CommandProvider commandProvider, ImmutableList<String> args) {
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
    CommandResult commandResult = treeCommand.executeCommand(CommandProvider.NOOP, ImmutableList.of("foo", "bar"));
    assertThat(commandResult.getActualCommand()).isInstanceOf(CommandTest.ExampleCommand.class);
    assertThat(commandResult.getActualCommand().getName()).isEqualTo("bar");
    commandResult = treeCommand.executeCommand(CommandProvider.NOOP, ImmutableList.of("bar", "foo"));
    assertThat(commandResult.getActualCommand()).isNotInstanceOf(ExampleCommand.class);
  }
}