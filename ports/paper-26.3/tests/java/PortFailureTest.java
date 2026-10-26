import java.nio.file.*;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

/** A failed data read must disable the plugin without overwriting any source bytes. */
public final class PortFailureTest extends JavaPlugin {
  @Override
  public void onEnable() {
    Bukkit.getScheduler()
        .runTaskLater(
            this,
            () -> {
              try {
                boolean disabled = !Bukkit.getPluginManager().isPluginEnabled("ServersNPC");
                byte[] expected = Files.readAllBytes(Path.of("failure-expected.bin"));
                byte[] actual = Files.readAllBytes(Path.of("plugins/ServersNPC/data.json"));
                if (!disabled || !java.util.Arrays.equals(expected, actual))
                  throw new AssertionError("Plugin enabled or damaged file was overwritten");
                Files.writeString(
                    Path.of("failure-result.txt"),
                    "PASS: plugin disabled, original data.json preserved byte-for-byte");
              } catch (Throwable error) {
                try {
                  Files.writeString(Path.of("failure-result.txt"), "FAIL: " + error);
                } catch (Exception ignored) {
                }
              } finally {
                Bukkit.shutdown();
              }
            },
            40);
  }
}
