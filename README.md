# CuriousProductions.GameManager
Copyright (c) 2020 Gabriella Hotten (see license in LICENSE.md)

Game Manager API for any CPS games.

**Setup**

If you haven't already, please follow the basic setup guide at https://github.com/IsGabriellaCurious/CuriousProductions.Root

To create a game, all you need (as a minimum) is a main plugin class and a game class. The game class should extend `cpsGame`. And then just follow out everything it asks you to do. Then hop back over to your plugin class and initalize the Game Manager by doing the following: `GameManager gameManager = new GameManager(this, new YourGameClass(your params));` and Game Manager will handle the rest!

I will be adding an example class soon to help understand the basic setup of a game.

**List of Available Modules** (and the order they should be enabled in, yes this matters)
- Network Data Hub*
- Command Hub*
- Redis Hub*
- Account Hub*
- Proxy Manager*
- Punish Manager*
- GM Chat Hub
- Staff Hub*
- Socreboard Centre*
- Server Manager*
- Stat Manager*
- Game Manager*

*This is a **core** module and **must be enabled**.
