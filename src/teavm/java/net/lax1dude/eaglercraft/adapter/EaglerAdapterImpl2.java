package net.lax1dude.eaglercraft.adapter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import org.json.JSONObject;
import org.teavm.interop.Async;
import org.teavm.interop.AsyncCallback;
import org.teavm.jso.JSBody;
import org.teavm.jso.JSFunctor;
import org.teavm.jso.JSObject;
import org.teavm.jso.ajax.ReadyStateChangeHandler;
import org.teavm.jso.ajax.XMLHttpRequest;
import org.teavm.jso.browser.TimerHandler;
import org.teavm.jso.browser.Window;
import org.teavm.jso.canvas.CanvasRenderingContext2D;
import org.teavm.jso.canvas.ImageData;
import org.teavm.jso.dom.events.Event;
import org.teavm.jso.dom.events.EventListener;
import org.teavm.jso.dom.events.KeyboardEvent;
import org.teavm.jso.dom.events.MessageEvent;
import org.teavm.jso.dom.events.MouseEvent;
import org.teavm.jso.dom.events.WheelEvent;
import org.teavm.jso.dom.html.HTMLCanvasElement;
import org.teavm.jso.dom.html.HTMLDocument;
import org.teavm.jso.dom.html.HTMLElement;
import org.teavm.jso.dom.html.HTMLVideoElement;
import org.teavm.jso.dom.html.HTMLImageElement;
import org.teavm.jso.media.MediaError;
import org.teavm.jso.typedarrays.ArrayBuffer;
import org.teavm.jso.typedarrays.Float32Array;
import org.teavm.jso.typedarrays.Int32Array;
import org.teavm.jso.typedarrays.Uint8Array;
import org.teavm.jso.typedarrays.Uint8ClampedArray;
import org.teavm.jso.webaudio.AudioBuffer;
import org.teavm.jso.webaudio.AudioBufferSourceNode;
import org.teavm.jso.webaudio.AudioContext;
import org.teavm.jso.webaudio.AudioListener;
import org.teavm.jso.webaudio.DecodeErrorCallback;
import org.teavm.jso.webaudio.DecodeSuccessCallback;
import org.teavm.jso.webaudio.GainNode;
import org.teavm.jso.webaudio.MediaElementAudioSourceNode;
import org.teavm.jso.webaudio.MediaEvent;
import org.teavm.jso.webaudio.PannerNode;
import org.teavm.jso.webgl.WebGLBuffer;
import org.teavm.jso.webgl.WebGLFramebuffer;
import org.teavm.jso.webgl.WebGLProgram;
import org.teavm.jso.webgl.WebGLRenderbuffer;
import org.teavm.jso.webgl.WebGLShader;
import org.teavm.jso.webgl.WebGLTexture;
import org.teavm.jso.webgl.WebGLUniformLocation;
import org.teavm.jso.websocket.CloseEvent;
import org.teavm.jso.websocket.WebSocket;

import net.lax1dude.eaglercraft.AssetRepository;
import net.lax1dude.eaglercraft.Base64;
import net.lax1dude.eaglercraft.EaglerImage;
import net.lax1dude.eaglercraft.EarlyLoadScreen;
import net.lax1dude.eaglercraft.LocalStorageManager;
import net.lax1dude.eaglercraft.ServerQuery;
import net.lax1dude.eaglercraft.ServerQuery.QueryResponse;
import net.lax1dude.eaglercraft.adapter.teavm.WebGLQuery;
import net.lax1dude.eaglercraft.adapter.teavm.WebGLVertexArray;
import net.minecraft.src.MathHelper;
import net.lax1dude.eaglercraft.adapter.EaglerAdapterImpl2.RateLimit;
import net.lax1dude.eaglercraft.adapter.teavm.WebGL2RenderingContext;
import static net.lax1dude.eaglercraft.adapter.teavm.WebGL2RenderingContext.*;

public class EaglerAdapterImpl2 {

	public static final boolean _wisWebGL() {
		return true;
	}
	private static boolean isAnisotropicPatched = false;
	public static final boolean _wisAnisotropicPatched() {
		return isAnisotropicPatched;
	}
	public static final String _wgetShaderHeader() {
		return "#version 300 es";
	}
	
	@JSBody(params = { }, script = "return window.location.href;")
	private static native String getLocationString();
	
	public static final boolean isSSLPage() {
		return getLocationString().startsWith("https");
	}
	
	public static final InputStream loadResource(String path) {
		byte[] file = loadResourceBytes(path);
		if (file != null) {
			return new ByteArrayInputStream(file);
		} else {
			return null;
		}
	}

	public static final byte[] loadResourceBytes(String path) {
		return AssetRepository.getResource(path);
	}
	
	public static final String fileContents(String path) {
		byte[] contents = loadResourceBytes(path);
		if(contents == null) {
			return null;
		}else {
			return new String(contents, Charset.forName("UTF-8"));
		}
	}
	
	public static final String[] fileContentsLines(String path) {
		String contents = fileContents(path);
		if(contents == null) {
			return null;
		}else {
			return contents.replace("\r\n", "\n").split("[\r\n]");
		}
	}
	
	@Async
	public static native String downloadAssetPack(String assetPackageURI);
	
	private static void downloadAssetPack(String assetPackageURI, final AsyncCallback<String> cb) {
		final XMLHttpRequest request = XMLHttpRequest.create();
		request.setResponseType("arraybuffer");
		request.open("GET", assetPackageURI, true);
		request.setOnReadyStateChange(new ReadyStateChangeHandler() {
			@Override
			public void stateChanged() {
				if(request.getReadyState() == XMLHttpRequest.DONE) {
					Uint8Array bl = Uint8Array.create((ArrayBuffer)request.getResponse());
					loadedPackage = new byte[bl.getByteLength()];
					for(int i = 0; i < loadedPackage.length; ++i) {
						loadedPackage[i] = (byte) bl.get(i);
					}
					cb.complete("yee");
				}
			}
		});
		request.send();
	}
	
	@JSBody(params = { "obj" }, script = "window.currentContext = obj;")
	private static native int setContextVar(JSObject obj);

	@JSBody(params = { "v", "s" }, script = "window[v] = s;")
	public static native void setDebugVar(String v, String s);
	
	@JSBody(params = { }, script = "if(window.navigator.userActivation){return window.navigator.userActivation.hasBeenActive;}else{return false;}")
	public static native boolean hasBeenActive();

	public static HTMLDocument doc = null;
	public static HTMLElement parent = null;
	public static HTMLCanvasElement canvas = null;
	public static CanvasRenderingContext2D frameBuffer = null;
	public static HTMLCanvasElement renderingCanvas = null;
	public static WebGL2RenderingContext webgl = null;
	public static Window win = null;
	private static byte[] loadedPackage = null;
	private static EventListener contextmenu = null;
	private static EventListener mousedown = null;
	private static EventListener mouseup = null;
	private static EventListener mousemove = null;
	private static EventListener keydown = null;
	private static EventListener keyup = null;
	private static EventListener keypress = null;
	private static EventListener wheel = null;
	private static String[] identifier = new String[0];
	
	public static final String[] getIdentifier() {
		return identifier;
	}

	@JSBody(params = { "v" }, script = "try { return \"\"+window.eval(v); } catch(e) { return \"<error>\"; }")
	private static native String getString(String var);
	
	public static void onWindowUnload() {
		LocalStorageManager.saveStorageA();
		LocalStorageManager.saveStorageG();
		LocalStorageManager.saveStorageP();
	}

	@JSBody(params = { "m" }, script = "return m.offsetX;")
	private static native int getOffsetX(MouseEvent m);
	
	@JSBody(params = { "m" }, script = "return m.offsetY;")
	private static native int getOffsetY(MouseEvent m);
	
	@JSBody(params = { "e" }, script = "return e.which;")
	private static native int getWhich(KeyboardEvent e);
	
	public static final void initializeContext(HTMLElement rootElement, String assetPackageURI) {
		parent = rootElement;
		String s = parent.getAttribute("style");
		parent.setAttribute("style", (s == null ? "" : s)+"overflow-x:hidden;overflow-y:hidden;");
		win = Window.current();
		doc = win.getDocument();
		canvas = (HTMLCanvasElement)doc.createElement("canvas");
		canvas.setAttribute("id", "deevis589723589");
		canvas.setWidth(parent.getClientWidth());
		canvas.setHeight(parent.getClientHeight());
		rootElement.appendChild(canvas);
		renderingCanvas = (HTMLCanvasElement)doc.createElement("canvas");
		renderingCanvas.setWidth(canvas.getWidth());
		renderingCanvas.setHeight(canvas.getHeight());
		frameBuffer = (CanvasRenderingContext2D) canvas.getContext("2d");
		webgl = (WebGL2RenderingContext) renderingCanvas.getContext("webgl2", youEagler());
		if(webgl == null) {
			throw new RuntimeException("WebGL 2.0 is not supported in your browser ("+getString("window.navigator.userAgent")+")");
		}
		setContextVar(webgl);
		
		//String agent = getString("window.navigator.userAgent").toLowerCase();
		//if(agent.contains("windows")) isAnisotropicPatched = false;
		
		webgl.getExtension("EXT_texture_filter_anisotropic");
		
		win.addEventListener("contextmenu", contextmenu = new EventListener<MouseEvent>() {
			@Override
			public void handleEvent(MouseEvent evt) {
				evt.preventDefault();
				evt.stopPropagation();
			}
		});
		canvas.addEventListener("mousedown", mousedown = new EventListener<MouseEvent>() {
			@Override
			public void handleEvent(MouseEvent evt) {
				int b = evt.getButton();
				buttonStates[b == 1 ? 2 : (b == 2 ? 1 : b)] = true;
				mouseEvents.add(evt);
				evt.preventDefault();
				evt.stopPropagation();
			}
		});
		canvas.addEventListener("mouseup", mouseup = new EventListener<MouseEvent>() {
			@Override
			public void handleEvent(MouseEvent evt) {
				int b = evt.getButton();
				buttonStates[b == 1 ? 2 : (b == 2 ? 1 : b)] = false;
				mouseEvents.add(evt);
				evt.preventDefault();
				evt.stopPropagation();
			}
		});
		canvas.addEventListener("mousemove", mousemove = new EventListener<MouseEvent>() {
			@Override
			public void handleEvent(MouseEvent evt) {
				mouseX = getOffsetX(evt);
				mouseY = canvas.getClientHeight() - getOffsetY(evt);
				mouseDX += evt.getMovementX();
				mouseDY += -evt.getMovementY();
				evt.preventDefault();
				evt.stopPropagation();
			}
		});
		win.addEventListener("keydown", keydown = new EventListener<KeyboardEvent>() {
			@Override
			public void handleEvent(KeyboardEvent evt) {
				//keyStates[remapKey(evt.getKeyCode())] = true;
				keyStates[remapKey(getWhich(evt))] = true;
				keyEvents.add(evt);
				evt.preventDefault();
				evt.stopPropagation();
			}
		});
		win.addEventListener("keyup", keyup = new EventListener<KeyboardEvent>() {
			@Override
			public void handleEvent(KeyboardEvent evt) {
				//keyStates[remapKey(evt.getKeyCode())] = false;
				keyStates[remapKey(getWhich(evt))] = false;
				keyEvents.add(evt);
				evt.preventDefault();
				evt.stopPropagation();
			}
		});
		win.addEventListener("keypress", keypress = new EventListener<KeyboardEvent>() {
			@Override
			public void handleEvent(KeyboardEvent evt) {
				if(enableRepeatEvents && evt.isRepeat()) keyEvents.add(evt);
				evt.preventDefault();
				evt.stopPropagation();
			}
		});
		canvas.addEventListener("wheel", wheel = new EventListener<WheelEvent>() {
			@Override
			public void handleEvent(WheelEvent evt) {
				mouseEvents.add(evt);
				evt.preventDefault();
				evt.stopPropagation();
			}
		});
		win.addEventListener("blur", new EventListener<WheelEvent>() {
			@Override
			public void handleEvent(WheelEvent evt) {
				isWindowFocused = false;
			}
		});
		win.addEventListener("focus", new EventListener<WheelEvent>() {
			@Override
			public void handleEvent(WheelEvent evt) {
				isWindowFocused = true;
			}
		});
		onBeforeCloseRegister();
		execute("window.eagsFileChooser = {\r\n" + 
				"inputElement: null,\r\n" + 
				"openFileChooser: function(ext, mime){\r\n" + 
				"el = window.eagsFileChooser.inputElement = document.createElement(\"input\");\r\n" + 
				"el.type = \"file\";\r\n" + 
				"el.multiple = false;\r\n" + 
				"el.addEventListener(\"change\", function(evt){\r\n" + 
				"var f = window.eagsFileChooser.inputElement.files;\r\n" + 
				"if(f.length == 0){\r\n" + 
				"window.eagsFileChooser.getFileChooserResult = null;\r\n" + 
				"}else{\r\n" + 
				"(async function(){\r\n" + 
				"window.eagsFileChooser.getFileChooserResult = await f[0].arrayBuffer();\r\n" + 
				"window.eagsFileChooser.getFileChooserResultName = f[0].name;\r\n" + 
				"})();\r\n" + 
				"}\r\n" + 
				"});\r\n" + 
				"window.eagsFileChooser.getFileChooserResult = null;\r\n" + 
				"window.eagsFileChooser.getFileChooserResultName = null;\r\n" + 
				"el.accept = mime;\r\n" + 
				"el.click();\r\n" + 
				"},\r\n" + 
				"getFileChooserResult: null,\r\n" + 
				"getFileChooserResultName: null\r\n" + 
				"};");
		
		EarlyLoadScreen.paintScreen();
		
		downloadAssetPack(assetPackageURI);
		
		try {
			AssetRepository.install(loadedPackage);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if(mouseEvents.isEmpty() && keyEvents.isEmpty() && !hasBeenActive()) {
			EarlyLoadScreen.paintEnable();
			
			while(mouseEvents.isEmpty() && keyEvents.isEmpty()) {
				try {
					Thread.sleep(100l);
				} catch (InterruptedException e) {
					;
				}
			}
		}
		
		audioctx = AudioContext.create();
		
		mouseEvents.clear();
		keyEvents.clear();
		
		Window.setInterval(new TimerHandler() {
			@Override
			public void onTimer() {
				Iterator<BufferedVideo> vids = videosBuffer.values().iterator();
				while(vids.hasNext()) {
					BufferedVideo v = vids.next();
					if(System.currentTimeMillis() - v.requestedTime > v.ttl) {
						v.videoElement.setSrc("");
						vids.remove();
					}
				}
			}
		}, 5000);
	}
	
	public static final void destroyContext() {
		
	}
	
	public static final void removeEventHandlers() {
		win.removeEventListener("contextmenu", contextmenu);
		win.removeEventListener("mousedown", mousedown);
		win.removeEventListener("mouseup", mouseup);
		win.removeEventListener("mousemove", mousemove);
		win.removeEventListener("keydown", keydown);
		win.removeEventListener("keyup", keyup);
		win.removeEventListener("keypress", keypress);
		win.removeEventListener("wheel", wheel);
	}

	private static LinkedList<MouseEvent> mouseEvents = new LinkedList();
	private static LinkedList<KeyboardEvent> keyEvents = new LinkedList();

	private static int mouseX = 0;
	private static int mouseY = 0;
	private static double mouseDX = 0.0D;
	private static double mouseDY = 0.0D;
	private static int width = 0;
	private static int height = 0;
	private static boolean enableRepeatEvents = false;
	private static boolean isWindowFocused = true;
	
	@JSBody(params = { }, script = "return {antialias: false, depth: true, powerPreference: \"high-performance\", desynchronized: false, preserveDrawingBuffer: false, premultipliedAlpha: false, alpha: false};")
	public static native JSObject youEagler();
	
	public static final int _wGL_TEXTURE_2D = TEXTURE_2D;
	public static final int _wGL_DEPTH_TEST = DEPTH_TEST;
	public static final int _wGL_LEQUAL = LEQUAL;
	public static final int _wGL_GEQUAL = GEQUAL;
	public static final int _wGL_GREATER = GREATER;
	public static final int _wGL_LESS = LESS;
	public static final int _wGL_BACK = BACK;
	public static final int _wGL_FRONT = FRONT;
	public static final int _wGL_FRONT_AND_BACK = FRONT_AND_BACK;
	public static final int _wGL_COLOR_BUFFER_BIT = COLOR_BUFFER_BIT;
	public static final int _wGL_DEPTH_BUFFER_BIT = DEPTH_BUFFER_BIT;
	public static final int _wGL_BLEND = BLEND;
	public static final int _wGL_RGBA = RGBA;
	public static final int _wGL_RGB = RGB;
	public static final int _wGL_RGB8 = RGB8;
	public static final int _wGL_RGBA8 = RGBA8;
	public static final int _wGL_UNSIGNED_BYTE = UNSIGNED_BYTE;
	public static final int _wGL_UNSIGNED_SHORT = UNSIGNED_SHORT;
	public static final int _wGL_SRC_ALPHA = SRC_ALPHA;
	public static final int _wGL_ONE_MINUS_SRC_ALPHA = ONE_MINUS_SRC_ALPHA;
	public static final int _wGL_ONE_MINUS_DST_COLOR = ONE_MINUS_DST_COLOR;
	public static final int _wGL_ONE_MINUS_SRC_COLOR = ONE_MINUS_SRC_COLOR;
	public static final int _wGL_ZERO = ZERO;
	public static final int _wGL_CULL_FACE = CULL_FACE;
	public static final int _wGL_TEXTURE_MIN_FILTER = TEXTURE_MIN_FILTER;
	public static final int _wGL_TEXTURE_MAG_FILTER = TEXTURE_MAG_FILTER;
	public static final int _wGL_LINEAR = LINEAR;
	public static final int _wGL_EQUAL = EQUAL;
	public static final int _wGL_SRC_COLOR = SRC_COLOR;
	public static final int _wGL_ONE = ONE;
	public static final int _wGL_NEAREST = NEAREST;
	public static final int _wGL_CLAMP = CLAMP_TO_EDGE;
	public static final int _wGL_TEXTURE_WRAP_S = TEXTURE_WRAP_S;
	public static final int _wGL_TEXTURE_WRAP_T = TEXTURE_WRAP_T;
	public static final int _wGL_REPEAT = REPEAT;
	public static final int _wGL_DST_COLOR = DST_COLOR;
	public static final int _wGL_DST_ALPHA = DST_ALPHA;
	public static final int _wGL_FLOAT = FLOAT;
	public static final int _wGL_SHORT = SHORT;
	public static final int _wGL_TRIANGLES = TRIANGLES;
	public static final int _wGL_TRIANGLE_STRIP = TRIANGLE_STRIP;
	public static final int _wGL_TRIANGLE_FAN = TRIANGLE_FAN;
	public static final int _wGL_LINE_STRIP = LINE_STRIP;
	public static final int _wGL_LINES = LINES;
	public static final int _wGL_PACK_ALIGNMENT = PACK_ALIGNMENT;
	public static final int _wGL_UNPACK_ALIGNMENT = UNPACK_ALIGNMENT;
	public static final int _wGL_TEXTURE0 = TEXTURE0;
	public static final int _wGL_TEXTURE1 = TEXTURE1;
	public static final int _wGL_TEXTURE2 = TEXTURE2;
	public static final int _wGL_TEXTURE3 = TEXTURE3;
	public static final int _wGL_VIEWPORT = VIEWPORT;
	public static final int _wGL_VERTEX_SHADER = VERTEX_SHADER;
	public static final int _wGL_FRAGMENT_SHADER = FRAGMENT_SHADER;
	public static final int _wGL_ARRAY_BUFFER = ARRAY_BUFFER;
	public static final int _wGL_ELEMENT_ARRAY_BUFFER = ELEMENT_ARRAY_BUFFER;
	public static final int _wGL_STATIC_DRAW = STATIC_DRAW;
	public static final int _wGL_DYNAMIC_DRAW = DYNAMIC_DRAW;
	public static final int _wGL_INVALID_ENUM = INVALID_ENUM;
	public static final int _wGL_INVALID_VALUE= INVALID_VALUE;
	public static final int _wGL_INVALID_OPERATION = INVALID_OPERATION;
	public static final int _wGL_OUT_OF_MEMORY = OUT_OF_MEMORY;
	public static final int _wGL_CONTEXT_LOST_WEBGL = CONTEXT_LOST_WEBGL;
	public static final int _wGL_FRAMEBUFFER_COMPLETE = FRAMEBUFFER_COMPLETE;
	public static final int _wGL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT = FRAMEBUFFER_INCOMPLETE_ATTACHMENT;
	public static final int _wGL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT = FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT;
	public static final int _wGL_COLOR_ATTACHMENT0 = COLOR_ATTACHMENT0;
	public static final int _wGL_DEPTH_STENCIL_ATTACHMENT = DEPTH_STENCIL_ATTACHMENT;
	public static final int _wGL_DEPTH_STENCIL = DEPTH_STENCIL;
	public static final int _wGL_NEAREST_MIPMAP_LINEAR = NEAREST_MIPMAP_LINEAR; 
	public static final int _wGL_LINEAR_MIPMAP_LINEAR = LINEAR_MIPMAP_LINEAR; 
	public static final int _wGL_LINEAR_MIPMAP_NEAREST = LINEAR_MIPMAP_NEAREST; 
	public static final int _wGL_NEAREST_MIPMAP_NEAREST = NEAREST_MIPMAP_NEAREST;
	public static final int _wGL_TEXTURE_MAX_LEVEL = TEXTURE_MAX_LEVEL; 
	public static final int _wGL_UNSIGNED_INT_24_8 = UNSIGNED_INT_24_8;
	public static final int _wGL_UNSIGNED_INT = UNSIGNED_INT;
	public static final int _wGL_ANY_SAMPLES_PASSED = ANY_SAMPLES_PASSED; 
	public static final int _wGL_QUERY_RESULT = QUERY_RESULT;
	public static final int _wGL_QUERY_RESULT_AVAILABLE = QUERY_RESULT_AVAILABLE;
	public static final int _wGL_TEXTURE_MAX_ANISOTROPY = TEXTURE_MAX_ANISOTROPY_EXT;
	public static final int _wGL_DEPTH24_STENCIL8 = DEPTH24_STENCIL8;
	public static final int _wGL_DEPTH_COMPONENT32F = DEPTH_COMPONENT32F;
	public static final int _wGL_DEPTH_ATTACHMENT = DEPTH_ATTACHMENT;
	public static final int _wGL_MULTISAMPLE = -1;
	public static final int _wGL_LINE_SMOOTH = -1;
	public static final int _wGL_READ_FRAMEBUFFER = READ_FRAMEBUFFER;
	public static final int _wGL_DRAW_FRAMEBUFFER = DRAW_FRAMEBUFFER;
	public static final int _wGL_FRAMEBUFFER = FRAMEBUFFER;
	public static final int _wGL_POLYGON_OFFSET_FILL = POLYGON_OFFSET_FILL;
	
	public static final class TextureGL { 
		protected final WebGLTexture obj;
		public int w = -1;
		public int h = -1;
		public boolean nearest = true;
		public boolean anisotropic = false;
		protected TextureGL(WebGLTexture obj) { 
			this.obj = obj; 
		} 
	} 
	public static final class BufferGL { 
		protected final WebGLBuffer obj; 
		protected BufferGL(WebGLBuffer obj) { 
			this.obj = obj; 
		} 
	} 
	public static final class ShaderGL { 
		protected final WebGLShader obj; 
		protected ShaderGL(WebGLShader obj) { 
			this.obj = obj; 
		} 
	}
	private static int progId = 0;
	public static final class ProgramGL { 
		protected final WebGLProgram obj; 
		protected final int hashcode; 
		protected ProgramGL(WebGLProgram obj) { 
			this.obj = obj; 
			this.hashcode = ++progId;
		} 
	} 
	public static final class UniformGL { 
		protected final WebGLUniformLocation obj; 
		protected UniformGL(WebGLUniformLocation obj) { 
			this.obj = obj; 
		} 
	} 
	public static final class BufferArrayGL { 
		protected final WebGLVertexArray obj; 
		public boolean isQuadBufferBound; 
		protected BufferArrayGL(WebGLVertexArray obj) { 
			this.obj = obj; 
			this.isQuadBufferBound = false; 
		} 
	} 
	public static final class FramebufferGL { 
		protected final WebGLFramebuffer obj; 
		protected FramebufferGL(WebGLFramebuffer obj) { 
			this.obj = obj; 
		} 
	} 
	public static final class RenderbufferGL { 
		protected final WebGLRenderbuffer obj; 
		protected RenderbufferGL(WebGLRenderbuffer obj) { 
			this.obj = obj; 
		} 
	} 
	public static final class QueryGL { 
		protected final WebGLQuery obj; 
		protected QueryGL(WebGLQuery obj) { 
			this.obj = obj; 
		} 
	}
	
	public static final void _wglEnable(int p1) {
		webgl.enable(p1);
	}
	public static final void _wglClearDepth(float p1) {
		webgl.clearDepth(p1);
	}
	public static final void _wglDepthFunc(int p1) {
		webgl.depthFunc(p1);
	}
	public static final void _wglCullFace(int p1) {
		webgl.cullFace(p1);
	}
	private static int[] viewportCache = new int[4];
	public static final void _wglViewport(int p1, int p2, int p3, int p4) {
		viewportCache[0] = p1; viewportCache[1] = p2;
		viewportCache[2] = p3; viewportCache[3] = p4;
		webgl.viewport(p1, p2, p3, p4);
	}
	public static final void _wglClear(int p1) {
		webgl.clear(p1);
	}
	public static final void _wglClearColor(float p1, float p2, float p3, float p4) {
		webgl.clearColor(p1, p2, p3, p4);
	}
	public static final void _wglDisable(int p1) {
		webgl.disable(p1);
	}
	public static final int _wglGetError() {
		return webgl.getError();
	}
	public static final void _wglFlush() {
		//webgl.flush();
	}
	private static Uint8Array uploadBuffer = Uint8Array.create(ArrayBuffer.create(4 * 1024 * 1024));
	public static final void _wglTexImage2D(int p1, int p2, int p3, int p4, int p5, int p6, int p7, int p8, ByteBuffer p9) {
		if(p9 == null) {
			webgl.texImage2D(p1, p2, p3, p4, p5, p6, p7, p8, null);
		}else {
			int len = p9.remaining();
			Uint8Array uploadBuffer1 = uploadBuffer;
			for(int i = 0; i < len; ++i) {
				uploadBuffer1.set(i, (short) ((int)p9.get() & 0xff));
			}
			Uint8Array data = Uint8Array.create(uploadBuffer.getBuffer(), 0, len);
			webgl.texImage2D(p1, p2, p3, p4, p5, p6, p7, p8, data);
		}
	}
	public static final void _wglBlendFunc(int p1, int p2) {
		webgl.blendFunc(p1, p2);
	}
	public static final void _wglDepthMask(boolean p1) {
		webgl.depthMask(p1);
	}
	public static final void _wglColorMask(boolean p1, boolean p2, boolean p3, boolean p4) {
		webgl.colorMask(p1, p2, p3, p4);
	}
	public static final void _wglBindTexture(int p1, TextureGL p2) {
		webgl.bindTexture(p1, p2 == null ? null : p2.obj);
	}
	public static final void _wglCopyTexSubImage2D(int p1, int p2, int p3, int p4, int p5, int p6, int p7, int p8) {
		webgl.copyTexSubImage2D(p1, p2, p3, p4, p5, p6, p7, p8);
	}
	public static final void _wglTexParameteri(int p1, int p2, int p3) {
		webgl.texParameteri(p1, p2, p3);
	}
	public static final void _wglTexParameterf(int p1, int p2, float p3) {
		webgl.texParameterf(p1, p2, p3);
	}
	public static final void _wglTexImage2D(int p1, int p2, int p3, int p4, int p5, int p6, int p7, int p8, IntBuffer p9) {
		int len = p9.remaining();
		Int32Array deevis = Int32Array.create(uploadBuffer.getBuffer());
		for(int i = 0; i < len; ++i) {
			deevis.set(i, p9.get());
		}
		Uint8Array data = Uint8Array.create(uploadBuffer.getBuffer(), 0, len*4);
		webgl.texImage2D(p1, p2, p3, p4, p5, p6, p7, p8, data);
	}
	public static final void _wglTexSubImage2D(int p1, int p2, int p3, int p4, int p5, int p6, int p7, int p8, IntBuffer p9) {
		int len = p9.remaining();
		Int32Array deevis = Int32Array.create(uploadBuffer.getBuffer());
		for(int i = 0; i < len; ++i) {
			deevis.set(i, p9.get());
		}
		Uint8Array data = Uint8Array.create(uploadBuffer.getBuffer(), 0, len*4);
		webgl.texSubImage2D(p1, p2, p3, p4, p5, p6, p7, p8, data);
	}
	public static final void _wglDeleteTextures(TextureGL p1) {
		webgl.deleteTexture(p1.obj);
	}
	public static final void _wglDrawArrays(int p1, int p2, int p3) {
		webgl.drawArrays(p1, p2, p3);
	}
	public static final void _wglDrawElements(int p1, int p2, int p3, int p4) {
		webgl.drawElements(p1, p2, p3, p4);
	}
	public static final TextureGL _wglGenTextures() {
		return new TextureGL(webgl.createTexture());
	}
	public static final void _wglTexSubImage2D(int p1, int p2, int p3, int p4, int p5, int p6, int p7, int p8, ByteBuffer p9) {
		int len = p9.remaining();
		for(int i = 0; i < len; ++i) {
			uploadBuffer.set(i, (short) ((int)p9.get() & 0xff));
		}
		Uint8Array data = Uint8Array.create(uploadBuffer.getBuffer(), 0, len);
		webgl.texSubImage2D(p1, p2, p3, p4, p5, p6, p7, p8, data);
	}
	public static final void _wglActiveTexture(int p1) {
		webgl.activeTexture(p1);
	}
	public static final ProgramGL _wglCreateProgram() {
		return new ProgramGL(webgl.createProgram());
	}
	public static final ShaderGL _wglCreateShader(int p1) {
		return new ShaderGL(webgl.createShader(p1));
	}
	public static final void _wglAttachShader(ProgramGL p1, ShaderGL p2) {
		webgl.attachShader(p1.obj, p2.obj);
	}
	public static final void _wglDetachShader(ProgramGL p1, ShaderGL p2) {
		webgl.detachShader(p1.obj, p2.obj);
	}
	public static final void _wglCompileShader(ShaderGL p1) {
		webgl.compileShader(p1.obj);
	}
	public static final void _wglLinkProgram(ProgramGL p1) {
		webgl.linkProgram(p1.obj);
	}
	public static final void _wglShaderSource(ShaderGL p1, String p2) {
		webgl.shaderSource(p1.obj, p2);
	}
	public static final String _wglGetShaderInfoLog(ShaderGL p1) {
		return webgl.getShaderInfoLog(p1.obj);
	}
	public static final String _wglGetProgramInfoLog(ProgramGL p1) {
		return webgl.getProgramInfoLog(p1.obj);
	}
	public static final boolean _wglGetShaderCompiled(ShaderGL p1) {
		return webgl.getShaderParameteri(p1.obj, COMPILE_STATUS) == 1;
	}
	public static final boolean _wglGetProgramLinked(ProgramGL p1) {
		return webgl.getProgramParameteri(p1.obj, LINK_STATUS) == 1;
	}
	public static final void _wglDeleteShader(ShaderGL p1) {
		webgl.deleteShader(p1.obj);
	}
	public static final void _wglDeleteProgram(ProgramGL p1) {
		webgl.deleteProgram(p1.obj);
	}
	public static final BufferGL _wglCreateBuffer() {
		return new BufferGL(webgl.createBuffer());
	}
	public static final void _wglDeleteBuffer(BufferGL p1) {
		webgl.deleteBuffer(p1.obj);
	}
	public static final void _wglBindBuffer(int p1, BufferGL p2) {
		webgl.bindBuffer(p1, p2 == null ? null : p2.obj);
	}
	public static final void _wglBufferData0(int p1, IntBuffer p2, int p3) {
		int len = p2.remaining();
		Int32Array deevis = Int32Array.create(uploadBuffer.getBuffer());
		for(int i = 0; i < len; ++i) {
			deevis.set(i, p2.get());
		}
		Uint8Array data = Uint8Array.create(uploadBuffer.getBuffer(), 0, len*4);
		webgl.bufferData(p1, data, p3);
	}
	public static final void _wglBufferSubData0(int p1, int p2, IntBuffer p3) {
		int len = p3.remaining();
		Int32Array deevis = Int32Array.create(uploadBuffer.getBuffer());
		for(int i = 0; i < len; ++i) {
			deevis.set(i, p3.get());
		}
		Uint8Array data = Uint8Array.create(uploadBuffer.getBuffer(), 0, len*4);
		webgl.bufferSubData(p1, p2, data);
	}
	public static final void _wglBufferData(int p1, Object p2, int p3) {
		webgl.bufferData(p1, (Int32Array)p2, p3);
	}
	public static final void _wglBufferSubData(int p1, int p2, Object p3) {
		webgl.bufferSubData(p1, p2, (Int32Array)p3);
	}
	public static final void _wglBindAttribLocation(ProgramGL p1, int p2, String p3) {
		webgl.bindAttribLocation(p1.obj, p2, p3);
	}
	public static final void _wglEnableVertexAttribArray(int p1) {
		webgl.enableVertexAttribArray(p1);
	}
	public static final void _wglDisableVertexAttribArray(int p1) {
		webgl.disableVertexAttribArray(p1);
	}
	public static final UniformGL _wglGetUniformLocation(ProgramGL p1, String p2) {
		WebGLUniformLocation u = webgl.getUniformLocation(p1.obj, p2);
		return u == null ? null : new UniformGL(u);
	}
	public static final void _wglBindAttributeLocation(ProgramGL p1, int p2, String p3) {
		webgl.bindAttribLocation(p1.obj, p2, p3);
	}
	public static final void _wglUniform1f(UniformGL p1, float p2) {
		if(p1 != null) webgl.uniform1f(p1.obj, p2);
	}
	public static final void _wglUniform2f(UniformGL p1, float p2, float p3) {
		if(p1 != null) webgl.uniform2f(p1.obj, p2, p3);
	}
	public static final void _wglUniform3f(UniformGL p1, float p2, float p3, float p4) {
		if(p1 != null) webgl.uniform3f(p1.obj, p2, p3, p4);
	}
	public static final void _wglUniform4f(UniformGL p1, float p2, float p3, float p4, float p5) {
		if(p1 != null) webgl.uniform4f(p1.obj, p2, p3, p4, p5);
	}
	public static final void _wglUniform1i(UniformGL p1, int p2) {
		if(p1 != null) webgl.uniform1i(p1.obj, p2);
	}
	public static final void _wglUniform2i(UniformGL p1, int p2, int p3) {
		if(p1 != null) webgl.uniform2i(p1.obj, p2, p3);
	}
	public static final void _wglUniform3i(UniformGL p1, int p2, int p3, int p4) {
		if(p1 != null) webgl.uniform3i(p1.obj, p2, p3, p4);
	}
	public static final void _wglUniform4i(UniformGL p1, int p2, int p3, int p4, int p5) {
		if(p1 != null) webgl.uniform4i(p1.obj, p2, p3, p4, p5);
	}
	private static Float32Array mat2 = Float32Array.create(4);
	private static Float32Array mat3 = Float32Array.create(9);
	private static Float32Array mat4 = Float32Array.create(16);
	public static final void _wglUniformMat2fv(UniformGL p1, float[] mat) {
		mat2.set(mat);
		if(p1 != null) webgl.uniformMatrix2fv(p1.obj, false, mat2);
	}
	public static final void _wglUniformMat3fv(UniformGL p1, float[] mat) {
		mat3.set(mat);
		if(p1 != null) webgl.uniformMatrix3fv(p1.obj, false, mat3);
	}
	public static final void _wglUniformMat4fv(UniformGL p1, float[] mat) {
		mat4.set(mat);
		if(p1 != null) webgl.uniformMatrix4fv(p1.obj, false, mat4);
	}
	private static int currentProgram = -1;
	public static final void _wglUseProgram(ProgramGL p1) {
		if(p1 != null && currentProgram != p1.hashcode) {
			currentProgram = p1.hashcode;
			webgl.useProgram(p1.obj);
		}
	}
	public static final void _wglGetParameter(int p1, int size, int[] ret) {
		if(p1 == _wGL_VIEWPORT) {
			ret[0] = viewportCache[0];
			ret[1] = viewportCache[1];
			ret[2] = viewportCache[2];
			ret[3] = viewportCache[3];
		}
	}
	public static final void _wglPolygonOffset(float p1, float p2) {
		webgl.polygonOffset(p1, p2);
	}
	public static final void _wglVertexAttribPointer(int p1, int p2, int p3, boolean p4, int p5, int p6) {
		webgl.vertexAttribPointer(p1, p2, p3, p4, p5, p6);
	}
	public static final void _wglBindFramebuffer(int p1, FramebufferGL p2) {
		webgl.bindFramebuffer(p1, p2 == null ? null : p2.obj);
	}
	public static final FramebufferGL _wglCreateFramebuffer() {
		return new FramebufferGL(webgl.createFramebuffer());
	}
	public static final void _wglDeleteFramebuffer(FramebufferGL p1) {
		webgl.deleteFramebuffer(p1.obj);
	}
	public static final void _wglFramebufferTexture2D(int p1, TextureGL p2) {
		webgl.framebufferTexture2D(FRAMEBUFFER, p1, TEXTURE_2D, p2 == null ? null : p2.obj, 0);
	}
	public static final QueryGL _wglCreateQuery() { 
		return new QueryGL(webgl.createQuery()); 
	}
	public static final void _wglBeginQuery(int p1, QueryGL p2) { 
		webgl.beginQuery(p1, p2.obj); 
	}
	public static final void _wglEndQuery(int p1) { 
		webgl.endQuery(p1); 
	}
	public static final void _wglDeleteQuery(QueryGL p1) { 
		webgl.deleteQuery(p1.obj);
	}
	public static final int _wglGetQueryObjecti(QueryGL p1, int p2) { 
		return webgl.getQueryParameter(p1.obj, p2);
	}
	public static final BufferArrayGL _wglCreateVertexArray() {
		return new BufferArrayGL(webgl.createVertexArray());
	}
	public static final void _wglDeleteVertexArray(BufferArrayGL p1) {
		webgl.deleteVertexArray(p1.obj);
	}
	public static final void _wglBindVertexArray(BufferArrayGL p1) {
		webgl.bindVertexArray(p1 == null ? null : p1.obj);
	}
	public static final void _wglDrawBuffer(int p1) {
		webgl.drawBuffers(new int[] { p1 });
	}
	public static final RenderbufferGL _wglCreateRenderBuffer() {
		return new RenderbufferGL(webgl.createRenderbuffer());
	}
	public static final void _wglBindRenderbuffer(RenderbufferGL p1) {
		webgl.bindRenderbuffer(RENDERBUFFER, p1 == null ? null : p1.obj);
	}
	public static final void _wglRenderbufferStorage(int p1, int p2, int p3) {
		webgl.renderbufferStorage(RENDERBUFFER, p1, p2, p3);
	}
	public static final void _wglFramebufferRenderbuffer(int p1, RenderbufferGL p2) {
		webgl.framebufferRenderbuffer(FRAMEBUFFER, p1, RENDERBUFFER, p2 == null ? null : p2.obj);
	}
	public static final void _wglDeleteRenderbuffer(RenderbufferGL p1) {
		webgl.deleteRenderbuffer(p1.obj);
	}
	public static final void _wglRenderbufferStorageMultisample(int p1, int p2, int p3, int p4) {
		webgl.renderbufferStorageMultisample(RENDERBUFFER, p1, p2, p3, p4);
	}
	public static final void _wglBlitFramebuffer(int p1, int p2, int p3, int p4, int p5, int p6, int p7, int p8, int p9, int p10) {
		webgl.blitFramebuffer(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10);
	}
	public static final int _wglGetAttribLocation(ProgramGL p1, String p2) {
		return webgl.getAttribLocation(p1.obj, p2);
	}
	
	@JSBody(params = { "ctx", "p" }, script = "return ctx.getTexParameter(0x0DE1, p) | 0;")
	private static final native int __wglGetTexParameteri(WebGL2RenderingContext ctx, int p);
	public static final int _wglGetTexParameteri(int p1) {
		return __wglGetTexParameteri(webgl, p1);
	}
	@JSBody(params = { "ctx", "p" }, script = "return (0.0 + ctx.getTexParameter(0x0DE1, p));")
	private static final native float __wglGetTexParameterf(WebGL2RenderingContext ctx, int p);
	public static final float _wglGetTexParameterf(int p1) {
		return __wglGetTexParameterf(webgl, p1);
	}
	public static final boolean isWindows() {
		return getString("window.navigator.platform").toLowerCase().contains("win");
	}

	private static HTMLCanvasElement imageLoadCanvas = null;
	private static CanvasRenderingContext2D imageLoadContext = null;

	@JSBody(params = { "buf", "mime" }, script = "return URL.createObjectURL(new Blob([buf], {type: mime}));")
	private static native String getDataURL(ArrayBuffer buf, String mime);
	
	@JSBody(params = { "url" }, script = "URL.revokeObjectURL(url);")
	private static native void freeDataURL(String url);
	
	public static final EaglerImage loadPNG(byte[] data) {
		ArrayBuffer arr = ArrayBuffer.create(data.length);
		Uint8Array.create(arr).set(data);
		return loadPNG0(arr);
	}
	
	@Async
	private static native EaglerImage loadPNG0(ArrayBuffer data);
	
	private static void loadPNG0(ArrayBuffer data, final AsyncCallback<EaglerImage> ret) {
		final HTMLImageElement toLoad = (HTMLImageElement) doc.createElement("img");
		toLoad.addEventListener("load", new EventListener<Event>() {
			@Override
			public void handleEvent(Event evt) {
				if(imageLoadCanvas == null) {
					imageLoadCanvas = (HTMLCanvasElement) doc.createElement("canvas");
				}
				if(imageLoadCanvas.getWidth() < toLoad.getWidth()) {
					imageLoadCanvas.setWidth(toLoad.getWidth());
				}
				if(imageLoadCanvas.getHeight() < toLoad.getHeight()) {
					imageLoadCanvas.setHeight(toLoad.getHeight());
				}
				if(imageLoadContext == null) {
					imageLoadContext = (CanvasRenderingContext2D) imageLoadCanvas.getContext("2d");
				}
				imageLoadContext.clearRect(0, 0, toLoad.getWidth(), toLoad.getHeight());
				imageLoadContext.drawImage(toLoad, 0, 0, toLoad.getWidth(), toLoad.getHeight());
				ImageData pxlsDat = imageLoadContext.getImageData(0, 0, toLoad.getWidth(), toLoad.getHeight());
				Uint8ClampedArray pxls = pxlsDat.getData();
				int totalPixels = pxlsDat.getWidth() * pxlsDat.getHeight();
				freeDataURL(toLoad.getSrc());
				if(pxls.getByteLength() < totalPixels * 4) {
					ret.complete(null);
					return;
				}
				int[] pixels = new int[totalPixels];
				for(int i = 0; i < pixels.length; ++i) {
					pixels[i] = (pxls.get(i * 4) << 16) | (pxls.get(i * 4 + 1) << 8) | pxls.get(i * 4 + 2) | (pxls.get(i * 4 + 3) << 24);
				}
				ret.complete(new EaglerImage(pixels, pxlsDat.getWidth(), pxlsDat.getHeight(), true));
			}
		});
		toLoad.addEventListener("error", new EventListener<Event>() {
			@Override
			public void handleEvent(Event evt) {
				freeDataURL(toLoad.getSrc());
				ret.complete(null);
			}
		});
		String src = getDataURL(data, "image/png");
		if(src == null) {
			ret.complete(null);
		}else {
			toLoad.setSrc(src);
		}
	}

	private static HTMLVideoElement currentVideo = null;
	private static TextureGL videoTexture = null;
	private static boolean videoIsLoaded = false;
	private static boolean videoTexIsInitialized = false;
	private static int frameRate = 33;
	private static long frameTimer = 0l;
	
	public static final boolean isVideoSupported() {
		return true;
	}
	public static final void loadVideo(String src, boolean autoplay) {
		loadVideo(src, autoplay, null, null);
	}
	public static final void loadVideo(String src, boolean autoplay, String setJavascriptPointer) {
		loadVideo(src, autoplay, setJavascriptPointer, null);
	}
	
	@JSBody(params = { "ptr", "el" }, script = "window[ptr] = el;")
	private static native void setVideoPointer(String ptr, HTMLVideoElement el);
	@JSBody(params = { "ptr", "el" }, script = "window[ptr](el);")
	private static native void callVideoLoadEvent(String ptr, HTMLVideoElement el);
	
	private static MediaElementAudioSourceNode currentVideoAudioSource = null;
	
	private static GainNode currentVideoAudioGain = null;
	private static float currentVideoAudioGainValue = 1.0f;
	
	private static PannerNode currentVideoAudioPanner = null;
	private static float currentVideoAudioX = 0.0f;
	private static float currentVideoAudioY = 0.0f;
	private static float currentVideoAudioZ = 0.0f;
	
	public static final void loadVideo(String src, boolean autoplay, String setJavascriptPointer, final String javascriptOnloadFunction) {
		videoIsLoaded = false;
		videoTexIsInitialized = false;
		if(videoTexture == null) {
			videoTexture = _wglGenTextures();
		}
		if(currentVideo != null) {
			currentVideo.pause();
			currentVideo.setSrc("");
		}
		
		BufferedVideo vid = videosBuffer.get(src);
		
		if(vid != null) {
			currentVideo = vid.videoElement;
			videosBuffer.remove(src);
		}else {
			currentVideo = (HTMLVideoElement) win.getDocument().createElement("video");
			currentVideo.setAttribute("crossorigin", "anonymous");
			currentVideo.setAutoplay(autoplay);
		}
		
		if(setJavascriptPointer != null) {
			setVideoPointer(setJavascriptPointer, currentVideo);
		}
		
		currentVideo.addEventListener("playing", new EventListener<Event>() {
			@Override
			public void handleEvent(Event evt) {
				videoIsLoaded = true;
				if(javascriptOnloadFunction != null) {
					callVideoLoadEvent(javascriptOnloadFunction, currentVideo);
				}
			}
		});
		
		if(vid == null) {
			currentVideo.setControls(false);
			currentVideo.setSrc(src);
		}else {
			if(autoplay) {
				currentVideo.play();
			}
		}
		
		if(currentVideoAudioSource != null) {
			currentVideoAudioSource.disconnect();
		}
		
		currentVideoAudioSource = audioctx.createMediaElementSource(currentVideo);
		
		if(currentVideoAudioGainValue < 0.0f) {
			currentVideoAudioSource.connect(audioctx.getDestination());
		}else {
			if(currentVideoAudioGain == null) {
				currentVideoAudioGain = audioctx.createGain();
				currentVideoAudioGain.getGain().setValue(currentVideoAudioGainValue > 1.0f ? 1.0f : currentVideoAudioGainValue);
			}
			
			currentVideoAudioSource.connect(currentVideoAudioGain);
			
			if(currentVideoAudioPanner == null) {
				currentVideoAudioPanner = audioctx.createPanner();
				currentVideoAudioPanner.setRolloffFactor(1f);
				currentVideoAudioPanner.setDistanceModel("linear");
				currentVideoAudioPanner.setPanningModel("HRTF");
				currentVideoAudioPanner.setConeInnerAngle(360f);
				currentVideoAudioPanner.setConeOuterAngle(0f);
				currentVideoAudioPanner.setConeOuterGain(0f);
				currentVideoAudioPanner.setOrientation(0f, 1f, 0f);
				currentVideoAudioPanner.setPosition(currentVideoAudioX, currentVideoAudioY, currentVideoAudioZ);
				currentVideoAudioPanner.setMaxDistance(currentVideoAudioGainValue * 16f + 0.1f);
				currentVideoAudioGain.connect(currentVideoAudioPanner);
				currentVideoAudioPanner.connect(audioctx.getDestination());
			}
		}
		
	}
	
	private static class BufferedVideo {
		
		protected final HTMLVideoElement videoElement;
		protected final String url;
		protected final long requestedTime;
		protected final int ttl;
		
		public BufferedVideo(HTMLVideoElement videoElement, String url, int ttl) {
			this.videoElement = videoElement;
			this.url = url;
			this.requestedTime = System.currentTimeMillis();
			this.ttl = ttl;
		}
		
	}
	
	private static final HashMap<String, BufferedVideo> videosBuffer = new HashMap();
	
	public static final void bufferVideo(String src, int ttl) {
		if(!videosBuffer.containsKey(src)) {
			HTMLVideoElement video = (HTMLVideoElement) win.getDocument().createElement("video");
			video.setAutoplay(false);
			video.setAttribute("crossorigin", "anonymous");
			video.setPreload("auto");
			video.setControls(false);
			video.setSrc(src);
			videosBuffer.put(src, new BufferedVideo(video, src, ttl));
		}
	}
	
	public static final void unloadVideo() {
		if(videoTexture != null) {
			_wglDeleteTextures(videoTexture);
			videoTexture = null;
		}
		if(currentVideo != null) {
			currentVideo.pause();
			currentVideo.setSrc("");
			currentVideo = null;
		}
		if(currentVideoAudioSource != null) {
			currentVideoAudioSource.disconnect();
		}
	}
	public static final boolean isVideoLoaded() {
		return videoTexture != null && currentVideo != null && videoIsLoaded;
	}
	public static final boolean isVideoPaused() {
		return currentVideo == null || currentVideo.isPaused();
	}
	public static final void setVideoPaused(boolean pause) {
		if(currentVideo != null) {
			if(pause) {
				currentVideo.pause();
			}else {
				currentVideo.play();
			}
		}
	}
	public static final void setVideoLoop(boolean loop) {
		if(currentVideo != null) {
			currentVideo.setLoop(loop);
		}
	}
	public static final void setVideoVolume(float x, float y, float z, float v) {
		currentVideoAudioX = x;
		currentVideoAudioY = y;
		currentVideoAudioZ = z;
		if(v < 0.0f) {
			if(currentVideoAudioGainValue >= 0.0f && currentVideoAudioSource != null) {
				currentVideoAudioSource.disconnect();
				currentVideoAudioSource.connect(audioctx.getDestination());
			}
			currentVideoAudioGainValue = v;
		}else {
			if(currentVideoAudioGain != null) {
				currentVideoAudioGain.getGain().setValue(v > 1.0f ? 1.0f : v);
				if(currentVideoAudioGainValue < 0.0f && currentVideoAudioSource != null) {
					currentVideoAudioSource.disconnect();
					currentVideoAudioSource.connect(currentVideoAudioGain);
				}
			}
			currentVideoAudioGainValue = v;
			if(currentVideoAudioPanner != null) {
				currentVideoAudioPanner.setMaxDistance(v * 16f + 0.1f);
				currentVideoAudioPanner.setPosition(x, y, z);
			}
		}
	}
	
	@JSBody(
		params = {"ctx", "target", "internalformat", "format", "type", "video"},
		script = "ctx.texImage2D(target, 0, internalformat, format, type, video);"
	)
	private static native void html5VideoTexImage2D(WebGL2RenderingContext ctx, int target, int internalformat, int format, int type, HTMLVideoElement video);

	@JSBody(
		params = {"ctx", "target", "format", "type", "video"},
		script = "ctx.texSubImage2D(target, 0, 0, 0, format, type, video);"
	)
	private static native void html5VideoTexSubImage2D(WebGL2RenderingContext ctx, int target, int format, int type, HTMLVideoElement video);
	
	public static final void updateVideoTexture() {
		long ms = System.currentTimeMillis();
		if(ms - frameTimer < frameRate && videoTexIsInitialized) {
			return;
		}
		frameTimer = ms;
		if(currentVideo != null && videoTexture != null && videoIsLoaded) {
			try {
				_wglBindTexture(_wGL_TEXTURE_2D, videoTexture);
				if(videoTexIsInitialized) {
					html5VideoTexSubImage2D(webgl, _wGL_TEXTURE_2D, _wGL_RGBA, _wGL_UNSIGNED_BYTE, currentVideo);
				}else {
					html5VideoTexImage2D(webgl, _wGL_TEXTURE_2D, _wGL_RGBA, _wGL_RGBA, _wGL_UNSIGNED_BYTE, currentVideo);
					_wglTexParameteri(_wGL_TEXTURE_2D, _wGL_TEXTURE_WRAP_S, _wGL_CLAMP);
					_wglTexParameteri(_wGL_TEXTURE_2D, _wGL_TEXTURE_WRAP_T, _wGL_CLAMP);
					_wglTexParameteri(_wGL_TEXTURE_2D, _wGL_TEXTURE_MIN_FILTER, _wGL_LINEAR);
					_wglTexParameteri(_wGL_TEXTURE_2D, _wGL_TEXTURE_MAG_FILTER, _wGL_LINEAR);
					videoTexIsInitialized = true;
				}
			}catch(Throwable t) {
				// rip
			}
		}
	}
	public static final void bindVideoTexture() {
		if(videoTexture != null) {
			_wglBindTexture(_wGL_TEXTURE_2D, videoTexture);
		}
	}
	public static final int getVideoWidth() {
		if(currentVideo != null && videoIsLoaded) {
			return currentVideo.getWidth();
		}else {
			return -1;
		}
	}
	public static final int getVideoHeight() {
		if(currentVideo != null && videoIsLoaded) {
			return currentVideo.getHeight();
		}else {
			return -1;
		}
	}
	public static final float getVideoCurrentTime() {
		if(currentVideo != null && videoIsLoaded) {
			return (float) currentVideo.getCurrentTime();
		}else {
			return -1.0f;
		}
	}
	public static final void setVideoCurrentTime(float seconds) {
		if(currentVideo != null && videoIsLoaded) {
			currentVideo.setCurrentTime(seconds);
		}
	}
	public static final float getVideoDuration() {
		if(currentVideo != null && videoIsLoaded) {
			return (float) currentVideo.getDuration();
		}else {
			return -1.0f;
		}
	}

	public static final int VIDEO_ERR_NONE = -1;
	public static final int VIDEO_ERR_ABORTED = 1;
	public static final int VIDEO_ERR_NETWORK = 2;
	public static final int VIDEO_ERR_DECODE = 3;
	public static final int VIDEO_ERR_SRC_NOT_SUPPORTED = 4;

	public static final int getVideoError() {
		if(currentVideo != null && videoIsLoaded) {
			MediaError err = currentVideo.getError();
			if(err != null) {
				return err.getCode();
			}else {
				return -1;
			}
		}else {
			return -1;
		}
	}
	
	public static final void setVideoFrameRate(float fps) {
		frameRate = (int)(1000.0f / fps);
		if(frameRate < 1) {
			frameRate = 1;
		}
	}

	private static HTMLImageElement currentImage = null;
	private static TextureGL imageTexture = null;
	private static boolean imageIsLoaded = false;
	private static boolean imageTexIsInitialized = false;
	private static int imageFrameRate = 33;
	private static long imageFrameTimer = 0l;

	public static final boolean isImageSupported() {
		return true;
	}
	public static final void loadImage(String src) {
		loadImage(src, null);
	}
	public static final void loadImage(String src, String setJavascriptPointer) {
		loadImage(src, setJavascriptPointer, null);
	}

	@JSBody(params = { "ptr", "el" }, script = "window[ptr] = el;")
	private static native void setImagePointer(String ptr, HTMLImageElement el);
	@JSBody(params = { "ptr", "el" }, script = "window[ptr](el);")
	private static native void callImageLoadEvent(String ptr, HTMLImageElement el);

	public static final void loadImage(String src, String setJavascriptPointer, final String javascriptOnloadFunction) {
		imageIsLoaded = false;
		imageTexIsInitialized = false;
		if(imageTexture == null) {
			imageTexture = _wglGenTextures();
		}
		if(currentImage != null) {
			currentImage.setSrc("");
		}

		BufferedImageElem img = imagesBuffer.get(src);

		if(img != null) {
			currentImage = img.imageElement;
			imagesBuffer.remove(src);
		}else {
			currentImage = (HTMLImageElement) win.getDocument().createElement("img");
			currentImage.setAttribute("crossorigin", "anonymous");
		}

		if(setJavascriptPointer != null) {
			setImagePointer(setJavascriptPointer, currentImage);
		}

		currentImage.addEventListener("load", new EventListener<Event>() {
			@Override
			public void handleEvent(Event evt) {
				imageIsLoaded = true;
				if(javascriptOnloadFunction != null) {
					callImageLoadEvent(javascriptOnloadFunction, currentImage);
				}
			}
		});

		if(img == null) {
			currentImage.setSrc(src);
		}
	}

	private static class BufferedImageElem {

		protected final HTMLImageElement imageElement;
		protected final String url;
		protected final long requestedTime;
		protected final int ttl;

		public BufferedImageElem(HTMLImageElement imageElement, String url, int ttl) {
			this.imageElement = imageElement;
			this.url = url;
			this.requestedTime = System.currentTimeMillis();
			this.ttl = ttl;
		}

	}

	private static final HashMap<String, BufferedImageElem> imagesBuffer = new HashMap();

	public static final void bufferImage(String src, int ttl) {
		if(!imagesBuffer.containsKey(src)) {
			HTMLImageElement image = (HTMLImageElement) win.getDocument().createElement("img");
			image.setAttribute("crossorigin", "anonymous");
			image.setSrc(src);
			imagesBuffer.put(src, new BufferedImageElem(image, src, ttl));
		}
	}

	public static final void unloadImage() {
		if(imageTexture != null) {
			_wglDeleteTextures(imageTexture);
			imageTexture = null;
		}
		if(currentImage != null) {
			currentImage.setSrc("");
			currentImage = null;
		}
	}
	public static final boolean isImageLoaded() {
		return imageTexture != null && currentImage != null && imageIsLoaded;
	}

	@JSBody(
			params = {"ctx", "target", "internalformat", "format", "type", "image"},
			script = "ctx.texImage2D(target, 0, internalformat, format, type, image);"
	)
	private static native void html5ImageTexImage2D(WebGL2RenderingContext ctx, int target, int internalformat, int format, int type, HTMLImageElement image);

	@JSBody(
			params = {"ctx", "target", "format", "type", "image"},
			script = "ctx.texSubImage2D(target, 0, 0, 0, format, type, image);"
	)
	private static native void html5ImageTexSubImage2D(WebGL2RenderingContext ctx, int target, int format, int type, HTMLImageElement image);

	public static final void updateImageTexture() {
		long ms = System.currentTimeMillis();
		if(ms - imageFrameTimer < imageFrameRate && imageTexIsInitialized) {
			return;
		}
		imageFrameTimer = ms;
		if(currentImage != null && imageTexture != null && imageIsLoaded) {
			try {
				_wglBindTexture(_wGL_TEXTURE_2D, imageTexture);
				if(imageTexIsInitialized) {
					html5ImageTexSubImage2D(webgl, _wGL_TEXTURE_2D, _wGL_RGBA, _wGL_UNSIGNED_BYTE, currentImage);
				}else {
					html5ImageTexImage2D(webgl, _wGL_TEXTURE_2D, _wGL_RGBA, _wGL_RGBA, _wGL_UNSIGNED_BYTE, currentImage);
					_wglTexParameteri(_wGL_TEXTURE_2D, _wGL_TEXTURE_WRAP_S, _wGL_CLAMP);
					_wglTexParameteri(_wGL_TEXTURE_2D, _wGL_TEXTURE_WRAP_T, _wGL_CLAMP);
					_wglTexParameteri(_wGL_TEXTURE_2D, _wGL_TEXTURE_MIN_FILTER, _wGL_LINEAR);
					_wglTexParameteri(_wGL_TEXTURE_2D, _wGL_TEXTURE_MAG_FILTER, _wGL_LINEAR);
					imageTexIsInitialized = true;
				}
			}catch(Throwable t) {
				// rip
			}
		}
	}
	public static final void bindImageTexture() {
		if(imageTexture != null) {
			_wglBindTexture(_wGL_TEXTURE_2D, imageTexture);
		}
	}
	public static final int getImageWidth() {
		if(currentImage != null && imageIsLoaded) {
			return currentImage.getWidth();
		}else {
			return -1;
		}
	}
	public static final int getImageHeight() {
		if(currentImage != null && imageIsLoaded) {
			return currentImage.getHeight();
		}else {
			return -1;
		}
	}

	public static final void setImageFrameRate(float fps) {
		frameRate = (int)(1000.0f / fps);
		if(frameRate < 1) {
			frameRate = 1;
		}
	}
	
	private static MouseEvent currentEvent = null;
	private static KeyboardEvent currentEventK = null;
	private static boolean[] buttonStates = new boolean[8];
	private static boolean[] keyStates = new boolean[256];
	public static final boolean mouseNext() {
		currentEvent = null;
		return !mouseEvents.isEmpty() && (currentEvent = mouseEvents.remove(0)) != null;
	}
	public static final int mouseGetEventButton() {
		if(currentEvent == null) return -1;
		int b = currentEvent.getButton();
		return b == 1 ? 2 : (b == 2 ? 1 : b);
	}
	public static final boolean mouseGetEventButtonState() {
		return currentEvent == null ? false : currentEvent.getType().equals(MouseEvent.MOUSEDOWN);
	}
	public static final boolean mouseIsButtonDown(int p1) {
		return buttonStates[p1];
	}
	public static final int mouseGetEventDWheel() {
		return ("wheel".equals(currentEvent.getType())) ? (((WheelEvent)currentEvent).getDeltaY() == 0.0D ? 0 : (((WheelEvent)currentEvent).getDeltaY() > 0.0D ? -1 : 1)) : 0;
	}
	public static final void mouseSetCursorPosition(int x, int y) {
		
	}
	private static long mouseUngrabTimer = 0l;
	private static int mouseUngrabTimeout = 0;
	public static final void mouseSetGrabbed(boolean grabbed) {
		if(grabbed) {
			canvas.requestPointerLock();
			long t = System.currentTimeMillis();
			if(mouseUngrabTimeout != 0) Window.clearTimeout(mouseUngrabTimeout);
			if(t - mouseUngrabTimer < 3000l) {
				mouseUngrabTimeout = Window.setTimeout(new TimerHandler() {
					@Override
					public void onTimer() {
						canvas.requestPointerLock();
					}
				}, 3000 - (int)(t - mouseUngrabTimer));
			}
		}else {
			mouseUngrabTimer = System.currentTimeMillis();
			if(mouseUngrabTimeout != 0) Window.clearTimeout(mouseUngrabTimeout);
			doc.exitPointerLock();
		}
	}
	public static final int mouseGetDX() {
		double dx = mouseDX;
		mouseDX = 0.0D;
		return (int)dx;
	}
	public static final int mouseGetDY() {
		double dy = mouseDY;
		mouseDY = 0.0D;
		return (int)dy;
	}
	public static final int mouseGetX() {
		return mouseX;
	}
	public static final int mouseGetY() {
		return mouseY;
	}
	public static final int mouseGetEventX() {
		return currentEvent == null ? -1 : currentEvent.getClientX();
	}
	public static final int mouseGetEventY() {
		return currentEvent == null ? -1 : canvas.getClientHeight() - currentEvent.getClientY();
	}
	public static final boolean keysNext() {
		if(unpressCTRL) { //un-press ctrl after copy/paste permission
			keyEvents.clear();
			currentEventK = null;
			keyStates[29] = false;
			keyStates[157] = false;
			keyStates[28] = false;
			keyStates[219] = false;
			keyStates[220] = false;
			unpressCTRL = false;
			return false;
		}
		currentEventK = null;
		return !keyEvents.isEmpty() && (currentEventK = keyEvents.remove(0)) != null;
	}
	public static final int getEventKey() {
		return currentEventK == null ? -1 : remapKey(getWhich(currentEventK));
	}
	public static final char getEventChar() {
		if(currentEventK == null) return '\0';
		String s = currentEventK.getKey();
		return currentEventK == null ? ' ' : (char) (s.length() > 1 ? '\0' : s.charAt(0));
	}
	public static final boolean getEventKeyState() {
		return currentEventK == null? false : !currentEventK.getType().equals("keyup");
	}
	public static final boolean isKeyDown(int p1) {
		if(unpressCTRL) { //un-press ctrl after copy/paste permission
			keyStates[28] = false;
			keyStates[29] = false;
			keyStates[157] = false;
			keyStates[219] = false;
			keyStates[220] = false;
		}
		return keyStates[p1];
	}
	public static final String getKeyName(int p1) {
		return (p1 >= 0 && p1 < 256) ? LWJGLKeyNames[p1] : "null";
	}
	public static final void setFullscreen(boolean p1) {
		win.alert("use F11 to enter fullscreen");
	}
	public static final boolean shouldShutdown() {
		return false;
	}
	
	@Async
	private static native Object interrupt();
	
	private static void interrupt(final AsyncCallback<Object> cb) {
		Window.setTimeout(new TimerHandler() {
			
			@Override
			public void onTimer() {
				cb.complete(null);
			}
			
		}, 0);
	}
	
	@JSBody(params = { "obj" }, script = "if(obj.commit) obj.commit();")
	private static native int commitContext(JSObject obj);
	
	public static final void updateDisplay() {
		//commitContext(webgl);
		int w = parent.getClientWidth();
		int h = parent.getClientHeight();
		if(canvas.getWidth() != w) {
			canvas.setWidth(w);
		}
		if(canvas.getHeight() != h) {
			canvas.setHeight(h);
		}
		frameBuffer.drawImage(renderingCanvas, 0, 0, w, h);
		if(renderingCanvas.getWidth() != w) {
			renderingCanvas.setWidth(w);
		}
		if(renderingCanvas.getHeight() != h) {
			renderingCanvas.setHeight(h);
		}
		/*
		try {
			Thread.sleep(1l);
		} catch (InterruptedException e) {
			;
		}
		*/
		interrupt();
	}
	public static final void setVSyncEnabled(boolean p1) {
		
	} 
	public static final void enableRepeatEvents(boolean b) {
		enableRepeatEvents = b;
	}
	
	@JSBody(params = { }, script = "return document.pointerLockElement != null;")
	public static native boolean isPointerLocked();
	
	private static boolean pointerLockFlag = false;
	
	public static final boolean isFocused() {
		boolean yee = isPointerLocked();
		boolean dee = pointerLockFlag;
		pointerLockFlag = yee;
		if(!dee && yee) {
			mouseDX = 0.0D;
			mouseDY = 0.0D;
		}
		return isWindowFocused && !(dee && !yee);
	}
	public static final int getScreenWidth() {
		return win.getScreen().getAvailWidth();
	}
	public static final int getScreenHeight() {
		return win.getScreen().getAvailHeight();
	}
	public static final int getCanvasWidth() {
		return renderingCanvas.getWidth();
	}
	public static final int getCanvasHeight() {
		return renderingCanvas.getHeight();
	}
	public static final void setDisplaySize(int x, int y) {
		
	}
	public static final void syncDisplay(int performanceToFps) {
		
	}
	
	private static final DateFormat dateFormatSS = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");
	public static final void saveScreenshot() {
		saveScreenshot("screenshot_" + dateFormatSS.format(new Date()).toString() + ".png", canvas);
	}
	
	@JSBody(params = { "name", "cvs" }, script = "var a=document.createElement(\"a\");a.href=cvs.toDataURL(\"image/png\");a.download=name;a.click();")
	private static native void saveScreenshot(String name, HTMLCanvasElement cvs);

	public static enum RateLimit {
		NONE, FAILED, BLOCKED, FAILED_POSSIBLY_LOCKED, LOCKED, NOW_LOCKED;
	}

	private static final Set<String> rateLimitedAddresses = new HashSet();
	private static final Set<String> blockedAddresses = new HashSet();
	
	private static WebSocket sock = null;
	private static boolean sockIsConnecting = false;
	private static boolean sockIsConnected = false;
	private static boolean sockIsAlive = false;
	private static LinkedList<byte[]> readPackets = new LinkedList();
	private static RateLimit rateLimitStatus = null;
	private static String currentSockURI = null;
	
	public static final RateLimit getRateLimitStatus() {
		RateLimit l = rateLimitStatus;
		rateLimitStatus = null;
		return l;
	}
	public static final void logRateLimit(String addr, RateLimit l) {
		if(l == RateLimit.BLOCKED) {
			blockedAddresses.add(addr);
		}else {
			rateLimitedAddresses.add(addr);
		}
	}
	public static final RateLimit checkRateLimitHistory(String addr) {
		if(blockedAddresses.contains(addr)) {
			return RateLimit.LOCKED;
		}else if(rateLimitedAddresses.contains(addr)) {
			return RateLimit.BLOCKED;
		}else {
			return RateLimit.NONE;
		}
	}
	
	@Async
	public static native String connectWebSocket(String sockURI);
	
	private static void connectWebSocket(String sockURI, final AsyncCallback<String> cb) {
		sockIsConnecting = true;
		sockIsConnected = false;
		sockIsAlive = false;
		rateLimitStatus = null;
		currentSockURI = sockURI;
		try {
			sock = WebSocket.create(sockURI);
		} catch(Throwable t) {
			sockIsConnecting = false;
			sockIsAlive = false;
			return;
		}
		sock.setBinaryType("arraybuffer");
		sock.onOpen(new EventListener<MessageEvent>() {
			@Override
			public void handleEvent(MessageEvent evt) {
				sockIsConnecting = false;
				sockIsAlive = false;
				sockIsConnected = true;
				readPackets.clear();
				cb.complete("okay");
			}
		});
		sock.onClose(new EventListener<CloseEvent>() {
			@Override
			public void handleEvent(CloseEvent evt) {
				sock = null;
				if(sockIsConnecting) {
					if(rateLimitStatus == null) {
						if(blockedAddresses.contains(currentSockURI)) {
							rateLimitStatus = RateLimit.LOCKED;
						}else if(rateLimitedAddresses.contains(currentSockURI)) {
							rateLimitStatus = RateLimit.FAILED_POSSIBLY_LOCKED;
						}else {
							rateLimitStatus = RateLimit.FAILED;
						}
					}
				}else if(!sockIsAlive) {
					if(rateLimitStatus == null) {
						if(blockedAddresses.contains(currentSockURI)) {
							rateLimitStatus = RateLimit.LOCKED;
						}else if(rateLimitedAddresses.contains(currentSockURI)) {
							rateLimitStatus = RateLimit.BLOCKED;
						}
					}
				}
				boolean b = sockIsConnecting;
				sockIsConnecting = false;
				sockIsConnected = false;
				sockIsAlive = false;
				if(b) cb.complete("fail");
			}
		});
		sock.onMessage(new EventListener<MessageEvent>() {
			@Override
			public void handleEvent(MessageEvent evt) {
				sockIsAlive = true;
				if(isString(evt.getData())) {
					String stat = evt.getDataAsString();
					if(stat.equalsIgnoreCase("BLOCKED")) {
						if(rateLimitStatus == null) {
							rateLimitStatus = RateLimit.BLOCKED;
						}
						rateLimitedAddresses.add(currentSockURI);
					}else if(stat.equalsIgnoreCase("LOCKED")) {
						if(rateLimitStatus == null) {
							rateLimitStatus = RateLimit.NOW_LOCKED;
						}
						rateLimitedAddresses.add(currentSockURI);
						blockedAddresses.add(currentSockURI);
					}
					sockIsConnecting = false;
					sockIsConnected = false;
					sock.close();
					return;
				}
				Uint8Array a = Uint8Array.create(evt.getDataAsArray());
				byte[] b = new byte[a.getByteLength()];
				for(int i = 0; i < b.length; ++i) {
					b[i] = (byte) (a.get(i) & 0xFF);
				}
				readPackets.add(b);
			}
		});
	}
	
	public static final boolean startConnection(String uri) {
		String res = connectWebSocket(uri);
		return "fail".equals(res) ? false : true;
	}
	public static final void endConnection() {
		if(sock == null || sock.getReadyState() == 3) {
			sockIsConnecting = false;
		}
		if(sock != null && !sockIsConnecting) sock.close();
	}
	public static final boolean connectionOpen() {
		if(sock == null || sock.getReadyState() == 3) {
			sockIsConnecting = false;
		}
		return sock != null && !sockIsConnecting && sock.getReadyState() != 3;
	}
	@JSBody(params = { "sock", "buffer" }, script = "sock.send(buffer);")
	private static native void nativeBinarySend(WebSocket sock, ArrayBuffer buffer);
	public static final void writePacket(byte[] packet) {
		if(sock != null && !sockIsConnecting) {
			Uint8Array arr = Uint8Array.create(packet.length);
			arr.set(packet);
			nativeBinarySend(sock, arr.getBuffer());
		}
	}
	public static final byte[] readPacket() {
		if(!readPackets.isEmpty()) {
			return readPackets.remove(0);
		}else {
			return null;
		}
	}
	public static final byte[] loadLocalStorage(String key) {
		String s = win.getLocalStorage().getItem("_eaglercraft."+key);
		if(s != null) {
			return Base64.decodeBase64(s);
		}else {
			return null;
		}
	}
	public static final void saveLocalStorage(String key, byte[] data) {
		win.getLocalStorage().setItem("_eaglercraft."+key, Base64.encodeBase64String(data));
	}
	public static final void openLink(String url) {
		win.open(url, "_blank");
	}
	public static final void redirectTo(String url) {
		Window.current().getLocation().setFullURL(url);
	}

	@JSBody(params = { "str" }, script = "window.eval(str);")
	private static native void execute(String str);
	
	@JSBody(params = { }, script = "window.onbeforeunload = function(){javaMethods.get('net.lax1dude.eaglercraft.adapter.EaglerAdapterImpl2.onWindowUnload()V').invoke();return false;};")
	private static native void onBeforeCloseRegister();

	@JSBody(params = { "ext", "mime" }, script = "window.eagsFileChooser.openFileChooser(ext, mime);")
	public static native void openFileChooser(String ext, String mime);
	
	public static final byte[] getFileChooserResult() {
		ArrayBuffer b = getFileChooserResult0();
		if(b == null) return null;
		Uint8Array array = Uint8Array.create(b);
		byte[] ret = new byte[array.getByteLength()];
		for(int i = 0; i < ret.length; ++i) {
			ret[i] = (byte) array.get(i);
		}
		return ret;
	}

	@JSBody(params = {  }, script = "var ret = window.eagsFileChooser.getFileChooserResult; window.eagsFileChooser.getFileChooserResult = null; return ret;")
	private static native ArrayBuffer getFileChooserResult0();

	@JSBody(params = { }, script = "var ret = window.eagsFileChooser.getFileChooserResultName; window.eagsFileChooser.getFileChooserResultName = null; return ret;")
	public static native String getFileChooserResultName();
	
	public static final void setListenerPos(float x, float y, float z, float vx, float vy, float vz, float pitch, float yaw) {
		float var2 = MathHelper.cos(-yaw * 0.017453292F);
		float var3 = MathHelper.sin(-yaw * 0.017453292F);
		float var4 = -MathHelper.cos(pitch * 0.017453292F);
		float var5 = MathHelper.sin(pitch * 0.017453292F);
		AudioListener l = audioctx.getListener();
		l.setPosition(x, y, z);
		l.setOrientation(-var3 * var4, -var5, -var2 * var4, 0.0f, 1.0f, 0.0f);
	}
	
	private static int playbackId = 0;
	private static final HashMap<String,AudioBufferX> loadedSoundFiles = new HashMap();
	private static AudioContext audioctx = null;
	private static float playbackOffsetDelay = 0.03f;
	
	public static final void setPlaybackOffsetDelay(float f) {
		playbackOffsetDelay = f;
	}
	
	@Async
	public static native AudioBuffer decodeAudioAsync(ArrayBuffer buffer);
	
	private static void decodeAudioAsync(ArrayBuffer buffer, final AsyncCallback<AudioBuffer> cb) {
		audioctx.decodeAudioData(buffer, new DecodeSuccessCallback() {
			@Override
			public void onSuccess(AudioBuffer decodedData) {
				cb.complete(decodedData);
			}
		}, new DecodeErrorCallback() {
			@Override
			public void onError(JSObject error) {
				cb.complete(null);
			}
		});
	}
	
	private static final HashMap<Integer,AudioBufferSourceNodeX> activeSoundEffects = new HashMap();
	
	private static class AudioBufferX {
		private final AudioBuffer buffer;
		private AudioBufferX(AudioBuffer buffer) {
			this.buffer = buffer;
		}
	}

	private static class AudioBufferSourceNodeX {
		private final AudioBufferSourceNode source;
		private final PannerNode panner;
		private final GainNode gain;
		private AudioBufferSourceNodeX(AudioBufferSourceNode source, PannerNode panner, GainNode gain) {
			this.source = source;
			this.panner = panner;
			this.gain = gain;
		}
	}
	
	private static final AudioBuffer getBufferFor(String fileName) {
		AudioBufferX ret = loadedSoundFiles.get(fileName);
		if(ret == null) {
			byte[] file = loadResourceBytes(fileName);
			if(file == null) return null;
			Uint8Array buf = Uint8Array.create(file.length);
			buf.set(file);
			ret = new AudioBufferX(decodeAudioAsync(buf.getBuffer()));
			loadedSoundFiles.put(fileName, ret);
		}
		return ret.buffer;
	}
	public static final int beginPlayback(String fileName, float x, float y, float z, float volume, float pitch) {
		AudioBuffer b = getBufferFor(fileName);
		if(b == null) return -1;
		AudioBufferSourceNode s = audioctx.createBufferSource();
		s.setBuffer(b);
		s.getPlaybackRate().setValue(pitch);
		PannerNode p = audioctx.createPanner();
		p.setPosition(x, y, z);
		p.setMaxDistance(volume * 16f + 0.1f);
		p.setRolloffFactor(1f);
		//p.setVelocity(0f, 0f, 0f);
		p.setDistanceModel("linear");
		p.setPanningModel("HRTF");
		p.setConeInnerAngle(360f);
		p.setConeOuterAngle(0f);
		p.setConeOuterGain(0f);
		p.setOrientation(0f, 1f, 0f);
		GainNode g = audioctx.createGain();
		g.getGain().setValue(volume > 1.0f ? 1.0f : volume);
		s.connect(g);
		g.connect(p);
		p.connect(audioctx.getDestination());
		s.start(0.0d, playbackOffsetDelay);
		final int theId = ++playbackId;
		activeSoundEffects.put(theId, new AudioBufferSourceNodeX(s, p, g));
		s.setOnEnded(new EventListener<MediaEvent>() {

			@Override
			public void handleEvent(MediaEvent evt) {
				activeSoundEffects.remove(theId);
			}
			
		});
		return theId;
	}
	public static final int beginPlaybackStatic(String fileName, float volume, float pitch) {
		AudioBuffer b = getBufferFor(fileName);
		if(b == null) return -1;
		AudioBufferSourceNode s = audioctx.createBufferSource();
		s.setBuffer(b);
		s.getPlaybackRate().setValue(pitch);
		GainNode g = audioctx.createGain();
		g.getGain().setValue(volume > 1.0f ? 1.0f : volume);
		s.connect(g);
		g.connect(audioctx.getDestination());
		s.start(0.0d, playbackOffsetDelay);
		final int theId = ++playbackId;
		activeSoundEffects.put(theId, new AudioBufferSourceNodeX(s, null, g));
		s.setOnEnded(new EventListener<MediaEvent>() {

			@Override
			public void handleEvent(MediaEvent evt) {
				activeSoundEffects.remove(theId);
			}
			
		});
		return playbackId;
	}
	public static final void setPitch(int id, float pitch) {
		AudioBufferSourceNodeX b = activeSoundEffects.get(id);
		if(b != null) {
			b.source.getPlaybackRate().setValue(pitch);
		}
	}
	public static final void setVolume(int id, float volume) {
		AudioBufferSourceNodeX b = activeSoundEffects.get(id);
		if(b != null) {
			b.gain.getGain().setValue(volume > 1.0f ? 1.0f : volume);
			if(b.panner != null) b.panner.setMaxDistance(volume * 16f + 0.1f);
		}
	}
	public static final void moveSound(int id, float x, float y, float z, float vx, float vy, float vz) {
		AudioBufferSourceNodeX b = activeSoundEffects.get(id);
		if(b != null && b.panner != null) {
			b.panner.setPosition(x, y, z);
			//b.panner.setVelocity(vx, vy, vz);
		}
	}
	public static final void endSound(int id) {
		AudioBufferSourceNodeX b = activeSoundEffects.get(id);
		if(b != null) {
			b.source.stop();
			activeSoundEffects.remove(id);
		}
	}
	public static final boolean isPlaying(int id) {
		return activeSoundEffects.containsKey(id);
	}
	public static final void openConsole() {
		
	}
	private static boolean connected = false;
	public static final void voiceConnect(String channel) {
		win.alert("voice channels are not implemented yet");
		connected = true;
	}
	public static final void voiceVolume(float volume) {
		
	}
	public static final boolean voiceActive() {
		return connected;
	}
	public static final boolean voiceRelayed() {
		return connected;
	}
	public static final String[] voiceUsers() {
		return new String[0];
	}
	public static final String[] voiceUsersTalking() {
		return new String[0];
	}
	public static final void voiceEnd() {
		connected = false;
	}
	public static final void doJavascriptCoroutines() {
		
	}
	public static final long maxMemory() {
		return 1024*1024*1024;
	}
	public static final long totalMemory() {
		return 1024*1024*1024;
	}
	public static final long freeMemory() {
		return 0l;
	}
	public static final void exit() {
		
	}
	
	@JSBody(params = { }, script = "return window.navigator.userAgent;")
	public static native String getUserAgent();
	
	private static String[] LWJGLKeyNames = new String[] {"NONE", "ESCAPE", "1", "2", "3", "4", "5", "6", "7", "8", "9", "0", "MINUS", "EQUALS", "BACK", "TAB", "Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P", "LBRACKET", "RBRACKET", "RETURN", "LCONTROL", "A", "S", "D", "F", "G", "H", "J", "K", "L", "SEMICOLON", "APOSTROPHE", "GRAVE", "LSHIFT", "BACKSLASH", "Z", "X", "C", "V", "B", "N", "M", "COMMA", "PERIOD", "SLASH", "RSHIFT", "MULTIPLY", "LMENU", "SPACE", "CAPITAL", "F1", "F2", "F3", "F4", "F5", "F6", "F7", "F8", "F9", "F10", "NUMLOCK", "SCROLL", "NUMPAD7", "NUMPAD8", "NUMPAD9", "SUBTRACT", "NUMPAD4", "NUMPAD5", "NUMPAD6", "ADD", "NUMPAD1", "NUMPAD2", "NUMPAD3", "NUMPAD0", "DECIMAL", "null", "null", "null", "F11", "F12", "null", "null", "null", "null", "null", "null", "null", "null", "null", "null", "null", "F13", "F14", "F15", "F16", "F17", "F18", "null", "null", "null", "null", "null", "null", "KANA", "F19", "null", "null", "null", "null", "null", "null", "null", "CONVERT", "null", "NOCONVERT", "null", "YEN", "null", "null", "null", "null", "null", "null", "null", "null", "null", "null", "null", "null", "null", "null", "null", "NUMPADEQUALS", "null", "null", "CIRCUMFLEX", "AT", "COLON", "UNDERLINE", "KANJI", "STOP", "AX", "UNLABELED", "null", "null", "null", "null", "NUMPADENTER", "RCONTROL", "null", "null", "null", "null", "null", "null", "null", "null", "null", "SECTION", "null", "null", "null", "null", "null", "null", "null", "null", "null", "null", "null", "NUMPADCOMMA", "null", "DIVIDE", "null", "SYSRQ", "RMENU", "null", "null", "null", "null", "null", "null", "null", "null", "null", "null", "null", "FUNCTION", "PAUSE", "null", "HOME", "UP", "PRIOR", "null", "LEFT", "null", "RIGHT", "null", "END", "DOWN", "NEXT", "INSERT", "DELETE", "null", "null", "null", "null", "null", "null", "CLEAR", "LMETA", "RMETA", "APPS", "POWER", "SLEEP", "null", "null", "null", "null", "null", "null", "null", "null", "null", "null", "null", "null", "null", "null", "null", "null", "null", "null", "null", "null", "null", "null", "null", "null", "null", "null", "null", "null", "null", "null", "null", "null"};
	
	private static int[] LWJGLKeyCodes = new int[] {
			/* 0 */ -1,
			/* 1 */ -1,
			/* 2 */ -1,
			/* 3 */ -1,
			/* 4 */ -1,
			/* 5 */ -1,
			/* 6 */ -1,
			/* 7 */ -1,
			/* 8 */ 14,
			/* 9 */ 15,
			/* 10 */ -1,
			/* 11 */ -1,
			/* 12 */ -1,
			/* 13 */ 28,
			/* 14 */ -1,
			/* 15 */ -1,
			/* 16 */ 42,
			/* 17 */ 29,
			/* 18 */ 56,
			/* 19 */ -1,
			/* 20 */ -1,
			/* 21 */ -1,
			/* 22 */ -1,
			/* 23 */ -1,
			/* 24 */ -1,
			/* 25 */ -1,
			/* 26 */ -1,
			/* 27 */ 1,
			/* 28 */ -1,
			/* 29 */ -1,
			/* 30 */ -1,
			/* 31 */ -1,
			/* 32 */ 57,
			/* 33 */ 210,
			/* 34 */ 201,
			/* 35 */ 207,
			/* 36 */ 199,
			/* 37 */ 203,
			/* 38 */ 200,
			/* 39 */ 205,
			/* 40 */ 208,
			/* 41 */ 205,
			/* 42 */ 208,
			/* 43 */ -1,
			/* 44 */ -1,
			/* 45 */ 210,
			/* 46 */ 211,
			/* 47 */ 211,
			/* 48 */ 11,
			/* 49 */ 2,
			/* 50 */ 3,
			/* 51 */ 4,
			/* 52 */ 5,
			/* 53 */ 6,
			/* 54 */ 7,
			/* 55 */ 8,
			/* 56 */ 9,
			/* 57 */ 10,
			/* 58 */ -1,
			/* 59 */ -1,
			/* 60 */ -1,
			/* 61 */ -1,
			/* 62 */ -1,
			/* 63 */ -1,
			/* 64 */ -1,
			/* 65 */ 30,
			/* 66 */ 48,
			/* 67 */ 46,
			/* 68 */ 32,
			/* 69 */ 18,
			/* 70 */ 33,
			/* 71 */ 34,
			/* 72 */ 35,
			/* 73 */ 23,
			/* 74 */ 36,
			/* 75 */ 37,
			/* 76 */ 38,
			/* 77 */ 50,
			/* 78 */ 49,
			/* 79 */ 24,
			/* 80 */ 25,
			/* 81 */ 16,
			/* 82 */ 19,
			/* 83 */ 31,
			/* 84 */ 20,
			/* 85 */ 22,
			/* 86 */ 47,
			/* 87 */ 17,
			/* 88 */ 45,
			/* 89 */ 21,
			/* 90 */ 44,
			/* 91 */ -1,
			/* 92 */ -1,
			/* 93 */ -1,
			/* 94 */ -1,
			/* 95 */ -1,
			/* 96 */ -1,
			/* 97 */ -1,
			/* 98 */ -1,
			/* 99 */ -1,
			/* 100 */ -1,
			/* 101 */ -1,
			/* 102 */ -1,
			/* 103 */ -1,
			/* 104 */ -1,
			/* 105 */ -1,
			/* 106 */ -1,
			/* 107 */ -1,
			/* 108 */ -1,
			/* 109 */ 12,
			/* 110 */ 52,
			/* 111 */ 53,
			/* 112 */ -1,
			/* 113 */ -1,
			/* 114 */ -1,
			/* 115 */ -1,
			/* 116 */ -1,
			/* 117 */ -1,
			/* 118 */ -1,
			/* 119 */ -1,
			/* 120 */ -1,
			/* 121 */ -1,
			/* 122 */ -1,
			/* 123 */ -1,
			/* 124 */ -1,
			/* 125 */ -1,
			/* 126 */ -1,
			/* 127 */ -1,
			/* 128 */ -1,
			/* 129 */ -1,
			/* 130 */ -1,
			/* 131 */ -1,
			/* 132 */ -1,
			/* 133 */ -1,
			/* 134 */ -1,
			/* 135 */ -1,
			/* 136 */ -1,
			/* 137 */ -1,
			/* 138 */ -1,
			/* 139 */ -1,
			/* 140 */ -1,
			/* 141 */ -1,
			/* 142 */ -1,
			/* 143 */ -1,
			/* 144 */ -1,
			/* 145 */ -1,
			/* 146 */ -1,
			/* 147 */ -1,
			/* 148 */ -1,
			/* 149 */ -1,
			/* 150 */ -1,
			/* 151 */ -1,
			/* 152 */ -1,
			/* 153 */ -1,
			/* 154 */ -1,
			/* 155 */ -1,
			/* 156 */ -1,
			/* 157 */ -1,
			/* 158 */ -1,
			/* 159 */ -1,
			/* 160 */ -1,
			/* 161 */ -1,
			/* 162 */ -1,
			/* 163 */ -1,
			/* 164 */ -1,
			/* 165 */ -1,
			/* 166 */ -1,
			/* 167 */ -1,
			/* 168 */ -1,
			/* 169 */ -1,
			/* 170 */ -1,
			/* 171 */ -1,
			/* 172 */ -1,
			/* 173 */ -1,
			/* 174 */ -1,
			/* 175 */ -1,
			/* 176 */ -1,
			/* 177 */ -1,
			/* 178 */ -1,
			/* 179 */ -1,
			/* 180 */ -1,
			/* 181 */ -1,
			/* 182 */ -1,
			/* 183 */ -1,
			/* 184 */ -1,
			/* 185 */ -1,
			/* 186 */ 39,
			/* 187 */ 13,
			/* 188 */ 51,
			/* 189 */ 12,
			/* 190 */ 52,
			/* 191 */ 53,
			/* 192 */ -1,
			/* 193 */ -1,
			/* 194 */ -1,
			/* 195 */ -1,
			/* 196 */ -1,
			/* 197 */ -1,
			/* 198 */ -1,
			/* 199 */ -1,
			/* 200 */ -1,
			/* 200 */ -1,
			/* 201 */ -1,
			/* 202 */ -1,
			/* 203 */ -1,
			/* 204 */ -1,
			/* 205 */ -1,
			/* 206 */ -1,
			/* 207 */ -1,
			/* 208 */ -1,
			/* 209 */ -1,
			/* 210 */ -1,
			/* 211 */ -1,
			/* 212 */ -1,
			/* 213 */ -1,
			/* 214 */ -1,
			/* 215 */ -1,
			/* 216 */ -1,
			/* 217 */ -1,
			/* 218 */ -1,
			/* 219 */ 26,
			/* 220 */ 43,
			/* 221 */ 27,
			/* 222 */ 40
	};

	public static final int _wArrayByteLength(Object obj) {
		return ((Int32Array)obj).getByteLength();
	}
	
	public static final Object _wCreateLowLevelIntBuffer(int len) {
		return Int32Array.create(len);
	}
	
	private static int appendbufferindex = 0;
	private static Int32Array appendbuffer = Int32Array.create(ArrayBuffer.create(525000*4));

	public static final void _wAppendLowLevelBuffer(Object arr) {
		Int32Array a = ((Int32Array)arr);
		if(appendbufferindex + a.getLength() < appendbuffer.getLength()) {
			appendbuffer.set(a, appendbufferindex);
			appendbufferindex += a.getLength();
		}
	}
	
	public static final Object _wGetLowLevelBuffersAppended() {
		Int32Array ret = Int32Array.create(appendbuffer.getBuffer(), 0, appendbufferindex);
		appendbufferindex = 0;
		return ret;
	}
	
	private static int remapKey(int k) {
		return (k > LWJGLKeyCodes.length || k < 0) ? -1 : LWJGLKeyCodes[k];
	}
	
	@JSFunctor
	private static interface StupidFunctionResolveString extends JSObject {
		void resolveStr(String s);
	}
	
	private static boolean unpressCTRL = false;
	
	@Async
	public static native String getClipboard();
	
	private static void getClipboard(final AsyncCallback<String> cb) {
		final long start = System.currentTimeMillis();
		getClipboard0(new StupidFunctionResolveString() {
			@Override
			public void resolveStr(String s) {
				if(System.currentTimeMillis() - start > 500l) {
					unpressCTRL = true;
				}
				cb.complete(s);
			}
		});
	}
	
	@JSBody(params = { "cb" }, script = "if(!window.navigator.clipboard) cb(null); else window.navigator.clipboard.readText().then(function(s) { cb(s); }, function(s) { cb(null); });")
	private static native void getClipboard0(StupidFunctionResolveString cb);
	
	@JSBody(params = { "str" }, script = "if(window.navigator.clipboard) window.navigator.clipboard.writeText(str);")
	public static native void setClipboard(String str);
	
	@JSBody(params = { "obj" }, script = "return typeof obj === \"string\";")
	private static native boolean isString(JSObject obj);
	
	private static class ServerQueryImpl implements ServerQuery {
		
		private final LinkedList<QueryResponse> queryResponses = new LinkedList();
		private final LinkedList<byte[]> queryResponsesBytes = new LinkedList();
		private final String type;
		private boolean open;
		private boolean alive;
		private String uriString;
		
		private final WebSocket sock;
		
		private ServerQueryImpl(String type_, String uri) {
			type = type_;
			uriString = uri;
			alive = false;
			WebSocket s = null;
			try {
				s = WebSocket.create(uri);
				s.setBinaryType("arraybuffer");
				open = true;
			}catch(Throwable t) {
				open = false;
				if(EaglerAdapterImpl2.blockedAddresses.contains(uriString)) {
					queryResponses.add(new QueryResponse(true));
				}else if(EaglerAdapterImpl2.rateLimitedAddresses.contains(uriString)) {
					queryResponses.add(new QueryResponse(false));
				}
				sock = null;
				return;
			}
			sock = s;
			if(open) {
				sock.onOpen(new EventListener<MessageEvent>() {
					@Override
					public void handleEvent(MessageEvent evt) {
						sock.send("Accept: " + type);
					}
				});
				sock.onClose(new EventListener<CloseEvent>() {
					@Override
					public void handleEvent(CloseEvent evt) {
						open = false;
						if(!alive) {
							if(EaglerAdapterImpl2.blockedAddresses.contains(uriString)) {
								queryResponses.add(new QueryResponse(true));
							}else if(EaglerAdapterImpl2.rateLimitedAddresses.contains(uriString)) {
								queryResponses.add(new QueryResponse(false));
							}
						}
					}
				});
				sock.onMessage(new EventListener<MessageEvent>() {
					@Override
					public void handleEvent(MessageEvent evt) {
						alive = true;
						if(isString(evt.getData())) {
							try {
								String str = evt.getDataAsString();
								if(str.equalsIgnoreCase("BLOCKED")) {
									EaglerAdapterImpl2.rateLimitedAddresses.add(uriString);
									queryResponses.add(new QueryResponse(false));
									sock.close();
									return;
								}else if(str.equalsIgnoreCase("LOCKED")) {
									EaglerAdapterImpl2.blockedAddresses.add(uriString);
									queryResponses.add(new QueryResponse(true));
									sock.close();
									return;
								}else {
									QueryResponse q = new QueryResponse(new JSONObject(str));
									if(q.rateLimitStatus != null) {
										if(q.rateLimitStatus == RateLimit.BLOCKED) {
											EaglerAdapterImpl2.rateLimitedAddresses.add(uriString);
										}else if(q.rateLimitStatus == RateLimit.LOCKED) {
											EaglerAdapterImpl2.blockedAddresses.add(uriString);
										}
										sock.close();
									}
									queryResponses.add(q);
								}
							}catch(Throwable t) {
								System.err.println("Query response could not be parsed: " + t.toString());
							}
						}else {
							Uint8Array a = Uint8Array.create(evt.getDataAsArray());
							byte[] b = new byte[a.getByteLength()];
							for(int i = 0; i < b.length; ++i) {
								b[i] = (byte) (a.get(i) & 0xFF);
							}
							queryResponsesBytes.add(b);
						}
					}
				});
				Window.setTimeout(new TimerHandler() {
					@Override
					public void onTimer() {
						if(open && sock.getReadyState() != 1) {
							if(sock.getReadyState() == 0) {
								sock.close();
							}
							open = false;
						}
					}
				}, 5000l);
			}
		}

		@Override
		public boolean isQueryOpen() {
			return open;
		}

		@Override
		public void close() {
			open = false;
			sock.close();
		}

		@Override
		public void send(String str) {
			sock.send(str);
		}

		@Override
		public int responseAvailable() {
			return queryResponses.size();
		}

		@Override
		public int responseBinaryAvailable() {
			return queryResponsesBytes.size();
		}

		@Override
		public QueryResponse getResponse() {
			return queryResponses.size() > 0 ? queryResponses.remove(0) : null;
		}

		@Override
		public byte[] getBinaryResponse() {
			return queryResponsesBytes.size() > 0 ? queryResponsesBytes.remove(0) : null;
		}
		
	}

	public static final ServerQuery openQuery(String type, String uri) {
		return new ServerQueryImpl(type, uri);
	}
	
	private static String serverToJoinOnLaunch = null;
	
	public static final void setServerToJoinOnLaunch(String s) {
		serverToJoinOnLaunch = s;
	}
	
	public static final String getServerToJoinOnLaunch() {
		return serverToJoinOnLaunch;
	}
	
}