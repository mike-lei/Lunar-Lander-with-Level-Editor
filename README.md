# Lunar Lander Game with Level Editor

The player guides a spaceship to a designated landing area using WASD. If the spaceship lands with a speed faster than the limit, or the spaceships hits the terrain, then landing fails and the player needs to try again. There is a fuel limit: once fuel runs out, the spaceship starts to fall and the player has no way to stop it.

The game also has a level editor: the player can edit the terrain, the position of the landing area and the initial position of the spaceship by dragging them. The level editor supports undo/redo feature.

## Main Interface

![Screenshot](1.jpg)

Status Bar - 

1) Fuel - It reduces whenever players press W/A/S/D, when it reaches 0, the spaceship is out of control and starts to fall.
2) Speed - Green font when the speed is safe for landing.
3) Paused - Players can press SPACE to pause or resume the game.
4) LANDED/CRASH - Displayed when the spaceship safely lands on the pad or crashes.

Play Area - 

Where players play this game.

Level Editor - 

Only available when the game is paused. It can change everything in the play area.

## Level Editor

![Screenshot](edited.jpg)

There is a level editor with undo/redo feature in this game. Players can simply drag the terrain, spaceship and landing pad to change the difficulty. The change is reflected on the game area immediately.

## Successfully Landed

![Screenshot](landed.jpg)

If the spaceship touches the landing pad with a small velocity, the status bar displays a "LANDED". Players can press SPACE to restart the game.
