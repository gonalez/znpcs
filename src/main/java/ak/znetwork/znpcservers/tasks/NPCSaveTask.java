package ak.znetwork.znpcservers.tasks;

import ak.znetwork.znpcservers.ServersNPC;
import org.bukkit.scheduler.BukkitRunnable;

public class NPCSaveTask extends BukkitRunnable {

    private final ServersNPC serversNPC;

    private final int seconds;

    public NPCSaveTask(final ServersNPC serversNPC, final int seconds) {
        this.serversNPC = serversNPC;

        this.seconds = seconds;

        runTaskTimer(serversNPC, 100L, seconds);
    }

    @Override
    public void run() {
        this.serversNPC.saveAllNPC();
    }
}
