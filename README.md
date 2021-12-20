# Eaglercraft

![eaglercraft](https://cdn.discordapp.com/attachments/378764518081429506/921932182484836352/readmeeee.png)

### Official Demo URL: [https://g.eags.us/eaglercraft/](https://g.eags.us/eaglercraft/)

### Download Locally: [stable-download/Offline_Download_Version.html](https://github.com/LAX1DUDE/eaglercraft/raw/main/stable-download/Offline_Download_Version.html)

Note that the server may be down - if you want a gameplay demo download the files from here [https://github.com/LAX1DUDE/eaglercraft/tree/main/stable-download](https://github.com/LAX1DUDE/eaglercraft/tree/main/stable-download) and host the page locally yourself

## What is Eaglercraft?

Eaglercraft is real Minecraft 1.5.2 that you can play in any regular web browser. That includes school chromebooks, it works on all chromebooks. You can join real Minecraft 1.5.2 servers with it through a custom proxy based on Bungeecord.

## How does it work?

Eaglercraft uses the decompiled source of the official version of Minecraft 1.5.2 direct from Mojang decompiled by [MCP](http://www.modcoderpack.com/) and compiled to Javascript using [TeaVM](https://teavm.org/). Therefore it can join any Minecraft 1.5.2 server, as it is really running (a modified version of) Minecraft 1.5.2 in the browser. However, due to CORS restrictions it must use a modified version of Bungeecord which proxies the browser's Websocket connection to the pure TCP connection used by Minecraft. For graphics, a custom compatibility layer created by me allows the fixed function OpenGL 1.3 based rendering engine mojang uses to operate through an HTML5 WebGL canvas with minimal changes to the source.

## Installing

If you want to use this project but don't want to compile it from scratch, download [stable-download/stable-download.zip](https://github.com/LAX1DUDE/eaglercraft/raw/main/stable-download/stable-download.zip) and extract

Within stable-download.zip there is a 'java' and a 'web' folder. Upload the contents of the web folder to your web server. **Eaglercraft will not work if it is opened locally via file:///, it needs to be on an http:// or https:// page. Try [this extention](https://chrome.google.com/webstore/detail/web-server-for-chrome/ofhbbkphhbklhfoeikjpcbhemlocgigb/) if you are on chrome.** The eaglercraft bungeecord executable is in the java/bungee_command folder along with the sample configuration file and a run.bat script to launch it. CraftBukkit for minecraft 1.5.2 configured to work with the eaglercraft bungee executable is in java/bukkit_command. This version of minecraft's Spigot build doesn't work with bungee so you are limited to CraftBukkit plugins only on your server, unless you set up a newer version of Spigot and install ViaVersion or whatever it's called to make it backwards compatible.

To play the game, launch the run.bat script in both the bungee_command and bukkit_command folders. Then navigate to the URL where the contents of the web folder ended up. The game should load without any issues. Go to the Multiplayer screen and select 'Direct Connect'. **Type 127.0.0.1:25565.** Press connect or whatever and enjoy, the default port configured in the bungeecord config.yml is 25565 instead of 80 to avoid any potential conflict with the local web server or the OS (and linux desktop users can't use port numbers under 1024 without sudo).

**The default behavior in Eaglercraft if no :port is provided when connecting to a server is to use port 80, not port 25565. This is so the game's websocket in a production environment does not get blocked by any of the clients' firewalls. Also this enables you to use Cloudflare and nginx to create reverse proxy connections on the site to host multiple servers on the same domain.**

If you want SSL, set up [nginx](https://www.nginx.com/) as a reverse proxy from port 443 to the port on the bungeecord server. You can very easily configure SSL on an nginx virtual host when it is in proxy mode, much more easily than you could if I created my own websocket SSL config option in bungee. To connect to a server running an SSL websocket on the multiplayer screen, use this format: `wss://[url]/`. You can also add the :port option again after the domain or ip address at the beggining of the URL to change the port and connect with SSL. **If you set up the Eaglercraft index.html on an https:// URL, Chrome will only allow you to make wss:// connections from the multiplayer screen. It is an security feature in Chrome, if you want to support both ws:// and wss:// you have to host the Eaglercraft index.html on an http:// URL**. The best advice I have for security is to use Cloudflare to proxy both the site and the websocket, because you can use http and ws on your servers locally and then you can configure cloudflare to do the SSL for you when the connections are proxied. And it conceils your IP address to the max and you can also set up a content delivery network for the big assets.epk and classes.js files all for free on their little starter package

**To change the default servers on the server list, see the base64 in the javascript at line 8 of [stable-download/web/index.html](https://github.com/LAX1DUDE/eaglercraft/tree/main/stable-download/web/index.html). Copy and decode the base64 in the quotes using [base64decode.org](base64decode.org) and open the resulting file with NBTExplorer (the minecraft one). You will see the list of default servers in a 'servers' tag stored as NBT components, and you can edit them and add more as long as you follow the same format the existing servers have. When you're done, encode the file back to base64 using [base64encode.org](base64encode.org) and replace the base64 between the quotes on line 8 in index.html with the new base64 from base64encode.org.**

There is a plugin hard coded into the bungeecord server to synchronize skins, and also a plugin like authme for creating a lobby and authentication. Configure it like this in bungee:

    authservice:
      authfile: passwords.yml
      enabled: true
      limbo: lobby
      timeout: 30

Can't remember much on this one, so you're gonna have to do some experimentation

## Compiling

To compile for the web, run the gradle 'teavm' compile target to generate the classes.js file.

To complile to regular desktop Java for quick debugging, using native OpenGL for rendering instead of WebGL:
- Create a new empty eclipse project
- Link the src/main/java and src/lwjgl/java as source folders and add the jars in lwjgl-rundir as dependencies
- Create a run configuration and add a jvm argument pointing to the lwjgl natives folder (lwjgl-rundir/natives) like this: `-Djava.library.path=natives`, and make sure the working directory for the run configuration is the lwjgl-rundir folder.


To modify the game's assets repository (javascript/assets.epk), make your changes in lwjgl-runtime/resources/ and use the Eclipse project located in epkcompiler/ to regenerate the assets.epk file and copy it to the Javascript directory. 

this project is just a proof of concept to show what can be accomplished when using TeaVM to cross compile an existing java program to javascript. It is not very fast or stable, and the only real useful portion is the emulator code which creates a makeshift fixed function OpenGL 1.3 context using webgl (based on OpenGL 3.3) operational in the browser. Maybe it can be used to port other games in the future.
