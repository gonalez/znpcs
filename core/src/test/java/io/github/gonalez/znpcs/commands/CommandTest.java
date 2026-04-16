package io.github.gonalez.znpcs.commands;

import static com.google.common.truth.Truth.assertThat;

import com.google.common.collect.ImmutableClassToInstanceMap;
import com.google.common.collect.ImmutableList;
import java.util.Collection;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/** Unit tests for {@link Command}. */
@RunWith(JUnit4.class)
public class CommandTest {
  private CommandEnvironment commandEnvironment;

  @Before
  public void setup() {
    commandEnvironment = new CommandEnvironment(ImmutableClassToInstanceMap.of());
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
    public CommandResult execute(CommandEnvironment env, CommandContext ctx, ImmutableList<String> args) {
      return newCommandResult();
    }

    @Override
    protected int getMandatoryArguments() {
      return 0;
    }

    @Override
    protected Collection<Command> getChildren() {
      return ImmutableList.of();
    }
  }

  @Test
  public void testExecuteCommand_commandResult() throws Exception {
    ExampleCommand exampleCommand = new ExampleCommand("hello");
    CommandResult commandResult = exampleCommand.executeCommand(
        commandEnvironment, CommandContext.DEFAULT_INSTANCE, ImmutableList.of());
    assertThat(commandResult.getActualCommand()).isEqualTo(exampleCommand);
    assertThat(commandResult.getActualCommand().getName()).isEqualTo("hello");
  }

  public static final class ExampleTreeCommand extends Command {

    @Override
    public String getName() {
      return "";
    }

    @Override
    public CommandResult execute(CommandEnvironment env, CommandContext ctx, ImmutableList<String> args) {
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
    CommandResult commandResult = treeCommand.executeCommand(
        commandEnvironment, CommandContext.DEFAULT_INSTANCE, ImmutableList.of("foo", "bar"));
    assertThat(commandResult.getActualCommand()).isInstanceOf(CommandTest.ExampleCommand.class);
    assertThat(commandResult.getActualCommand().getName()).isEqualTo("bar");
    commandResult = treeCommand.executeCommand(
        commandEnvironment, CommandContext.DEFAULT_INSTANCE, ImmutableList.of("bar", "foo"));
    assertThat(commandResult.getActualCommand()).isNotInstanceOf(ExampleCommand.class);
  }

  public static final class ExampleTreeCommandWithDependency extends Command {

    @Override
    public String getName() {
      return "";
    }

    @Override
    public CommandResult execute(CommandEnvironment env, CommandContext ctx, ImmutableList<String> args) {
      return newCommandResult().setContext(ctx.toBuilder().put(String.class, "hello world").build());
    }

    @Override
    protected int getMandatoryArguments() {
      return 1;
    }

    @Override
    protected Collection<Command> getChildren() {
      return ImmutableList.of(new ExampleCommand("foo") {
        @Override
        public CommandResult execute(CommandEnvironment env, CommandContext ctx,
            ImmutableList<String> args) {
          String value = ctx.get(String.class);
          return newCommandResult().setSuccessMessage(Optional.ofNullable(value).orElse(""));
        }
      });
    }
  }

  @Test
  public void testExecuteCommand_childrenDependency() throws Exception {
    Command treeCommand = new ExampleTreeCommandWithDependency();
    CommandResult commandResult = treeCommand.executeCommand(
        commandEnvironment, CommandContext.DEFAULT_INSTANCE, ImmutableList.of("foo"));
    assertThat(commandResult.getSuccessMessage()).isEqualTo("hello world");
  }
}