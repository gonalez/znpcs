package io.github.gonalez.znpcs.npc;

import io.github.gonalez.znpcs.ServersNPC;
import io.github.gonalez.znpcs.ZNPConfigUtils;
import io.github.gonalez.znpcs.configuration.ConfigConfiguration;
import io.github.gonalez.znpcs.user.ZUser;
import io.github.gonalez.znpcs.utility.location.ZLocation;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public interface NPCPath {
  void initialize(DataInputStream paramDataInputStream) throws IOException;
  
  void write(DataOutputStream paramDataOutputStream) throws IOException;
  
  void start();
  
  PathInitializer getPath(NPC paramNPC);
  
  interface PathInitializer {
    void handle();
    
    ZLocation getLocation();
    
    abstract class AbstractPath implements PathInitializer {
      private final NPC npc;
      
      private final AbstractTypeWriter typeWriter;
      
      private ZLocation location;
      
      public AbstractPath(NPC npc, AbstractTypeWriter typeWriter) {
        this.npc = npc;
        this.typeWriter = typeWriter;
      }
      
      public NPC getNpc() {
        return this.npc;
      }
      
      public AbstractTypeWriter getPath() {
        return this.typeWriter;
      }
      
      public void setLocation(ZLocation location) {
        this.location = location;
      }
      
      public ZLocation getLocation() {
        return this.location;
      }
    }
  }
  
  class ZNPCPathDelegator {
    private final File file;
    
    protected ZNPCPathDelegator(File file) {
      this.file = file;
    }
    
    public DataOutputStream getOutputStream() throws IOException {
      return new DataOutputStream(new FileOutputStream(this.file));
    }
    
    public DataInputStream getInputStream() throws IOException {
      return new DataInputStream(new FileInputStream(this.file));
    }
    
    public static ZNPCPathDelegator forFile(File file) {
      return new ZNPCPathDelegator(file);
    }
    
    public static ZNPCPathDelegator forPath(AbstractTypeWriter pathAbstract) {
      return new ZNPCPathDelegator(pathAbstract.getFile());
    }
  }
  
  abstract class AbstractTypeWriter implements NPCPath {
    private static final Logger LOGGER = Logger.getLogger(AbstractTypeWriter.class.getName());
    
    private static final ConcurrentMap<String, AbstractTypeWriter> PATH_TYPES = new ConcurrentHashMap<>();

    private static final int PATH_DELAY = 1;

    private final TypeWriter typeWriter;

    private final File file;
    
    private final List<ZLocation> locationList;
    
    public AbstractTypeWriter(TypeWriter typeWriter, File file) {
      this.typeWriter = typeWriter;
      this.file = file;
      this.locationList = new ArrayList<>();
    }
    
    public void load() {
      try {
        DataInputStream reader = ZNPCPathDelegator.forFile(this.file).getInputStream();
        try {
          initialize(reader);
          register(this);
          if (reader != null)
            reader.close(); 
        } catch (Throwable throwable) {
          if (reader != null)
            try {
              reader.close();
            } catch (Throwable throwable1) {
              throwable.addSuppressed(throwable1);
            }  
          throw throwable;
        } 
      } catch (IOException e) {
        LOGGER.log(Level.WARNING, String.format("The path %s could not be loaded", this.file.getName()));
      } 
    }
    
    public void write() {
      try {
        DataOutputStream writer = ZNPCPathDelegator.forFile(getFile()).getOutputStream();
        try {
          write(writer);
          if (writer != null)
            writer.close(); 
        } catch (Throwable throwable) {
          if (writer != null)
            try {
              writer.close();
            } catch (Throwable throwable1) {
              throwable.addSuppressed(throwable1);
            }  
          throw throwable;
        } 
      } catch (IOException e) {
        LOGGER.log(Level.WARNING, String.format("Path %s could not be created", getName()), e);
      } 
    }
    
    public static AbstractTypeWriter forCreation(File file, ZUser user, TypeWriter typeWriter) {
      if (typeWriter == TypeWriter.MOVEMENT)
        return new TypeMovement(file, user);
      throw new IllegalStateException("can't find type writer for: " + typeWriter.name());
    }
    
    public static AbstractTypeWriter forFile(File file, TypeWriter typeWriter) {
      if (typeWriter == TypeWriter.MOVEMENT)
        return new TypeMovement(file); 
      throw new IllegalStateException("can't find type writer for: " + typeWriter.name());
    }
    
    public File getFile() {
      return this.file;
    }
    
    public List<ZLocation> getLocationList() {
      return this.locationList;
    }
    
    public String getName() {
      return this.file.getName().substring(0, this.file.getName().lastIndexOf('.'));
    }
    
    public static void register(AbstractTypeWriter abstractZNPCPath) {
      PATH_TYPES.put(abstractZNPCPath.getName(), abstractZNPCPath);
    }
    
    public static AbstractTypeWriter find(String name) {
      return PATH_TYPES.get(name);
    }
    
    public static Collection<AbstractTypeWriter> getPaths() {
      return PATH_TYPES.values();
    }
    
    public enum TypeWriter {
      MOVEMENT
    }
    
    private static class TypeMovement extends AbstractTypeWriter {
      private ZUser npcUser;
      
      private BukkitTask bukkitTask;
      
      public TypeMovement(File file) {
        super(TypeWriter.MOVEMENT, file);
      }
      
      public TypeMovement(File file, ZUser npcUser) {
        super(TypeWriter.MOVEMENT, file);
        this.npcUser = npcUser;
        start();
      }
      
      public void initialize(DataInputStream dataInputStream) throws IOException {
        while (dataInputStream.available() > 0) {
          String worldName = dataInputStream.readUTF();
          double x = dataInputStream.readDouble();
          double y = dataInputStream.readDouble();
          double z = dataInputStream.readDouble();
          float yaw = dataInputStream.readFloat();
          float pitch = dataInputStream.readFloat();
          getLocationList().add(new ZLocation(worldName, x, y, z, yaw, pitch));
        } 
      }
      
      public void write(DataOutputStream dataOutputStream) throws IOException {
        if (getLocationList().isEmpty())
          return; 
        Iterator<ZLocation> locationIterator = getLocationList().iterator();
        while (locationIterator.hasNext()) {
          ZLocation location = locationIterator.next();
          dataOutputStream.writeUTF(location.getWorldName());
          dataOutputStream.writeDouble(location.getX());
          dataOutputStream.writeDouble(location.getY());
          dataOutputStream.writeDouble(location.getZ());
          dataOutputStream.writeFloat(location.getYaw());
          dataOutputStream.writeFloat(location.getPitch());
          if (!locationIterator.hasNext())
            register(this); 
        } 
      }
      
      public void start() {
        this.npcUser.setHasPath(true);
        int maxPathLocations = ZNPConfigUtils.getConfig(ConfigConfiguration.class).maxPathLocations;
        this.bukkitTask = ServersNPC.SCHEDULER.runTaskTimerAsynchronously(() -> {
              if (this.npcUser.toPlayer() != null && this.npcUser.isHasPath()
                  && maxPathLocations > getLocationList().size()) {
                Location location = this.npcUser.toPlayer().getLocation();
                if (isValid(location))
                  getLocationList().add(new ZLocation(location)); 
              } else {
                this.bukkitTask.cancel();
                this.npcUser.setHasPath(false);
                write();
              } 
            }, 1, 1);
      }
      
      public MovementPath getPath(NPC npc) {
        return new MovementPath(npc, this);
      }
      
      protected boolean isValid(Location location) {
        if (getLocationList().isEmpty())
          return true; 
        ZLocation last = getLocationList().get(getLocationList().size() - 1);
        double xDiff = Math.abs(last.getX() - location.getX());
        double yDiff = Math.abs(last.getY() - location.getY());
        double zDiff = Math.abs(last.getZ() - location.getZ());
        return (xDiff + yDiff + zDiff > 0.01D);
      }
      
      protected static class MovementPath extends PathInitializer.AbstractPath {
        private int currentEntryPath = 0;
        
        private boolean pathReverse = false;
        
        public MovementPath(NPC npc, TypeMovement path) {
          super(npc, path);
        }
        
        public void handle() {
          updatePathLocation(getPath().getLocationList().get(this.currentEntryPath = getNextLocation()));
          int nextIndex = getNextLocation();
          if (nextIndex < 1) {
            this.pathReverse = false;
          } else if (nextIndex >= getPath().getLocationList().size() - 1) {
            this.pathReverse = true;
          } 
        }
        
        private int getNextLocation() {
          return this.pathReverse ? (this.currentEntryPath - 1) : (this.currentEntryPath + 1);
        }
        
        protected void updatePathLocation(ZLocation location) {
          setLocation(location);
          ZLocation next = getPath().getLocationList().get(getNextLocation());
          Vector vector = next.toVector().add(new Vector(0.0D, location.getY() - next.getY(), 0.0D));
          Location direction = next.bukkitLocation().clone().setDirection(location.toVector().subtract(vector)
              .multiply(new Vector(-1, 0, -1)));
          getNpc().setLocation(direction, false);
          getNpc().lookAt(null, direction, true);
        }
      }
    }
  }
}
