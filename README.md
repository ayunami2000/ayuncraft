# Eaglercraft

![eaglercraft](https://cdn.discordapp.com/attachments/378764518081429506/932053915061587978/thumbnail2.png)

### Official Demo URL: [https://g.eags.us/eaglercraft/](https://g.eags.us/eaglercraft/)

### Download Locally: [stable-download/Offline_Download_Version.html](https://github.com/LAX1DUDE/eaglercraft/raw/main/stable-download/Offline_Download_Version.html)

(right click the link and press 'Save link as...' to download the file)

### Visit this site for a list of servers: [https://g.eags.us/eaglercraft/servers/](https://g.eags.us/eaglercraft/servers/)

**For any questions you can join the discord server and hit me up there [https://discord.gg/KMQW9Uvjyq](https://discord.gg/KMQW9Uvjyq)**

## What is Eaglercraft?

Eaglercraft is real Minecraft 1.5.2 that you can play in any regular web browser. That includes school chromebooks, it works on all chromebooks. You can join real Minecraft 1.5.2 servers with it through a custom proxy based on Bungeecord.

## How to make a server

### If replit is acceptable, you can use this: [https://replit.com/@ayunami2000/eaglercraft-server](https://replit.com/@ayunami2000/eaglercraft-server)

**Manual setup instructions:**
1. **Check if Java is installed.** You can download it from [https://www.java.com/en/download/](https://www.java.com/en/download/)
2. Download the [stable-download/stable-download-new.zip](https://github.com/LAX1DUDE/eaglercraft/raw/main/stable-download/stable-download-new.zip) file from this repository
4. Extract the ZIP file you downloaded to a new folder
5. Open the new folder, go into the `java/bungee_command` folder
6. In Windows, double-click `run.bat`. It should open a new terminal window  
![run.bat](https://i.gyazo.com/2b0f6b3e5b2e5a5a102c62ea5b6fba3f.png)  
**Some computers may just say 'run' instead of 'run.bat', both are correct**
7. On macOS or Linux, google how to open the terminal and use the `cd` command to navigate to `java/bungee_command`  
Then, in that folder, run `chmod +x run_unix.sh` and then run `./run_unix.sh`. It should start the same server
8. Go to the other `java/bukkit_command` folder that was also extracted from the ZIP
9. Again, on Windows, double-click `run.bat` in the folder. It should open a second terminal window.  
Keep both the first and second terminal window you opened, just minimize them don't close
10. Again, on macOS or Linux, repeat step 7 except in the `java/bukkit_command` folder
11. **Your server is now ready.** Download and open [stable-download/Offline_Download_Version.html](https://github.com/LAX1DUDE/eaglercraft/raw/main/stable-download/Offline_Download_Version.html)
12. Go to 'Multiplayer' from the main menu. Select 'Direct Connect', type `127.0.0.1:25565` and press 'Join Server'
13. **It should allow you to connect, if not, check the two terminal windows for errors**
14. If you are okay with regularly checking for updates to [Offline_Download_Version.html](https://github.com/LAX1DUDE/eaglercraft/raw/main/stable-download/Offline_Download_Version.html), you are now finished
15. If you are playing with friends and want a shared website that can be updated, see the `stable-download/web` folder
16. To install, create a website and upload the contents of `stable-download/web` to the URL you want to have Eaglercraft on.  
Edit `index.html`, remove line 19 containing the `alert(` stuff, then search the file for `https://g.eags.us/eaglercraft/` and replace it everywhere with the URL to your own site instead of g.eags.us.
17. To modify the default list of servers, download [servers_template.dat](https://github.com/LAX1DUDE/eaglercraft/raw/main/stable-download/servers_template.dat) and open it in [NBTExplorer](https://github.com/jaquadro/NBTExplorer/releases). Make your changes and then save
18. Upload your modified `servers_template.dat` to [base64encode.org](https://www.base64encode.org/) and press 'Encode'.
19. Download and open the encoded file, copy and paste the text in the file back between the quotes in `index.html` at line 21 (or 22) in place of the old text that also begins with `CgAACQAHc2Vydm` between the quotes
20. To change your server's MOTD and icon, edit the second line of `java/bungee_command/config.yml`, and replace `server-icon.png` in the same folder. Use `&` instead of `§` to add color/formatting codes
21. You can give your MOTD multiple lines, read [yaml-multiline.info](https://yaml-multiline.info/) to see how
22. For an animated MOTD and icon, see: [eaglercraft-plugins/EaglerMOTD](https://github.com/LAX1DUDE/eaglercraft-plugins/tree/main/EaglerMOTD) (WIP, may not be complete)
23. To add some bukkit plugins, download the plugin's JAR file for CraftBukkit 1.5.2 and place it in `java/bukkit_command/plugins`
24. To add some bungee plugins, download the plugin's JAR file and place it in `java/bungee_command/plugins`
25. See [https://github.com/LAX1DUDE/eaglercraft-plugins/](https://github.com/LAX1DUDE/eaglercraft-plugins/) to download some supported plugins
26. To add `/login` and `/register`, install [AuthMe](https://github.com/LAX1DUDE/eaglercraft-plugins/tree/main/AuthMe) and carefully [read it's documentation](https://github.com/AuthMe/AuthMeReloaded/wiki) to set it up correctly
27. **If you use /op on your server, keep in mind that if you "/op LAX1DUDE", a player joining as 'laX1DUDE' or 'LaX1dUdE' or 'lax1dude' will all have /op too. To solve this problem, force all operators to only be able to join with all lowercase ('lax1dude') letters in their usernames by moving 'BitchFilterPlugin.jar" into "java/bukkit_command/plugins" and then register every op username lowercase**
28. To connect to your server through a `ws://` or `wss://` URL instead of `ip:port`, set up [nginx](https://nginx.org/) as a reverse proxy to the `ip:port` of you EaglercraftBungee server you want the URL to connect to. Use a location URL template with the `proxy_pass` directive.
29. Eaglercraft uses port 80 for IP connections by default, typing `127.0.0.1` is the same as typing `ws://127.0.0.1:80/`
30. To forward a client's remote IP address from a request on nginx to EaglercraftBungee for enforcing IP bans, set the `X-Real-IP` header on the request to websocket when it is proxied
31. To make a custom resource pack for your site, clone this repository and edit the files in [lwjgl-rundir/resources](https://github.com/LAX1DUDE/eaglercraft/tree/main/lwjgl-rundir/resources).
32. When you are done, navigate to [epkcompiler/](https://github.com/LAX1DUDE/eaglercraft/tree/main/epkcompiler) and double-click `run.bat`. Wait for the window to say `Press any key to continue...` and close it. Then, go to `../javascript` in the repository and copy `javascript/assets.epk` to the `assets.epk` on your website
33. If you're on mac or linux, navigate to the epkcompiler folder via `cd` and run `chmod +x run_unix.sh` and then `./run_unix.sh` to do this, then copy the same `javascript/assets.epk` to the `assets.epk` on your website

## Singleplayer?

I successfully created Singleplayer for this version of eaglercraft and it works 100%, **but after many rounds of very thorough testing**, I found that TeaVM is unable to optimize certain aspects of terrain generation and world ticking as well as it can optimize the rendering for a multiplayer-only build. On an [i9-11900K]( https://www.cpubenchmark.net/cpu.php?cpu=Intel+Core+i9-11900K+%40+3.50GHz&id=3904) a render distance of 'Tiny' struggles to pass 12 TPS while standing still, and drops below 1 TPS as soon as a couple new chunks have to be generated and trigger lighting updates. The playerbase of this game **will bother me every f\*\*king day if what I release as singleplayer is not perfect** and therefore singleplayer will remain private indefinetly and I will not answer any further questions about it or share the source code unless you are just looking to repurpose some of the base OS emulation code.

**Minecraft Alpha singleplayer will be ported in the coming weeks but that is not a promise I should be obligated to fulfill**

## How does it work?

Eaglercraft uses the decompiled source code of the official build of Minecraft 1.5.2 direct from Mojang. It is decompiled by [MCP](http://www.modcoderpack.com/) and then recompiled to Javascript using [TeaVM](https://teavm.org/). Therefore it can join real Minecraft 1.5.2 servers, as it is really running Minecraft 1.5.2 in the browser. However, due to security limitations in modern browsers, it must use javascript Websocket objects for multiplayer instead of direct TCP connections to it's servers. A modified version of Bungeecord is included with Eaglercraft which accepts browser HTTP Websocket connections from Eaglercraft clients and unwraps the streams internally to regular TCP so they can be forwarded to regular Bukkit servers with no plugins. For graphics, a custom GPU compatibility layer allows Mojang's fixed function OpenGL 1.3 based rendering engine to render directly to an HTML5 WebGL 2.0 canvas on the page with minimal changes to the source, preserving the game's graphics to look exactly the same as desktop vanilla Minecraft 1.5.2.

## Issues?

I got tired of closing duplicate 'how to maek sever' and 'add single player' issues almost every day so I disabled it because honestly I don't really care anymore, [join discord](https://discord.gg/KMQW9Uvjyq) if you've got an issue to report that you are confident can be backed up with source code

## Installing (detailed)

If you want to use this project but don't want to compile it from scratch, download [stable-download/stable-download-new.zip](https://github.com/LAX1DUDE/eaglercraft/raw/main/stable-download/stable-download-new.zip) and extract

Within stable-download-new.zip there is a 'java' and a 'web' folder. Upload the contents of the web folder to your web server. **The web folder will not work if it is opened locally via file:///, it needs to be opened on an http:// or https:// page. Try [this extention](https://chrome.google.com/webstore/detail/web-server-for-chrome/ofhbbkphhbklhfoeikjpcbhemlocgigb/) if you are on chrome or if that's not possible then download the alternative single-file html [offline version](https://github.com/LAX1DUDE/eaglercraft/raw/main/stable-download/Offline_Download_Version.html) that does work on file URLs.** If you use this alternative version, please make sure you and your peers keep your copies up to date by regularly downloading any newer versions of the html file at [this link](https://github.com/LAX1DUDE/eaglercraft/blob/main/stable-download/Offline_Download_Version.html) to avoid getting stuck with a version that has a game-breaking glitch or mistake. The eaglercraft bungeecord executable is in the java/bungee_command folder along with the sample configuration file and a run.bat script to launch it. CraftBukkit for minecraft 1.5.2 configured to work with the eaglercraft bungee executable is in java/bukkit_command. The available version of Spigot 1.5.2 has a bug when used with bungee so you are limited to CraftBukkit and CraftBukkit plugins only on your servers

**Here are some Bukkit plugins compatible with Eaglercraft: [https://github.com/LAX1DUDE/eaglercraft-plugins](https://github.com/LAX1DUDE/eaglercraft-plugins)**

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
