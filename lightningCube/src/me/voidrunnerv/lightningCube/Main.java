package me.voidrunnerv.lightningCube;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

/*

Lightning:

Uses command (GUI possibly?) and summons lightning in a cube where each side is x blocks long.
X is entered by the user, of course :)

Adpats to the terrain. If the terrain is higher or lower than the player it'll strike where
it would if it was natural lightning.
 
 */

public class Main extends JavaPlugin implements CommandExecutor{
	
	//I'll start by defining the structure for the code
	//Then i'll make the summonLightning() function
	
	//A variable we'll need laters
	int iteration = 1;
	
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		if(label.equalsIgnoreCase("lightning") && sender instanceof Player) {
			Player player = (Player) sender;
			if(args.length < 1) {
				player.sendMessage("Please include how big you'd like the cube to be");
			}
			
			lightningCube(player, Double.parseDouble(args[0]));
		}
		
		return false;
	}
	
	void summonLightning(Player player, Location coords) {
		//Summons lightning At Coordinates
		
		World playerWorld = player.getWorld();
		
		//We want to make sure the block above the location is air, and that the location isn't air
		
		if(playerWorld.getBlockAt(coords).getType() == Material.AIR || playerWorld.getBlockAt(coords.add(0,1,0)).getType() != Material.AIR) {
			coords = findBlockToLightning(player, coords);
		}
		
		//Note: Can use strikeLightningEffect() if you don't want it to do damage or cause fire I think.
		playerWorld.strikeLightning(coords);
	}
	
	void lightningCube(Player player, double x) {
		
		World playwerWorld = player.getWorld();
		
		//Calculate coords

		Location playerCoords = player.getLocation();
		
		//Calc top left bot and right coords
		
		int pCX = playerCoords.getBlockX();
		int pCY = playerCoords.getBlockY();
		int pCZ = playerCoords.getBlockZ();
		
		Location top = new Location(player.getWorld(), pCX+x,pCY,pCZ);
		Location bot = new Location(player.getWorld(), pCX-x,pCY,pCZ);
		
		Location left = new Location(player.getWorld(), pCX,pCY,pCZ+x);
		Location rightification = new Location(player.getWorld(), pCX,pCY,pCZ-x);
		
		bot.add(-x,0,0);
		
		left.add(0,0,x);
		rightification.add(0,0,-x);
		
		//Now that we know the coordinates for the center of each of the 4 sides of our "Cube" (it's a square but cube sounds cool ok??????)
		//We just have to fill in all of em with lightning. Watch this, for loop magic, boyos	
		
		for(int i = 0; i < x; i++) {
			summonLightning(player, new Location(playwerWorld, top.getX(),top.getY(),top.getZ()+i));
			summonLightning(player, new Location(playwerWorld, top.getX(),top.getY(),top.getZ()-i));
		}
		
		for(int i = 0; i < x; i++) {
			summonLightning(player, new Location(playwerWorld, bot.getX(),bot.getY(),bot.getZ()+i));
			summonLightning(player, new Location(playwerWorld, bot.getX(),bot.getY(),bot.getZ()-i));
		}
		
		for(int i = 0; i < x; i++) {
			summonLightning(player, new Location(playwerWorld, left.getX()+i,left.getY(),left.getZ()));
			summonLightning(player, new Location(playwerWorld, left.getX()-i,left.getY(),left.getZ()));
		}
		
		for(int i = 0; i < x; i++) {
			summonLightning(player, new Location(playwerWorld, rightification.getX()+i,rightification.getY(),rightification.getZ()));
			summonLightning(player, new Location(playwerWorld, rightification.getX()-i,rightification.getY(),rightification.getZ()));
		}
		
	}

	Location findBlockToLightning(Player player, Location location) {
		
		Location newLocation = location;
		
		//Determine if the lightning should be more up or down.
		//Up: Block above lightning isn't air
		//Down: Block on lightning is air
		//Possible that the block above the coords is air and the block at the coords isn't air? yes but that would mean it's already the right coords :)!
		
		Boolean up = true;
		
		//Don't need to check for the block above lightning not being air because we are checking below and confirming with the found varaible below, and if neither of these are true then it'll default to above because that's how we got here in the first place lol
		if(location.getBlock().getType() == Material.AIR) up = false;
		
		//This will be true if the lightning is already at good spot!
		Boolean found = (location.getBlock().getType() != Material.AIR && location.add(0,1,0).getBlock().getType() == Material.AIR);
		while(!found) {
			if(up) {
				newLocation = newLocation.add(0,1,0);
				found = location.add(0,1,0).getBlock().getType() == Material.AIR;
			} else {
				newLocation = newLocation.add(0,-1,0);
				found = location.getBlock().getType() != Material.AIR;
			}
		}
		
		
		return newLocation;
	}
}
