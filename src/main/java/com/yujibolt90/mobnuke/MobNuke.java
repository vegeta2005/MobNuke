package com.yujibolt90.mobnuke;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;

public class MobNuke extends JavaPlugin implements Listener{
	
	Map<Player, Integer> playersInFallout = new HashMap<Player, Integer>();
	Map<Player, Location> protectedPlayers = new HashMap<Player, Location>();
	Map<Integer, Location> radiatedEntities = new HashMap<Integer, Location>();
	Map<Entity, Location> detonatingEntities = new HashMap<Entity, Location>();
	
	Location loc1 = new Location(null,0,0,0);
	Location loc2 = new Location(null,0,0,0);
	Location blastOrigin = new Location(null,0,0,0);
	
	public float BLAST_POWER = 150.0f;
	
	public int radiationSpread;
	
	private int radiationTime = 0;
	
	@Override
    public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("MobNuke has been enabled!");
        
        ItemStack itemStack = new ItemStack(Material.BLAZE_SPAWN_EGG);
		ItemMeta itemStackMeta = itemStack.getItemMeta();
		itemStackMeta.setDisplayName("warhead");
		
		itemStack.setItemMeta(itemStackMeta);
		
	    NamespacedKey key = new NamespacedKey(this, "warhead");

	    ShapedRecipe recipe = new ShapedRecipe(key, itemStack);

	        
	    recipe.shape(" E ", " T ", " E ");
	    recipe.setIngredient('E', Material.EMERALD_BLOCK);
	    recipe.setIngredient('T', Material.TNT);

	    Bukkit.addRecipe(recipe);
		
		BukkitScheduler scheduler = getServer().getScheduler();
        scheduler.scheduleSyncRepeatingTask(this, new Runnable() {
        	//run this every second
            @Override
            public void run() {
            	//check online players
            	for(Player p : Bukkit.getOnlinePlayers()) {
            		//check if player is in fallout zone
	            	if (isInRect(p.getPlayer(),loc1,loc2)) {
	            		//if in hashmap, do this
	            		
	            		//if wearing netherite armor
	            		if (hasNetheriteArmor(p)==false) {
	            			radiationSideEffects(p);
	            		} else {
	            			getLogger().info(p.getPlayer().getName() + " is protected from fallout.");
	            			if (!(protectedPlayers.containsKey(p))){
	            				p.sendMessage("You are protected from the fallout.");
	            				protectedPlayers.put(p,p.getPlayer().getLocation());
	            			}
	            		}//if player isnt in fallout, remove them from the hashmap
	            	} 	else {
	            			if (playersInFallout.containsKey(p)&protectedPlayers.containsKey(p)) {
		            			protectedPlayers.remove(p);
		            			playersInFallout.remove(p);
		            			p.sendMessage("You are no longer in fallout zone.");
		            		}
	            			if (playersInFallout.containsKey(p)){
	            				playersInFallout.remove(p);
	            				p.sendMessage("You are no longer in fallout zone.");
		            		}
		            		if (protectedPlayers.containsKey(p)){
		            			protectedPlayers.remove(p);
		            			p.sendMessage("You are no longer in fallout zone.");
		            		}
	            	}
            	}
            }
        }, 0L, 20L);
    }
    
    @Override
    public void onDisable() {
    	getLogger().info("MobNuke has been disabled!");
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
	    	if (cmd.getName().equalsIgnoreCase("setblastpower")) { 
	    		
	    		if (args[0] == null) {
	                sender.sendMessage("You must type an integer to change the blast power!");
	                return true;
	            }
	    		
	    		int power = Integer.parseInt(args[0]);
	    		
	    		if (power > 300) {
	                sender.sendMessage("This number is too high! (Max: 300)");
	                return true;
	            }
	    		
	    		if (power < 1) {
	                sender.sendMessage("This number is too low! (Min: 1)");
	                return true;
	            }
	    		
	    		BLAST_POWER = power;
	    		sender.sendMessage( "Power has been changed to: " + args[0]);
	    		return false;
	    	} 
	    	
	    	if (cmd.getName().equalsIgnoreCase("blastpower")) {
	    		sender.sendMessage("Blast power: " + BLAST_POWER);
	    		return false;
	    	}
	    	
	    	if (cmd.getName().equalsIgnoreCase("givenuke")) { 
	    		
	    		Player target = sender.getServer().getPlayer(args[0]);
	    		
	    		ItemStack itemStack = new ItemStack(Material.BLAZE_SPAWN_EGG);
    			ItemMeta itemStackMeta = itemStack.getItemMeta();
    			itemStackMeta.setDisplayName("warhead");
    			
    			itemStack.setItemMeta(itemStackMeta);
	    		
	    		if (target == null) {
	                sender.sendMessage(args[0] + " is not currently online.");
	                return true;
	    		}
	    		
	    		if (sender.getName() != target.getName()) {
	    	        
	    			target.getInventory().addItem(itemStack);
	    			sender.sendMessage( "Gave Nuke to " + args[0] );
	    			target.sendMessage( "You received a Nuclear Warhead from " + sender.getName());
	    			return false;
	    		}
	    		
	    		target.getInventory().addItem(itemStack);
    			sender.sendMessage( "You've been given 1 Nuclear Warhead." );
    			return false;
	    	}
	    	
	    	if (cmd.getName().equalsIgnoreCase("nukehelp")) {
	    		sender.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "-MOB NUKE v0.1 (By yujibolt90)");
	    		sender.sendMessage(ChatColor.GOLD + "/nukehelp " + ChatColor.WHITE + "for a list of commands.");
	    		sender.sendMessage(ChatColor.GOLD + "/blastpower " + ChatColor.WHITE + "to see current blast power.");
	    		sender.sendMessage(ChatColor.GOLD + "/setblastpower <integer> "  + ChatColor.WHITE + "to set the base power of all explosions. (MAX 300; 150 by default)");
	    		sender.sendMessage(ChatColor.GOLD + "/givenuke <player> " + ChatColor.WHITE + "to put a nuke in a players inventory.");
	    		sender.sendMessage(ChatColor.GOLD + "-> Fallout: " + ChatColor.WHITE + "After the blast happens, fallout will spread in all directions.");
	    		sender.sendMessage(ChatColor.GOLD + "-> Resisting Fallout: " + ChatColor.WHITE + "A player wearing full netherite armor will not be affected by fallout!");
	    		sender.sendMessage(ChatColor.GOLD + "-> Crafting: " + ChatColor.WHITE + "Warheads can be crafted in survival with 1 tnt in the middle and 2 emerald blocks (one above the tnt and one below).");
	    		sender.sendMessage(ChatColor.GOLD + "-> Compass: " + ChatColor.WHITE + "Right-clicking a compass will show how far away the user is from the blast point. It will also show how far the fallout has spread from that point in all directions.");
	    		sender.sendMessage(ChatColor.GOLD + "-> Using a Nuclear Warhead: " + ChatColor.WHITE + "Spawn the blaze warhead and hit it once. (Make sure to get to safety quick!)");
	    		return false;
	    	}
		return false;
    }
    
    @EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
    	if (playersInFallout.containsKey(e.getEntity().getPlayer())){
	    	e.setDeathMessage(e.getEntity().getName() + " has died from radiation poisoning.");
	    	e.getEntity().removePotionEffect(PotionEffectType.SLOW);
	    	e.getEntity().removePotionEffect(PotionEffectType.HUNGER);
	    	e.getEntity().removePotionEffect(PotionEffectType.WITHER);
    	}
    }
    
    public void detonatorTick(Entity e, float f) {
    	e.getWorld().playSound(e.getLocation(),Sound.UI_BUTTON_CLICK,5.0f,1.0f);
    	e.setCustomName("DETONATING IN " + f);
    }
    
    public boolean hasNetheriteArmor(Player p) {
    	if (p.getInventory().getHelmet()!=(null)&&p.getInventory().getChestplate()!=(null)&&p.getInventory().getLeggings()!=(null)&&p.getInventory().getBoots()!=(null))
	    	if (p.getPlayer().getInventory().getHelmet().getType().equals(Material.NETHERITE_HELMET)&&p.getPlayer().getInventory().getChestplate().getType().equals(Material.NETHERITE_CHESTPLATE)&&p.getPlayer().getInventory().getLeggings().getType().equals(Material.NETHERITE_LEGGINGS)&&p.getPlayer().getInventory().getBoots().getType().equals(Material.NETHERITE_BOOTS))
			return true;
    	return false;
    }
    
    public void radiationSideEffects(Player p) {
    	if (playersInFallout.containsKey(p.getPlayer())) {
			Player player = p.getPlayer();
			
			playersInFallout.put(p, playersInFallout.get(p)+1);
    		
	    	getLogger().info(p.getPlayer().getName() + " is caught in the fallout!");
	    	switch (playersInFallout.get(p)) {
	    	case 1:
	    		player.sendMessage("You've been exposed to high levels of radiation. Find shelter quickly...");
	    		getLogger().info("Player has radiation poisoning");
	    		player.playSound(player.getLocation(),Sound.ENTITY_GHAST_HURT,BLAST_POWER*1,0.1f);
	    		break;
	    	case 20:
	    		player.sendMessage("You have radiation poisoning...");
	    		getLogger().info("Stage 2");
	    		player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW,2000,3));
	    		player.playSound(player.getLocation(),Sound.ENTITY_GHAST_HURT,BLAST_POWER*1,0.1f);
	    		break;
	    	case 40:
	    		player.sendMessage("Your radiation poisoning worsens...");
	    		getLogger().info("Stage 3");
	    		player.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER,2000,10));
	    		player.playSound(player.getLocation(),Sound.ENTITY_GHAST_HURT,BLAST_POWER*1,0.1f);
	    		break;
	    	case 60:
	    		player.sendMessage("Your organs slowly fail...");
	    		getLogger().info("Stage 4");
	    		player.addPotionEffect(new PotionEffect(PotionEffectType.WITHER,2000,3));
	    		player.playSound(player.getLocation(),Sound.ENTITY_GHAST_HURT,BLAST_POWER*1,0.1f);
	    		break;
	    	}
	    }	else //if not, put them in the hashmap
	    	playersInFallout.put(p, 0);
    }
    
    @EventHandler
    public void onExplosionDamage(EntityDamageEvent event) {
    	if (!(event.getCause().equals(DamageCause.BLOCK_EXPLOSION)))
    		return;
    	Entity entity = event.getEntity();
    	entity.setFireTicks(300);
    	
    	if (!entity.getType().equals(EntityType.PLAYER)){
    		getLogger().info("Entities have been radiated");
    		entity.setCustomName("Radiated Creature");
    		radiatedEntities.put(entity.getEntityId(), entity.getLocation());
    	} else getLogger().info("only player entities");
    }
    
    @EventHandler
    public void useCompass(PlayerInteractEvent event) {
    	Player player = event.getPlayer();
    	
    	if (player.getInventory().getItemInMainHand().getType().equals(Material.COMPASS)){
    			player.setCompassTarget(blastOrigin);
    			player.sendMessage("You are " + (int)player.getLocation().distance(blastOrigin) + " block(s) away from the blast origin.");
    			player.sendMessage("Fallout has spread " + radiationSpread + " block(s).");
    	}
    }
    
    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
    	if (radiatedEntities.containsKey(event.getEntity().getEntityId())) {
    		ItemStack itemStack = new ItemStack(Material.ROTTEN_FLESH);
    		ItemMeta itemStackMeta = itemStack.getItemMeta();
    		itemStackMeta.addEnchant(Enchantment.VANISHING_CURSE, 1, true);
    		itemStackMeta.setDisplayName("Radiated Flesh");
    		
    		itemStack.setItemMeta(itemStackMeta);
	    	event.getDrops().clear();
	    	event.getDrops().add(itemStack);
    	}
    }
    
    public boolean isInRect(Entity e, Location loc1, Location loc2)
    {
        double[] dim = new double[2];
     
        dim[0] = loc1.getX();
        dim[1] = loc2.getX();
        Arrays.sort(dim);
        if(e.getLocation().getX() > dim[1] || e.getLocation().getX() < dim[0])
            return false;
     
        dim[0] = loc1.getY();
        dim[1] = loc2.getY();
        Arrays.sort(dim);
        if(e.getLocation().getY() > dim[1] || e.getLocation().getY() < dim[0])
            return false;
        
        dim[0] = loc1.getZ();
        dim[1] = loc2.getZ();
        Arrays.sort(dim);
        if(e.getLocation().getZ() > dim[1] || e.getLocation().getZ() < dim[0])
            return false;
     
        /*TODO same thing with y*/
     
        return true;
    }
    
    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
    	if ((event.getDamager() instanceof Player) & !(event.getEntity() instanceof Player) & (event.getEntityType().equals(EntityType.BLAZE)) & (event.getEntity().getName().equalsIgnoreCase("warhead")) & !(detonatingEntities.containsKey(event.getEntity()))) {
    		
    		detonatingEntities.put(event.getEntity(), event.getEntity().getLocation());
    		
	    	Entity entity = (Entity) event.getEntity();
	    	Player player = (Player) event.getDamager();
	    	
	    	entity.setInvulnerable(true);
			entity.setCustomNameVisible(true);
	    	
	    	CountdownTimer timer = new CountdownTimer(MobNuke.getPlugin(MobNuke.class),
	    	        10,	//Start
	    	        () -> getLogger().info("Initializing detonation..."),
	    	        () -> {//Finish
	    	        	//Create an explosion
	    	        	detonatingEntities.remove(event.getEntity());
	    	        	getLogger().info("Entity Detonated!");
	    	        	blastOrigin = entity.getLocation();
	    	            entity.setInvulnerable(false);
	    	            entity.getWorld().playSound(entity.getLocation(),Sound.ENTITY_GENERIC_EXPLODE,BLAST_POWER*5,0.1f);
	    	    		entity.getWorld().createExplosion(entity.getLocation(), BLAST_POWER, true);
	    	    		
//	    	    		if (BLAST_POWER>50)
	    	    		player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS,60,1));
	    	    		 //Create Fallout
	    	    		Fallout fallout = new Fallout(blastOrigin.getX(), blastOrigin.getY(), blastOrigin.getZ(),1,1);
	    	    		
	    	    		//first location
	    	    		loc1.setWorld(entity.getWorld());
	    	    		fallout.x1=(blastOrigin.getX()+(double)BLAST_POWER/2);
	    	    		fallout.y1=(blastOrigin.getY()+4);
	    	    		fallout.z1=(blastOrigin.getZ()+(double)BLAST_POWER/2);
	    	    		
	    	    		//second location
	    	    		loc2.setWorld(entity.getWorld());
	    	    		fallout.x2=(blastOrigin.getX()-(double)BLAST_POWER/2);
	    	    		fallout.y2=(blastOrigin.getY()-4);
	    	    		fallout.z2=(blastOrigin.getZ()-(double)BLAST_POWER/2);
	    	    		
	    	    		loc1.setWorld(entity.getWorld());
	    	    		loc1.setX(fallout.x1);
	    	    		loc1.setY(fallout.y1);
	    	    		loc1.setZ(fallout.z1);
	    	    		
	    	    		//second location
	    	    		loc2.setWorld(entity.getWorld());
	    	    		loc2.setX(fallout.x2);
	    	    		loc2.setY(fallout.y2);
	    	    		loc2.setZ(fallout.z2);
	    	    		//create fallout timer
	    	    		CountdownTimer falloutTimer = new CountdownTimer(MobNuke.getPlugin(MobNuke.class),
	    		    	        BLAST_POWER/2,
	    		    	        () -> {//Start
	    		    	        	//Add to hasmap
	    		    	        	radiationSpread = (int)BLAST_POWER/2;
	    		    	        	getLogger().info("Fallout has begun spreading...");
	    		    	        },
	    		    	        () -> {
	    		    	        	//Finish
	    		    	        	
	    		    	        	loc1.setX(fallout.x1);
	    		    	    		loc1.setY(fallout.y1);
	    		    	    		loc1.setZ(fallout.z1);
	    		    	    		
	    		    	    		loc2.setX(fallout.x2);
	    		    	    		loc2.setY(fallout.y2);
	    		    	    		loc2.setZ(fallout.z2);
	    		    	        	
	    		    	        	radiationTime += 1;
	    		    	        	getLogger().info("Fallout has finished spreading");
	    		    	        },
	    		    	        (t) -> {//Do every second
	    		    	        	//Spread fallout
	    		    	        	fallout.increaseFallout();
	    		    	        	radiationSpread++;
	    		    	        	
	    		    	        	loc1.setX(fallout.x1);
	    		    	    		loc1.setY(fallout.y1);
	    		    	    		loc1.setZ(fallout.z1);
	    		    	    		
	    		    	    		loc2.setX(fallout.x2);
	    		    	    		loc2.setY(fallout.y2);
	    		    	    		loc2.setZ(fallout.z2);
	    		    	    		
	    		    	        	getLogger().info("Fallout has grown by 1. Currently " + radiationSpread + " block(s).");
	    		    	        }
	    		    	);	falloutTimer.scheduleTimer(); //start timer
	    	        },
	    	        (t) -> detonatorTick(entity, t.getSecondsLeft()) //Do every second
	    		);	timer.scheduleTimer(); //start timer
	    }
    }
}