package me.pwnage.bukkit.AdminMode;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class AdminMode extends JavaPlugin
{
    public Logger log = Logger.getLogger("Minecraft");
    public HashMap<Player, ItemStack[]> pi = new HashMap<Player, ItemStack[]>();
    public HashMap<Player, Location>  pl = new HashMap<Player, Location>();
    public HashMap<Player, Integer>  ph = new HashMap<Player, Integer>();
    public ItemStack[] AdminStack = new ItemStack[36];
    public amEntity amEntity = new amEntity(this);
    public amPlayer amPlayer = new amPlayer(this);
    public String name = "AdminMode";
    public String ver  = "0.4.3";
    public Properties settings;
    public PermissionHandler permh;
    public boolean resetLoc = false, resetHealth = false, resetItems = false;

    @Override
    public void onEnable()
    {
        readCfg();
        registerPermissions();
        getServer().getPluginManager().registerEvent(Type.ENTITY_DAMAGE, amEntity, Priority.Normal, this);
        getServer().getPluginManager().registerEvent(Type.ENTITY_TARGET, amEntity, Priority.Normal, this);
        getServer().getPluginManager().registerEvent(Type.PLAYER_JOIN, amPlayer, Priority.Normal, this);
        getServer().getPluginManager().registerEvent(Type.PLAYER_QUIT, amPlayer, Priority.Normal, this);
        log.log(Level.INFO, "[" + name + "] " + name + " Version " + ver + " Enabled.");
    }

    @Override
    public void onDisable()
    {
        log.log(Level.INFO, "[" + name + "] " + name + " Version " + ver + " Disabled.");
    }

    @Override
    public boolean onCommand(CommandSender cs, Command c, String name, String[] split)
    {
        if(name.equals("adminmode") || name.equals("am"))
        {
            if(cs instanceof Player)
            {
                Player p = (Player)cs;
                boolean canUse = false;
                if(permh != null)
                {
                    canUse = permh.has(p, "adminmode.use");
                    if(permh.has(p, "*"))
                    {
                        canUse = true;
                    }
                }

                if(p.isOp() || canUse)
                {
                    if(this.pi.containsKey((Player)cs))
                    {
                        resetInv(p);
                        p.sendMessage(ChatColor.YELLOW + "Disabled Admin Mode.");
                    } else if(!this.pi.containsKey((Player)cs)) {
                        pullInv(p);
                        p.sendMessage(ChatColor.YELLOW + "Enabled Admin Mode. Type " + ChatColor.BLUE + "/" + name + " off " + ChatColor.YELLOW + " to disable.");
                    }
                } else {
                    p.sendMessage(ChatColor.RED + "You do not have permission to run this command!");
                }
            } else {
                cs.sendMessage("You need to be a player to run this command!");
                return false;
            }
            return true;
        }
        return false;
    }

    public void resetInv(Player p)
    {
        if(resetItems)
        {
            if(pi.containsKey(p))
                p.getInventory().setContents(pi.get(p));
            pi.remove(p);
        }
        if(resetLoc)
        {
            if(pl.containsKey(p))
                p.teleport(pl.get(p));
            pl.remove(p);
        }
        if(resetHealth)
        {
            if(ph.containsKey(p))
                p.setHealth(ph.get(p));
            ph.remove(p);
        }
    }

    public void pullInv(Player p)
    {
        if(resetItems)
        {
            pi.put(p, p.getInventory().getContents());
            ItemStack[] EmptyStack = new ItemStack[pi.get(p).length];
            p.getInventory().setContents(AdminStack);
        }
        if(resetLoc)
            pl.put(p, p.getLocation());
        if(resetHealth)
            ph.put(p, p.getHealth());

    }

    public void readCfg()
    {
        settings = new Properties();
        try
        {
            settings.load(new FileInputStream("plugins/AdminMode/settings.properties"));

            if(settings.containsKey("resetLoc"))
            {
                resetLoc = Boolean.parseBoolean(settings.getProperty("resetLoc"));
            }
            if(settings.containsKey("resetItems"))
            {
                resetItems = Boolean.parseBoolean(settings.getProperty("resetItems"));
            }
            if(settings.containsKey("resetHealth"))
            {
                resetHealth = Boolean.parseBoolean(settings.getProperty("resetHealth"));
            }

            if(settings.containsKey("items"))
            {
                String[] Items4Admins = settings.getProperty("items").split(",");

                int i = 0;
                for(String ItemStart : Items4Admins)
                {
                    ItemStart.replaceAll("/", "");

                    String[] info = ItemStart.split(":");
                    int itemid = Integer.parseInt(info[0]);
                    int amount = 1;

                    if(info.length > 1)
                    {
                        amount = Integer.parseInt(info[1]);
                    }

                    AdminStack[i] = new ItemStack(itemid, amount);
                    i++;
                }
            } else {
                log.log(Level.INFO, "No items defined.");
            }
        } catch(Exception e)
        {
            String FileContents = "AdminMode properties file";
            
            try
            {
                new File("plugins/AdminMode/").mkdirs();
                settings.setProperty("resetLoc", "true");
                settings.setProperty("resetHealth", "true");
                settings.setProperty("resetItems", "true");
                settings.setProperty("items", "278,1:64");
                settings.store(new FileOutputStream("plugins/AdminMode/settings.properties"), FileContents);
                log.log(Level.INFO, "[" + name + "] Created properties file.");
            } catch (IOException ex)
            {
                log.log(Level.INFO, "[" + name + "] Unable to automatically properties files.");
            }
        }
    }

    public void registerPermissions()
    {
        PluginManager pm = getServer().getPluginManager();

        try
        {
            permh = ((Permissions)pm.getPlugin("Permissions")).getHandler();
            log.log(Level.INFO, "[" + name + "] Permissions enabled for use.");
            log.log(Level.INFO, "[" + name + "] Permissions have priority; being an op with BlastPick will do nothing.");
        } catch(NullPointerException npe)
        {
            permh = null;
        }
    }
}