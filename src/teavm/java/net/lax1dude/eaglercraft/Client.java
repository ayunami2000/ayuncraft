package net.lax1dude.eaglercraft;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.teavm.jso.JSBody;
import org.teavm.jso.JSExceptions;
import org.teavm.jso.browser.Window;
import org.teavm.jso.core.JSError;
import org.teavm.jso.dom.html.HTMLDocument;
import org.teavm.jso.dom.html.HTMLElement;

import net.lax1dude.eaglercraft.adapter.EaglerAdapterImpl2;
import net.minecraft.client.Minecraft;
import net.minecraft.src.ServerList;

public class Client {
	private static final String crashImage = "data:image/png;base64,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";
	
	public static HTMLElement rootElement = null;
	public static Minecraft instance = null;
    public static void main(String[] args) {
    	registerErrorHandler();
    	//try {
	    	String[] e = getOpts();
	    	EaglerAdapterImpl2.initializeContext(rootElement = Window.current().getDocument().getElementById(e[0]), e[1]);
	    	LocalStorageManager.loadStorage();
	    	if(e.length > 2) {
	    		ServerList.loadDefaultServers(e[2]);
	    	}
	    	run0();
		//}catch(Throwable t) {
		//	StringWriter s = new StringWriter();
		//	t.printStackTrace(new PrintWriter(s));
		//	showCrashScreen(s.toString());
		//}
    }

    private static void run0() {
    	System.out.println(" -------- starting minecraft -------- ");
    	instance = new Minecraft();
    	run1();
    }
    
    private static void run1() {
    	instance.run();
    }

	@JSBody(params = { }, script = "return window.minecraftOpts;")
	public static native String[] getOpts();

	@JSBody(params = { }, script = "window.minecraftError = null; window.onerror = function(message, file, line, column, errorObj) { if(errorObj) { window.minecraftError = errorObj; window.minecraftErrorL = \"\"+line+\":\"+column; javaMethods.get(\"net.lax1dude.eaglercraft.Client.handleNativeError()V\").invoke(); } else { alert(\"a native browser exception was thrown but your browser does not support fith argument in onerror\"); } };")
	public static native void registerErrorHandler();

	@JSBody(params = { }, script = "return window.minecraftError;")
	public static native JSError getWindowError();
	
	@JSBody(params = { }, script = "return window.minecraftErrorL;")
	public static native String getWindowErrorL();
	
	public static void handleNativeError() {
		JSError e = getWindowError();
		StringBuilder str = new StringBuilder();
		str.append("Native Browser Exception\n");
		str.append("----------------------------------\n");
		str.append("  Line: ").append(getWindowErrorL()).append('\n');
		str.append("  Type: ").append(e.getName()).append('\n');
		str.append("  Message: ").append(e.getMessage()).append('\n');
		str.append("----------------------------------\n\n");
		str.append(e.getStack()).append('\n');
		showCrashScreen(str.toString());
	}
	
	private static boolean isCrashed = false;
	
	private static void showCrashScreen(String t) {
		if(!isCrashed) {
			isCrashed = true;
			EaglerAdapterImpl2.removeEventHandlers();
			
			StringBuilder str = new StringBuilder();
			str.append("i did an oopsie and crashed. report to cgiacun@gmail.com or LAX1DUDE#6306 on discord or @eagler.69 on instagram if you want to see this bug fixed\n\n");
			str.append(t);
			str.append('\n').append('\n');
			str.append("eaglercraft.version = \"").append(ConfigConstants.version).append("\"\n");
			str.append("eaglercraft.minecraft = \"1.5.2\"\n");
			str.append("eaglercraft.brand = \"eagtek\"\n");
			str.append("eaglercraft.username = \"").append(EaglerProfile.username).append("\"\n");
			str.append("eaglercraft.channel = \"").append(EaglerProfile.myChannel).append("\"\n");
			str.append('\n');
			addArray(str, "window.minecraftOpts");
			str.append('\n');
			addDebug(str, "window.navigator.userAgent");
			addDebug(str, "window.navigator.vendor");
			addDebug(str, "window.navigator.language");
			addDebug(str, "window.navigator.hardwareConcurrency");
			addDebug(str, "window.navigator.deviceMemory");
			addDebug(str, "window.navigator.platform");
			addDebug(str, "window.navigator.product");
			str.append('\n');
			str.append("rootElement.clientWidth = ").append(rootElement.getClientWidth()).append('\n');
			str.append("rootElement.clientHeight = ").append(rootElement.getClientHeight()).append('\n');
			addDebug(str, "window.innerWidth");
			addDebug(str, "window.innerHeight");
			addDebug(str, "window.outerWidth");
			addDebug(str, "window.outerHeight");
			addDebug(str, "window.devicePixelRatio");
			addDebug(str, "window.screen.availWidth");
			addDebug(str, "window.screen.availHeight");
			addDebug(str, "window.screen.colorDepth");
			addDebug(str, "window.screen.pixelDepth");
			str.append('\n');
			addDebug(str, "window.currentContext");
			str.append('\n');
			addDebug(str, "window.location.href");
			addArray(str, "window.location.ancestorOrigins");
			str.append("\n----- Begin Minecraft Config -----\n");
			str.append(LocalStorageManager.dumpConfiguration());
			str.append("\n----- End Minecraft Config -----\n\n");
			addDebug(str, "window.minecraftServer");
			
			String s = rootElement.getAttribute("style");
			rootElement.setAttribute("style", (s == null ? "" : s) + "position:relative;");
			HTMLDocument doc = Window.current().getDocument();
			HTMLElement img = doc.createElement("img");
			HTMLElement div = doc.createElement("div");
			img.setAttribute("style", "z-index:100;position:absolute;top:10px;left:calc(50% - 151px);");
			img.setAttribute("src", crashImage);
			div.setAttribute("style", "z-index:100;position:absolute;top:135px;left:10%;right:10%;bottom:30px;background-color:white;border:1px solid #cccccc;overflow-x:hidden;overflow-y:scroll;overflow-wrap:break-word;white-space:pre-wrap;font: 14px monospace;padding:10px;");
			rootElement.appendChild(img);
			rootElement.appendChild(div);
			div.appendChild(doc.createTextNode(str.toString()));
			
		}
	}
	
	@JSBody(params = { "v" }, script = "try { return \"\"+window.eval(v); } catch(e) { return \"<error>\"; }")
	private static native String getString(String var);

	private static void addDebug(StringBuilder str, String var) {
		str.append(var).append(" = ").append(getString(var)).append('\n');
	}
	
	private static void addArray(StringBuilder str, String var) {
		str.append(var).append(" = ").append(getArray(var)).append('\n');
	}
	
	@JSBody(params = { "v" }, script = "try { return JSON.stringify(window.eval(v)); } catch(e) { return \"[\\\"<error>\\\"]\"; }")
	private static native String getArray(String var);
	
}
