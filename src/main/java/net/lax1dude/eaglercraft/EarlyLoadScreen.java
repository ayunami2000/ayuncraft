package net.lax1dude.eaglercraft;

import static net.lax1dude.eaglercraft.adapter.EaglerAdapterImpl2.*;

import java.nio.IntBuffer;

import net.lax1dude.eaglercraft.adapter.EaglerAdapterImpl2.BufferArrayGL;
import net.minecraft.src.GLAllocation;

public class EarlyLoadScreen {
	
	public static final String loadScreen = "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";
	public static final String enableScreen = "iVBORw0KGgoAAAANSUhEUgAAAIAAAACACAYAAADDPmHLAAAACXBIWXMAAC4jAAAuIwF4pT92AAAEAklEQVR42u2dvXbjIBBG7T0+xw+gTp06v//LmE6dO/VR5a3wGZNh+BGSFeveJgkIBrDy8TGKds8/Pz/PExyW8/P55AY4MP9YgmNzmeeZVUABAA8AKADgAQAFADwAoACABwAUAPAAgAIAHgBQAMADAAoAeABAAY7LOI7fpQDX65VPtZCt18w5d7rdbigAbOgBxnE8DcPwJnnDMCTrNJlsUVcizTnj9HWxeVvINfN9y361OdTEk30551ZZt3PsvYDYxOSChoPQ6sJ21mRLBm61jY0lpy61gDKWNdfcNcv5wErWLbfPF88I9/s9WtayzopXS85YtPqcMeT23SqedV1pucal1V4iTUooV/IaWSfbWHU5JmkvpmzrsayaB9DqfJnVTpMff72sc869/WzVlcjjOI7mOOVYfBzfT05exLfT5pqae008a71Ly6tPASV79CfPylvFjpm+teLH+tXiF5nA2LOAUMpCibckWpPBUOJT20btFuDjyK8p+S45Z4fX+ti+LDb3pef62PosWbfkDbBW8mFPhB/gt8Vr7gG+kZK9+C/GM2+ArffnnKRHbT5gSdJoK0+ydrziGyCW115LolLxnHOr59q3lt89b6U8Czg4pgdI5bUtKY3VzfOclGBtTLVSmmqn1cdyC7Iud+5791KX1MLJDz3Mg2s59pK6sM/asdTmLrRx5pzjS+e+awWw9lstVeuv1/a10rqwT8sn5LQr8RzaMVfmKrR2qfnFjs57/puLS0nyoTZp0fL8XGq+ap8v4AES+3Msx74kN2/tmblewWoXPl9o+RykZH5/5hTQYv+y+vj084XcPHpJbHmt1s7yGbV1q+UBnHO/gnoZje2RmuzK/Vr2F3sWEF6TGkvutqH5CG08qTmk5u77tLyK5Qtq62rgxRA8AO8FHBkygQeHLQAFADwAoACABwAUAPAAgAIAHgBQAMADAAoAeABAAQAPACgA4AEABQA8AKAAgAcAFAC+3gNM03Tqum7VQSyN4dtvMdZDKcBWC9oqhr8JoIEHeDwep77vf5VJfL0vl9fLa/u+f+vPfx9eszSGNXZo5AH6vlcXW36gsqykrzViwAIPYL3r3nXd63v5m6i9J2+VaT8viWGNHZQbYE97+KdjHPIGKH0XPSyL7eXSjPk2YZlsN03Tq21OjLAs598ZggIT2MpMbW3IMICFN0Dsv4xpfUbfAvIAK9wAcOAtAMgDwJHzAIACAB4AUADAAwAKAHgAQAEADwAoAOABAAUAPACgAIAHABQA8ACAAgAeAFAAwAMACgB4AEABAA8AKADgAQAFADwAoACABwAUAPAAgAIAHgBQAMADAAoAeABAAQAPACgA4AEABQA8AKAAgAcAFADwANCe/0of1jQ8XY5YAAAAAElFTkSuQmCC";

	private static BufferGL vbo = null;
	private static ProgramGL program = null;
	
	public static void paintScreen() {
		
		TextureGL tex = _wglGenTextures();
		_wglActiveTexture(_wGL_TEXTURE0);
		_wglBindTexture(_wGL_TEXTURE_2D, tex);
		_wglTexParameteri(_wGL_TEXTURE_2D, _wGL_TEXTURE_MAG_FILTER, _wGL_NEAREST);
		_wglTexParameteri(_wGL_TEXTURE_2D, _wGL_TEXTURE_MIN_FILTER, _wGL_NEAREST);
		_wglTexParameteri(_wGL_TEXTURE_2D, _wGL_TEXTURE_WRAP_S, _wGL_CLAMP);
		_wglTexParameteri(_wGL_TEXTURE_2D, _wGL_TEXTURE_WRAP_T, _wGL_CLAMP);
		EaglerImage img = EaglerImage.loadImage(Base64.decodeBase64(loadScreen));
		IntBuffer upload = GLAllocation.createDirectIntBuffer(192*192);
		upload.put(img.data);
		upload.flip();
		_wglTexImage2D(_wGL_TEXTURE_2D, 0, _wGL_RGBA, 192, 192, 0, _wGL_RGBA, _wGL_UNSIGNED_BYTE, upload);
		
		upload.clear();
		upload.put(Float.floatToIntBits(0.0f)); upload.put(Float.floatToIntBits(0.0f));
		upload.put(Float.floatToIntBits(0.0f)); upload.put(Float.floatToIntBits(1.0f));
		upload.put(Float.floatToIntBits(1.0f)); upload.put(Float.floatToIntBits(0.0f));
		upload.put(Float.floatToIntBits(1.0f)); upload.put(Float.floatToIntBits(0.0f));
		upload.put(Float.floatToIntBits(0.0f)); upload.put(Float.floatToIntBits(1.0f));
		upload.put(Float.floatToIntBits(1.0f)); upload.put(Float.floatToIntBits(1.0f));
		upload.flip();
			
		vbo = _wglCreateBuffer();
		_wglBindBuffer(_wGL_ARRAY_BUFFER, vbo);
		_wglBufferData0(_wGL_ARRAY_BUFFER, upload, _wGL_STATIC_DRAW);

		ShaderGL vert = _wglCreateShader(_wGL_VERTEX_SHADER);
		_wglShaderSource(vert, _wgetShaderHeader()+"\nprecision lowp float; in vec2 a_pos; out vec2 v_pos; void main() { gl_Position = vec4(((v_pos = a_pos) - 0.5) * vec2(2.0, -2.0), 0.0, 1.0); }");
		_wglCompileShader(vert);
		
		ShaderGL frag = _wglCreateShader(_wGL_FRAGMENT_SHADER);
		_wglShaderSource(frag, _wgetShaderHeader()+"\nprecision lowp float; in vec2 v_pos; out vec4 fragColor; uniform sampler2D tex; uniform vec2 aspect; void main() { fragColor = vec4(texture(tex, clamp(v_pos * aspect - ((aspect - 1.0) * 0.5), 0.02, 0.98)).rgb, 1.0); }");
		_wglCompileShader(frag);
		
		program = _wglCreateProgram();
		
		_wglAttachShader(program, vert);
		_wglAttachShader(program, frag);
		_wglLinkProgram(program);
		_wglDetachShader(program, vert);
		_wglDetachShader(program, frag);
		_wglDeleteShader(vert);
		_wglDeleteShader(frag);
		
		try {
			Thread.sleep(50l);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		_wglUseProgram(program);
		_wglBindAttributeLocation(program, 0, "a_pos");
		_wglUniform1i(_wglGetUniformLocation(program, "tex"), 0);

		int width = getCanvasWidth();
		int height = getCanvasHeight();
		float x, y;
		if(width > height) {
			x = (float)width / (float)height;
			y = 1.0f;
		}else {
			x = 1.0f;
			y = (float)height / (float)width;
		}
		
		_wglActiveTexture(_wGL_TEXTURE0);
		_wglBindTexture(_wGL_TEXTURE_2D, tex);
		
		_wglViewport(0, 0, width, height);
		_wglClearColor(1.0f, 1.0f, 1.0f, 1.0f);
		_wglClear(_wGL_COLOR_BUFFER_BIT | _wGL_DEPTH_BUFFER_BIT);
		
		_wglUniform2f(_wglGetUniformLocation(program, "aspect"), x, y);
		
		BufferArrayGL vao = _wglCreateVertexArray();
		_wglBindVertexArray(vao);
		_wglEnableVertexAttribArray(0);
		_wglVertexAttribPointer(0, 2, _wGL_FLOAT, false, 8, 0);
		_wglDrawArrays(_wGL_TRIANGLES, 0, 6);
		_wglDisableVertexAttribArray(0);
		_wglFlush();
		updateDisplay();

		_wglUseProgram(null);
		_wglBindBuffer(_wGL_ARRAY_BUFFER, null);
		_wglBindTexture(_wGL_TEXTURE_2D, null);
		_wglDeleteTextures(tex);
		_wglDeleteVertexArray(vao);
	}
	
	public static void paintEnable() {
		
		TextureGL tex = _wglGenTextures();
		_wglActiveTexture(_wGL_TEXTURE0);
		_wglBindTexture(_wGL_TEXTURE_2D, tex);
		_wglTexParameteri(_wGL_TEXTURE_2D, _wGL_TEXTURE_MAG_FILTER, _wGL_NEAREST);
		_wglTexParameteri(_wGL_TEXTURE_2D, _wGL_TEXTURE_MIN_FILTER, _wGL_NEAREST);
		_wglTexParameteri(_wGL_TEXTURE_2D, _wGL_TEXTURE_WRAP_S, _wGL_CLAMP);
		_wglTexParameteri(_wGL_TEXTURE_2D, _wGL_TEXTURE_WRAP_T, _wGL_CLAMP);
		EaglerImage img = EaglerImage.loadImage(Base64.decodeBase64(enableScreen));
		IntBuffer upload = GLAllocation.createDirectIntBuffer(128*128);
		upload.put(img.data);
		upload.flip();
		_wglTexImage2D(_wGL_TEXTURE_2D, 0, _wGL_RGBA, 128, 128, 0, _wGL_RGBA, _wGL_UNSIGNED_BYTE, upload);
		
		try {
			Thread.sleep(50l);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		_wglUseProgram(program);

		int width = getCanvasWidth();
		int height = getCanvasHeight();
		float x, y;
		if(width > height) {
			x = (float)width / (float)height;
			y = 1.0f;
		}else {
			x = 1.0f;
			y = (float)height / (float)width;
		}
		
		_wglActiveTexture(_wGL_TEXTURE0);
		_wglBindTexture(_wGL_TEXTURE_2D, tex);
		
		_wglViewport(0, 0, width, height);
		_wglClearColor(1.0f, 1.0f, 1.0f, 1.0f);
		_wglClear(_wGL_COLOR_BUFFER_BIT | _wGL_DEPTH_BUFFER_BIT);
		
		_wglUniform2f(_wglGetUniformLocation(program, "aspect"), x, y);

		BufferArrayGL vao = _wglCreateVertexArray();
		_wglBindVertexArray(vao);
		_wglBindBuffer(_wGL_ARRAY_BUFFER, vbo);
		_wglEnableVertexAttribArray(0);
		_wglVertexAttribPointer(0, 2, _wGL_FLOAT, false, 8, 0);
		_wglDrawArrays(_wGL_TRIANGLES, 0, 6);
		_wglDisableVertexAttribArray(0);
		_wglFlush();
		updateDisplay();

		_wglUseProgram(null);
		_wglBindBuffer(_wGL_ARRAY_BUFFER, null);
		_wglBindTexture(_wGL_TEXTURE_2D, null);
		_wglDeleteTextures(tex);
		_wglDeleteVertexArray(vao);
		
	}
	
}
