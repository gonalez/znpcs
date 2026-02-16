package io.github.gonalez.znpcs.commands;

import static com.google.common.truth.Truth.assertThat;

import com.google.common.collect.ImmutableClassToInstanceMap;
import com.google.common.collect.ImmutableList;
import io.github.gonalez.znpcs.command.Command;
import io.github.gonalez.znpcs.command.CommandContext;
import io.github.gonalez.znpcs.command.CommandEnvironment;
import io.github.gonalez.znpcs.command.CommandResult;
import io.github.gonalez.znpcs.command.NoopCommandContext;
import io.github.gonalez.znpcs.command.OutputStreamCommandContext;
import io.github.gonalez.znpcs.config.ConfigProvider;
import java.io.ByteArrayOutputStream;
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
    commandEnvironment = new CommandEnvironment(ConfigProvider.EMPTY, ImmutableClassToInstanceMap.of());
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
        commandEnvironment, NoopCommandContext.INSTANCE, ImmutableList.of());
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
        commandEnvironment, NoopCommandContext.INSTANCE, ImmutableList.of("foo", "bar"));
    assertThat(commandResult.getActualCommand()).isInstanceOf(CommandTest.ExampleCommand.class);
    assertThat(commandResult.getActualCommand().getName()).isEqualTo("bar");
    commandResult = treeCommand.executeCommand(
        commandEnvironment, NoopCommandContext.INSTANCE, ImmutableList.of("bar", "foo"));
    assertThat(commandResult.getActualCommand()).isNotInstanceOf(ExampleCommand.class);
  }

  @Test
  public void testExecuteCommand_consoleStream_logs() throws Exception {
    Command command = new ExampleCommand("") {
      @Override
      protected int getMandatoryArguments() {
        return super.getMandatoryArguments();
      }

      @Override
      public CommandResult execute(CommandEnvironment env, CommandContext ctx,
          ImmutableList<String> args) {
        ctx.log("hello %s", "world");
        return super.execute(env, ctx, args);
      }
    };

    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    command.executeCommand(
        commandEnvironment,
        new OutputStreamCommandContext("console", outputStream),
        ImmutableList.of());

    assertThat(outputStream.toString()).contains("hello world");
  }

  public static final class ExampleTreeCommandWithDependency extends Command {

    @Override
    public String getName() {
      return "";
    }

    @Override
    public CommandResult execute(CommandEnvironment env, CommandContext ctx, ImmutableList<String> args) {
      return newCommandResult().addDependency(String.class, "hello world");
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
          String value = env.getMergedCommandResult().getDependency(String.class);
          return newCommandResult().setSuccessMessage(Optional.ofNullable(value).orElse(""));
        }
      });
    }
  }

  @Test
  public void testExecuteCommand_childrenDependency() throws Exception {
    Command treeCommand = new ExampleTreeCommandWithDependency();
    CommandResult commandResult = treeCommand.executeCommand(
        commandEnvironment, NoopCommandContext.INSTANCE, ImmutableList.of("foo"));
    assertThat(commandResult.getSuccessMessage()).isEqualTo("hello world");
  }
}