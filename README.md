# Eaglercraft

[Live demo](https://games.nx-eags.tk/minecraft_demo/)

Note that the server may be down - if you still want an in-game demo compile the modified Bungeecord server found in `eaglercraftbungee` and connect it to a Minecarft 1.5.2 server.

## What is Eaglercraft?

Eaglercraft is real Minecraft 1.5.2 that you can play in any regular web browser. That includes school chromebooks, it works on all chromebooks. You can join real Minecraft 1.5.2 servers with it through a custom proxy based on Bungeecord.

## How does it work?

Eaglercraft uses the decompiled source of the official version of Minecraft 1.5.2 direct from Mojang decompiled by [MCP](http://www.modcoderpack.com/) and compiled to Javascript using [TeaVM](https://teavm.org/). Therefore it can join any Minecraft 1.5.2 server, as it is really running (a modified version of) Minecraft 1.5.2 in the browser. However, due to CORS restrictions it must use a modified version of Bungeecord which proxies the browser's Websocket connection to the pure TCP connection used by Minecraft. For graphics, a custom compatibility layer created by me allows the fixed function OpenGL 1.3 based rendering engine mojang uses to operate through an HTML5 WebGL canvas with minimal changes to the source.

## Compiling

To compile for the web, run the gradle 'teavm' compile target to generate the classes.js file.

To complile to regular desktop Java for quick debugging, using native OpenGL for rendering instead of WebGL:
- Create a new empty eclipse project
- Link the src/main/java and src/lwjgl/java as source folders and add the jars in lwjgl-rundir as dependencies
- Create a run configuration and add a jvm argument pointing to the lwjgl natives folder (lwjgl-rundir/natives) like this: `-Djava.library.path=natives`, and make sure the working directory for the run configuration is the lwjgl-rundir folder.


To modify the game's assets repository (javascript/assets.epk), make your changes in lwjgl-runtime/resources/ and use the Eclipse project located in epkcompiler/ to regenerate the assets.epk file and copy it to the Javascript directory. 


this project is just a proof of concept to show what can be accomplished when using TeaVM to cross compile an existing java program to javascript. It is not very fast or stable, and the only real useful portion is the emulator code which creates a makeshift fixed function OpenGL 1.3 context using webgl (based on OpenGL 3.3) operational in the browser. Maybe it can be used to port other games in the future.
