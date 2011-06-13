package me.pwnage.bukkit.AdminMode;

import java.util.logging.Level;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerQuitEvent;

public class amPlayer extends PlayerListener
{
    private AdminMode plugin;
    public amPlayer(AdminMode p)
    {
        // TODO: Understand the PLAYER_QUIT hook and add in autorestore of items / inventory.
        plugin = p;
    }

    @Override
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        Player p = event.getPlayer();
        if(plugin.pi.containsKey(p))
        {
            plugin.resetInv(p);
            plugin.log.log(Level.INFO, "[" + plugin.name + "] " + event.getPlayer().getName() + "'s inventory has been returned.");
        }
    }

    @Override
    public void onPlayerQuit(PlayerQuitEvent event)
    {
        Player p = event.getPlayer();
        if(plugin.pi.containsKey(p))
        {
            plugin.resetInv(p);
            plugin.log.log(Level.INFO, "[" + plugin.name + "] " + event.getPlayer().getName() + "'s inventory has been returned.");
        }
    }
}
