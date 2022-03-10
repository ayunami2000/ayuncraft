# Eaglercraft

![eaglercraft](https://cdn.discordapp.com/attachments/378764518081429506/932053915061587978/thumbnail2.png)

### Official Demo URL: [https://g.eags.us/eaglercraft/](https://g.eags.us/eaglercraft/)

### Download Locally: [stable-download/Offline_Download_Version.html](https://github.com/LAX1DUDE/eaglercraft/raw/main/stable-download/Offline_Download_Version.html)

(right click the link and press 'Save link as...' to download the file)

**Here is a 50 minute source walkthrough: [https://youtu.be/QvHvSX4Th84](https://youtu.be/QvHvSX4Th84)**

Note that the server may be down - if you want a gameplay demo download the files from here [https://github.com/LAX1DUDE/eaglercraft/tree/main/stable-download](https://github.com/LAX1DUDE/eaglercraft/tree/main/stable-download) and host the page locally yourself

### Visit this site for a list of servers: [https://g.eags.us/eaglercraft/servers/](https://g.eags.us/eaglercraft/servers/)

**For any questions you can join the discord server and hit me up there [https://discord.gg/KMQW9Uvjyq](https://discord.gg/KMQW9Uvjyq)**

## What is Eaglercraft?

Eaglercraft is real Minecraft 1.5.2 that you can play in any regular web browser. That includes school chromebooks, it works on all chromebooks. You can join real Minecraft 1.5.2 servers with it through a custom proxy based on Bungeecord.

## Singleplayer?

I successfully created Singleplayer for this version of eaglercraft and it works 100%, **but after many rounds of very thorough testing**, I found that TeaVM is unable to optimize certain aspects of terrain generation and world ticking as well as it can optimize the rendering for a multiplayer-only build. On an [i9-11900K]( https://www.cpubenchmark.net/cpu.php?cpu=Intel+Core+i9-11900K+%40+3.50GHz&id=3904) a render distance of 'Tiny' struggles to pass 12 TPS while standing still, and drops below 1 TPS as soon as a couple new chunks have to be generated and trigger lighting updates. The playerbase of this game **will bother me every f\*\*king day if what I release as singleplayer is not perfect** and therefore singleplayer will remain private indefinetly and I will not answer any further questions about it or share the source code unless you are just looking to repurpose some of the base OS emulation code.

**Minecraft Alpha singleplayer will be ported in the coming weeks but that is not a promise I should be obligated to fulfill**

## How does it work?

Eaglercraft uses the decompiled source code of the official build of Minecraft 1.5.2 direct from Mojang. It is decompiled by [MCP](http://www.modcoderpack.com/) and then recompiled to Javascript using [TeaVM](https://teavm.org/). Therefore it can join real Minecraft 1.5.2 servers, as it is really running Minecraft 1.5.2 in the browser. However, due to security limitations in modern browsers, it must use javascript Websocket objects for multiplayer instead of direct TCP connections to it's servers. A modified version of Bungeecord is included with Eaglercraft which accepts browser HTTP Websocket connections from Eaglercraft clients and unwraps the streams internally to regular TCP so they can be forwarded to regular Bukkit servers with no plugins. For graphics, a custom GPU compatibility layer allows Mojang's fixed function OpenGL 1.3 based rendering engine to render directly to an HTML5 WebGL 2.0 canvas on the page with minimal changes to the source, preserving the game's graphics to look exactly the same as desktop vanilla Minecraft 1.5.2.

## Issues?

I got tired of closing duplicate 'how to maek sever' and 'add single player' issues almost every day so I disabled it because honestly I don't really care anymore, [join discord](https://discord.gg/KMQW9Uvjyq) if you've got an issue to report that you are confident can be backed up with source code

## Installing

If you want to use this project but don't want to compile it from scratch, download [stable-download/stable-download.zip](https://github.com/LAX1DUDE/eaglercraft/raw/main/stable-download/stable-download.zip) and extract

Within stable-download.zip there is a 'java' and a 'web' folder. Upload the contents of the web folder to your web server. **The web folder will not work if it is opened locally via file:///, it needs to be opened on an http:// or https:// page. Try [this extention](https://chrome.google.com/webstore/detail/web-server-for-chrome/ofhbbkphhbklhfoeikjpcbhemlocgigb/) if you are on chrome or if that's not possible then download the alternative single-file html [offline version](https://github.com/LAX1DUDE/eaglercraft/raw/main/stable-download/Offline_Download_Version.html) that does work on file URLs.** If you use this alternative version, please make sure you and your peers keep your copies up to date by regularly downloading any newer versions of the html file at [this link](https://github.com/LAX1DUDE/eaglercraft/blob/main/stable-download/Offline_Download_Version.html) to avoid getting stuck with a version that has a game-breaking glitch or mistake. The eaglercraft bungeecord executable is in the java/bungee_command folder along with the sample configuration file and a run.bat script to launch it. CraftBukkit for minecraft 1.5.2 configured to work with the eaglercraft bungee executable is in java/bukkit_command. The available version of Spigot 1.5.2 has a bug when used with bungee so you are limited to CraftBukkit and CraftBukkit plugins only on your servers

To play the game, launch the run.bat script in both the bungee_command and bukkit_command folders. Then navigate to the URL where the contents of the web folder ended up. The game should load without any issues. Go to the Multiplayer screen and select 'Direct Connect'. **Type 127.0.0.1:25565.** Press connect or whatever and enjoy, the default port configured in the bungeecord config.yml is 25565 instead of 80 to avoid any potential conflict with the local web server or the OS (and linux desktop users can't use port numbers under 1024 without sudo).

**The default behavior in Eaglercraft if no :port is provided when connecting to a server is to use port 80, not port 25565. This is so the game's multiplayer connections in a production environment do not default to a port that is currently blocked by any firewalls. Also this enables you to use Cloudflare and nginx to create reverse proxy connections on your site to host multiple servers on the same domain using different ws:// URLs for each socket.**

If you want SSL, set up [nginx](https://www.nginx.com/) as a reverse proxy from port 443 to the port on the bungeecord server. You can very easily configure SSL on an nginx virtual host when it is in proxy mode, much more easily than you could if I created my own websocket SSL config option in bungee. To connect to a server running an SSL websocket on the multiplayer screen, use this format: `wss://[url]/`. You can also add the :port option again after the domain or ip address at the beggining of the URL to change the port and connect with SSL. **If you set up the Eaglercraft index.html on an https:// URL, Chrome will only allow you to make wss:// connections from the multiplayer screen. It is a security feature in Chrome, if you want to support both ws:// and wss:// you have to host the Eaglercraft index.html on an http:// URL**. The best advice I have for security is to use Cloudflare to proxy both the site and the websocket, because you can use http and ws on your servers locally and then you can configure cloudflare to do the SSL for you when the connections are proxied. And it conceils your IP address to the max and you can also set up a content delivery network for the big assets.epk and classes.js files all for free on their little starter package

**To change the default servers on the server list, download [stable-download/servers_template.dat](https://github.com/LAX1DUDE/eaglercraft/raw/main/stable-download/servers_template.dat) and open the file with NBTExplorer (the minecraft one). You will see the list of default servers in a 'servers' tag stored as NBT components, and you can edit them and add more as long as you follow the same format the existing servers have. When you're done, save the file and encode the file back to base64 using the upload option on [base64encode.org](base64encode.org), then download the encoded file and open it and replace the base64 between the quotes on line 8 of your index.html with the new base64 in the encoded file you downloaded.**

There is a plugin hard coded into the bungeecord server to auto synchronize the eaglercraft profile skins between players and worlds

You should probably use a plugin like [AuthMe](https://dev.bukkit.org/projects/authme-reloaded/files/682502) to keep griefers from logging in to other people's profiles

## Compiling

To compile for the web, run the gradle 'teavm' compile target to generate the classes.js file.

To complile to regular desktop Java for quick debugging, using native OpenGL for rendering instead of WebGL:
- Create a new empty eclipse project
- Link the src/main/java and src/lwjgl/java as source folders and add the jars in lwjgl-rundir as dependencies
- Create a run configuration and add a jvm argument pointing to the lwjgl natives folder (lwjgl-rundir/natives) like this: `-Djava.library.path=natives`, and make sure the working directory for the run configuration is the lwjgl-rundir folder.


To modify the game's resource pack (javascript/assets.epk), view the readme in the [/epkcompiler](https://github.com/LAX1DUDE/eaglercraft/tree/main/epkcompiler) directory

this project is just a proof of concept to show what can be accomplished when using TeaVM to cross compile an existing java program to javascript. It is not very fast or stable, and the only real useful portion is the emulator code which creates a makeshift fixed function OpenGL 1.3 context using webgl (based on OpenGL 3.3) operational in the browser. Maybe it can be used to port other games in the future.

## Contributing

All I really have to say is, tabs not spaces, and format the code to be like the eclipse auto format tool on factory settings, but also run-on lines of code long enough to go off the screen and single line if statements and other format violations in that category are welcome if it helps enhance the contrast between the less important code and the more important code in a file. Don't commit changes to `javascript/classes.js` or `javascript/assets.epk` or anything in `stable-download/`. I'll recompile those myself when I merge the pull request.
