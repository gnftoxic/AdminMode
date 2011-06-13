package me.pwnage.bukkit.AdminMode;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.entity.EntityTargetEvent;

public class amEntity extends EntityListener
{
    private AdminMode plugin;
    public amEntity(AdminMode p)
    {
        plugin = p;
    }

    @Override
    public void onEntityDamage(EntityDamageEvent event)
    {
        if(event.getEntity() instanceof Player)
        {
            if(plugin.pi.containsKey((Player)event.getEntity()))
            {
                event.setCancelled(true);
                return;
            }
        }
    }

    @Override
    public void onEntityTarget(EntityTargetEvent event)
    {
        if(event.getTarget() instanceof Player)
        {
            Player p = (Player)event.getTarget();

            if(plugin.pi.containsKey(p))
            {
                event.setCancelled(true);
                return;
            }
        }
    }
}
