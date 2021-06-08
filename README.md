# MobNuke
/**
 * Adds nuclear warefare to your Minecraft Server!
 * <b>The download can be found in the "target" folder</b>
 * Project created by @author Yujibolt90
 */


Commands:
/nukehelp for a list of commands.
/blastpower for the current blast power (default 150).
/setblastpower to set the blast power (max 300).
/givenuke to give a nuke to the target player.


Crafting:
-A player can craft a warhead in survival with: 1 tnt in the center and 2 emerald blocks (one above the tnt and one below the tnt).

Compass: The player can use a compass to measure the exact distance between themself and the blast point.
(they can also see how many blocks the fallout has spread in all directions)

Fallout:
-When a warhead is detonated, fallout will spread in all directions from the blast point for a duration (BlastPower divided by 2).
-Players will experience stages of health effects based on how long they are exposed to fallout radiation.
Stage 0 - Warning message
Stage 1 - Sluggishness (Slowness III)
Stage 2 - Nausea (Hunger X)
Stage 3 - Organ Failure (Wither III)
Stage 4 - Death
(Players wearing full netherite armor are immune to fallout radiation!)
*Fallout spreads at a speed of 1 block per second

Using a Warhead:
-Right-click the warhead against the ground to spawn a blaze warhead. Attack the blaze one time (killing it doesn't matter) to set off the nuke.
-A warhead will detonate after 10 seconds.
-Any entity caught in the warhead's blast will be set on fire for 10 seconds

