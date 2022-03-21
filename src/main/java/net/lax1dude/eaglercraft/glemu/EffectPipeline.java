package net.lax1dude.eaglercraft.glemu;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.HashMap;

import net.lax1dude.eaglercraft.EaglercraftRandom;
import net.lax1dude.eaglercraft.glemu.vector.Matrix4f;

import static net.lax1dude.eaglercraft.adapter.EaglerAdapterImpl2._wGL_CULL_FACE;
import static net.lax1dude.eaglercraft.adapter.EaglerAdapterImpl2._wglDisable;
import static net.lax1dude.eaglercraft.glemu.EaglerAdapterGL30.*;

public class EffectPipeline {

	private static String[] pipeline_tmp = null;
	public static String[] pipeline = new String[0];
	private static HashMap<String, ProgramGL> programs = new HashMap();
	private static HashMap<String, UniformGL[]> uniforms = new HashMap();

	private static FramebufferGL framebuffer;
	private static FramebufferGL framebuffer1;
	private static FramebufferGL framebuffer2;
	private static TextureGL framebuffer_color;
	private static TextureGL framebuffer_color1;
	private static TextureGL framebuffer_color2;
	private static TextureGL framebuffer_depth;

	private static FramebufferGL framebuffer_bloom_a = null;
	private static TextureGL framebuffer_bloom_a_color = null;
	private static FramebufferGL framebuffer_bloom_b = null;
	private static TextureGL framebuffer_bloom_b_color = null;

	private static BufferArrayGL renderQuadArray;
	private static BufferGL renderQuadBuffer;

	private static ShaderGL pvert_shader;

	private static int width = -1;
	private static int height = -1;

	private static int[] originalViewport = null;

	public static void init() {
		framebuffer = _wglCreateFramebuffer();
		//framebuffer1 = _wglCreateFramebuffer();
		//framebuffer2 = _wglCreateFramebuffer();
		framebuffer_color = _wglGenTextures();
		//framebuffer_color1 = _wglGenTextures();
		//framebuffer_color2 = _wglGenTextures();
		framebuffer_depth = _wglGenTextures();
		pvert_shader = _wglCreateShader(_wGL_VERTEX_SHADER);

		_wglShaderSource(pvert_shader, _wgetShaderHeader() + "\n" + fileContents("/glsl/pvert.glsl"));
		_wglCompileShader(pvert_shader);

		if (!_wglGetShaderCompiled(pvert_shader)) System.err.println(("\n" + _wglGetShaderInfoLog(pvert_shader)).replace("\n", "\n[/glsl/pvert.glsl] ") + "\n");

		_wglBindTexture(_wGL_TEXTURE_2D, framebuffer_color);
		_wglTexParameteri(_wGL_TEXTURE_2D, _wGL_TEXTURE_MAG_FILTER, _wGL_NEAREST);
		_wglTexParameteri(_wGL_TEXTURE_2D, _wGL_TEXTURE_MIN_FILTER, _wGL_NEAREST);
		_wglTexParameteri(_wGL_TEXTURE_2D, _wGL_TEXTURE_WRAP_S, _wGL_CLAMP);
		_wglTexParameteri(_wGL_TEXTURE_2D, _wGL_TEXTURE_WRAP_T, _wGL_CLAMP);

		//_wglBindTexture(_wGL_TEXTURE_2D, framebuffer_color1);
		//_wglTexParameteri(_wGL_TEXTURE_2D, _wGL_TEXTURE_MAG_FILTER, _wGL_NEAREST);
		//_wglTexParameteri(_wGL_TEXTURE_2D, _wGL_TEXTURE_MIN_FILTER, _wGL_NEAREST);
		//_wglTexParameteri(_wGL_TEXTURE_2D, _wGL_TEXTURE_WRAP_S, _wGL_CLAMP);
		//_wglTexParameteri(_wGL_TEXTURE_2D, _wGL_TEXTURE_WRAP_T, _wGL_CLAMP);
		
		//_wglBindTexture(_wGL_TEXTURE_2D, framebuffer_color2);
		//_wglTexParameteri(_wGL_TEXTURE_2D, _wGL_TEXTURE_MAG_FILTER, _wGL_NEAREST);
		//_wglTexParameteri(_wGL_TEXTURE_2D, _wGL_TEXTURE_MIN_FILTER, _wGL_NEAREST);
		//_wglTexParameteri(_wGL_TEXTURE_2D, _wGL_TEXTURE_WRAP_S, _wGL_CLAMP);
		//_wglTexParameteri(_wGL_TEXTURE_2D, _wGL_TEXTURE_WRAP_T, _wGL_CLAMP);

		_wglBindTexture(_wGL_TEXTURE_2D, framebuffer_depth);
		_wglTexParameteri(_wGL_TEXTURE_2D, _wGL_TEXTURE_MAG_FILTER, _wGL_NEAREST);
		_wglTexParameteri(_wGL_TEXTURE_2D, _wGL_TEXTURE_MIN_FILTER, _wGL_NEAREST);
		_wglTexParameteri(_wGL_TEXTURE_2D, _wGL_TEXTURE_WRAP_S, _wGL_CLAMP);
		_wglTexParameteri(_wGL_TEXTURE_2D, _wGL_TEXTURE_WRAP_T, _wGL_CLAMP);

		_wglBindFramebuffer(_wGL_FRAMEBUFFER, framebuffer);
		_wglFramebufferTexture2D(_wGL_COLOR_ATTACHMENT0, framebuffer_color);
		_wglFramebufferTexture2D(_wGL_DEPTH_STENCIL_ATTACHMENT, framebuffer_depth);

		//_wglBindFramebuffer(framebuffer1);
		//_wglFramebufferTexture2D(_wGL_COLOR_ATTACHMENT0, framebuffer_color1);
		
		//_wglBindFramebuffer(framebuffer2);
		//_wglFramebufferTexture2D(_wGL_COLOR_ATTACHMENT0, framebuffer_color2);

		_wglBindFramebuffer(_wGL_FRAMEBUFFER, null);
		
		renderQuadArray = _wglCreateVertexArray();
		renderQuadBuffer = _wglCreateBuffer();
		IntBuffer upload = (isWebGL ? IntBuffer.wrap(new int[12]) : ByteBuffer.allocateDirect(12 << 2).order(ByteOrder.nativeOrder()).asIntBuffer());
		upload.put(Float.floatToRawIntBits(0.0f)); upload.put(Float.floatToRawIntBits(0.0f));
		upload.put(Float.floatToRawIntBits(0.0f)); upload.put(Float.floatToRawIntBits(1.0f));
		upload.put(Float.floatToRawIntBits(1.0f)); upload.put(Float.floatToRawIntBits(0.0f));
		upload.put(Float.floatToRawIntBits(1.0f)); upload.put(Float.floatToRawIntBits(0.0f));
		upload.put(Float.floatToRawIntBits(1.0f)); upload.put(Float.floatToRawIntBits(1.0f));
		upload.put(Float.floatToRawIntBits(0.0f)); upload.put(Float.floatToRawIntBits(1.0f));
		upload.flip();
		_wglBindBuffer(_wGL_ARRAY_BUFFER, renderQuadBuffer);
		_wglBufferData0(_wGL_ARRAY_BUFFER, upload, _wGL_STATIC_DRAW);
		_wglBindVertexArray(renderQuadArray);
		_wglEnableVertexAttribArray(0);
		_wglVertexAttribPointer(0, 2, _wGL_FLOAT, false, 8, 0);
	}

	public static void beginPipelineRender() {
		if (pipeline_tmp != null) {
			pipeline = pipeline_tmp;
			pipeline_tmp = null;
		}
		
		if (pipeline.length > 0) {
			int[] viewport = new int[4];
			_wglGetParameter(_wGL_VIEWPORT, 4, viewport);
			if (width != viewport[2] || height != viewport[3]) {
				System.out.println("setting up framebuffer textures");
				width = viewport[2];
				height = viewport[3];
				originalViewport = viewport;

				_wglBindTexture(_wGL_TEXTURE_2D, framebuffer_color);
				_wglTexImage2D(_wGL_TEXTURE_2D, 0, _wGL_RGB, width, height, 0, _wGL_RGB, _wGL_UNSIGNED_BYTE, (ByteBuffer) null);

				//_wglBindTexture(_wGL_TEXTURE_2D, framebuffer_color1);
				//_wglTexImage2D(_wGL_TEXTURE_2D, 0, _wGL_RGB, width, height, 0, _wGL_RGB, _wGL_UNSIGNED_BYTE, (ByteBuffer) null);

				//_wglBindTexture(_wGL_TEXTURE_2D, framebuffer_color2);
				//_wglTexImage2D(_wGL_TEXTURE_2D, 0, _wGL_RGB, width, height, 0, _wGL_RGB, _wGL_UNSIGNED_BYTE, (ByteBuffer) null);

				_wglBindTexture(_wGL_TEXTURE_2D, framebuffer_depth);
				_wglTexImage2D(_wGL_TEXTURE_2D, 0, _wGL_DEPTH24_STENCIL8, width, height, 0, _wGL_DEPTH_STENCIL, _wGL_UNSIGNED_INT_24_8, (ByteBuffer) null);
			}

			_wglActiveTexture(_wGL_TEXTURE1);
			_wglBindTexture(_wGL_TEXTURE_2D, null);
			_wglActiveTexture(_wGL_TEXTURE0);
			_wglBindTexture(_wGL_TEXTURE_2D, null);

			_wglBindFramebuffer(_wGL_FRAMEBUFFER, framebuffer);
			_wglViewport(0, 0, width, height);
		}
		
	}
	
	private static boolean framebufferFlip = false;

	private static int bloom_width = -1;
	private static int bloom_height = -1;
	
	public static void reloadPipeline() {
		System.out.println("reloading "+pipeline.length+" pipeline shader programs");
		String[] tmp1 = pipeline;
		String[] tmp = programs.keySet().toArray(new String[0]);
		for(ProgramGL i : programs.values()) {
			_wglDeleteProgram(i);
		}
		programs.clear();
		uniforms.clear();
		setupPipeline(tmp1, tmp);
	}

	public static void endPipelineRender() {
		if (pipeline.length > 0) {
			_wglBindFramebuffer(_wGL_FRAMEBUFFER, null);
			
			_wglDisable(_wGL_DEPTH_TEST);
			_wglDisable(_wGL_CULL_FACE);
			_wglDepthMask(true);
			
			_wglActiveTexture(_wGL_TEXTURE1);
			_wglBindTexture(_wGL_TEXTURE_2D, framebuffer_depth);
			_wglActiveTexture(_wGL_TEXTURE0);
			
			_wglBindVertexArray(renderQuadArray);
			
			for(int i = 0; i < pipeline.length; ++i) {
				framebufferFlip = !framebufferFlip;
				if(i == 0) {
					_wglBindTexture(_wGL_TEXTURE_2D, framebuffer_color);
				}else {
					_wglBindTexture(_wGL_TEXTURE_2D, framebufferFlip ? framebuffer_color2 : framebuffer_color1);
				}
				
				if(i == pipeline.length - 1) {
					_wglBindFramebuffer(_wGL_FRAMEBUFFER, null);
					_wglViewport(originalViewport[0], originalViewport[1], originalViewport[2], originalViewport[3]);
				}else {
					_wglBindFramebuffer(_wGL_FRAMEBUFFER, framebufferFlip ? framebuffer1 : framebuffer2);
					_wglViewport(0, 0, width, height);
				}
				
				_wglActiveTexture(_wGL_TEXTURE0);
				
				ProgramGL prog = programs.get(pipeline[i]);
				_wglUseProgram(prog);
				setUniforms(uniforms.get(pipeline[i]));
				
				_wglActiveTexture(_wGL_TEXTURE0);
				if(i == 0) {
					_wglBindTexture(_wGL_TEXTURE_2D, framebuffer_color);
				}else {
					_wglBindTexture(_wGL_TEXTURE_2D, framebufferFlip ? framebuffer_color2 : framebuffer_color1);
				}
				
				if(i == pipeline.length - 1) {
					_wglBindFramebuffer(_wGL_FRAMEBUFFER, null);
					_wglViewport(originalViewport[0], originalViewport[1], originalViewport[2], originalViewport[3]);
				}else {
					_wglBindFramebuffer(_wGL_FRAMEBUFFER, framebufferFlip ? framebuffer1 : framebuffer2);
					_wglViewport(0, 0, width, height);
				}
				
				_wglUseProgram(prog);
				_wglDrawArrays(_wGL_TRIANGLES, 0, 6);

				_wglActiveTexture(_wGL_TEXTURE0);
				_wglBindTexture(_wGL_TEXTURE_2D, null);
				_wglActiveTexture(_wGL_TEXTURE1);
				_wglBindTexture(_wGL_TEXTURE_2D, null);
				_wglActiveTexture(_wGL_TEXTURE2);
				_wglBindTexture(_wGL_TEXTURE_2D, null);
				_wglActiveTexture(_wGL_TEXTURE0);
			}
			
			_wglBindFramebuffer(_wGL_FRAMEBUFFER, null);
		}
	}
	
	private static final EaglercraftRandom deevis = new EaglercraftRandom();

	private static float[] projBuffer = new float[16];
	private static float[] projBufferInv = new float[16];
	private static Matrix4f projectionMatrix = (Matrix4f) new Matrix4f().setZero();
	private static Matrix4f projectionMatrixInv = (Matrix4f) new Matrix4f().setZero();

	private static long randomInterTimer = 0l;
	private static float randomInterA = 0.0f;
	private static float randomInterB = 0.0f;
	
	private static void setUniforms(UniformGL[] is) {
		if(is[0] != null) {
			_wglUniform2f(is[0], width, height);
		}
		if(is[1] != null) {
			_wglUniform1f(is[1], deevis.nextFloat());
		}
		if(is[2] != null) {
			/*
			_wglUniform1i(is[2], 2);
			makeSSAOTexture();
			_wglActiveTexture(_wGL_TEXTURE2);
			_wglBindTexture(_wGL_TEXTURE_2D, framebuffer_ssao_color);
			_wglActiveTexture(_wGL_TEXTURE0);
			*/
		}
		if((is[3] != null || is[4] != null) && !projectionMatrix.equals(matProjV[matProjPointer])) {
			projectionMatrix.load(matProjV[matProjPointer]);
			if(is[3] != null) {
				projectionMatrix.store(projBuffer);
				_wglUniformMat4fv(is[3], projBuffer);
			}
			if(is[4] != null) {
				Matrix4f.invert(projectionMatrix, projectionMatrixInv);
				projectionMatrixInv.store(projBufferInv);
				_wglUniformMat4fv(is[4], projBufferInv);
			}
		}
		if(is[5] != null) {
			_wglUniform1i(is[5], 2);
			makeBloomTexture();
			_wglActiveTexture(_wGL_TEXTURE2);
			_wglBindTexture(_wGL_TEXTURE_2D, framebuffer_bloom_a_color);
			_wglTexParameteri(_wGL_TEXTURE_2D, _wGL_TEXTURE_MAG_FILTER, _wGL_LINEAR);
			_wglTexParameteri(_wGL_TEXTURE_2D, _wGL_TEXTURE_MIN_FILTER, _wGL_LINEAR);
			_wglActiveTexture(_wGL_TEXTURE0);
		}
		if(is[7] != null) {
			long time = System.currentTimeMillis();
			if(time - randomInterTimer > 140l) {
				randomInterTimer = time;
				randomInterB = randomInterA;
				randomInterA = deevis.nextFloat();
			}
			_wglUniform2f(is[7], randomInterA, randomInterB);
			float r = ((float)(time - randomInterTimer) / 140.0f);
			_wglUniform1f(is[8], 0 - 2 * r * r * r + 3 * r * r);
		}
	}
	
	private static void makeBloomTexture() {
		if(framebuffer_bloom_a == null) {
			framebuffer_bloom_a = _wglCreateFramebuffer();
			framebuffer_bloom_a_color = _wglGenTextures();
			framebuffer_bloom_b = _wglCreateFramebuffer();
			framebuffer_bloom_b_color = _wglGenTextures();

			_wglBindTexture(_wGL_TEXTURE_2D, framebuffer_bloom_a_color);
			_wglTexParameteri(_wGL_TEXTURE_2D, _wGL_TEXTURE_MAG_FILTER, _wGL_NEAREST);
			_wglTexParameteri(_wGL_TEXTURE_2D, _wGL_TEXTURE_MIN_FILTER, _wGL_NEAREST);
			_wglTexParameteri(_wGL_TEXTURE_2D, _wGL_TEXTURE_WRAP_S, _wGL_CLAMP);
			_wglTexParameteri(_wGL_TEXTURE_2D, _wGL_TEXTURE_WRAP_T, _wGL_CLAMP);

			_wglBindTexture(_wGL_TEXTURE_2D, framebuffer_bloom_b_color);
			_wglTexParameteri(_wGL_TEXTURE_2D, _wGL_TEXTURE_MAG_FILTER, _wGL_NEAREST);
			_wglTexParameteri(_wGL_TEXTURE_2D, _wGL_TEXTURE_MIN_FILTER, _wGL_NEAREST);
			_wglTexParameteri(_wGL_TEXTURE_2D, _wGL_TEXTURE_WRAP_S, _wGL_CLAMP);
			_wglTexParameteri(_wGL_TEXTURE_2D, _wGL_TEXTURE_WRAP_T, _wGL_CLAMP);
			
			_wglBindFramebuffer(_wGL_FRAMEBUFFER, framebuffer_bloom_a);
			_wglFramebufferTexture2D(_wGL_COLOR_ATTACHMENT0, framebuffer_bloom_a_color);
			
			_wglBindFramebuffer(_wGL_FRAMEBUFFER, framebuffer_bloom_b);
			_wglFramebufferTexture2D(_wGL_COLOR_ATTACHMENT0, framebuffer_bloom_b_color);
		}
		
		int w = width / 4;
		int h = height / 4;
		if(bloom_width != w || bloom_height != h) {
			bloom_width = w;
			bloom_height = h;

			_wglBindTexture(_wGL_TEXTURE_2D, framebuffer_bloom_a_color);
			_wglTexImage2D(_wGL_TEXTURE_2D, 0, _wGL_RGB, bloom_width, bloom_height, 0, _wGL_RGB, _wGL_UNSIGNED_BYTE, (ByteBuffer) null);
			
			_wglBindTexture(_wGL_TEXTURE_2D, framebuffer_bloom_b_color);
			_wglTexImage2D(_wGL_TEXTURE_2D, 0, _wGL_RGB, bloom_width, bloom_height, 0, _wGL_RGB, _wGL_UNSIGNED_BYTE, (ByteBuffer) null);
		}

		int[] viewport = new int[4];
		_wglGetParameter(_wGL_VIEWPORT, 4, viewport);
		
		_wglActiveTexture(_wGL_TEXTURE0);
		
		_wglBindFramebuffer(_wGL_FRAMEBUFFER, framebuffer_bloom_a);
		_wglViewport(0, 0, bloom_width, bloom_height);
		UniformGL i;
		_wglUseProgram(programs.get("/glsl/bloom_a.glsl"));
		i = uniforms.get("/glsl/bloom_a.glsl")[0];
		_wglUniform2i(i, bloom_width, bloom_height);
		_wglDrawArrays(_wGL_TRIANGLES, 0, 6);
		
		_wglBindFramebuffer(_wGL_FRAMEBUFFER, framebuffer_bloom_b);
		_wglActiveTexture(_wGL_TEXTURE0);
		_wglBindTexture(_wGL_TEXTURE_2D, framebuffer_bloom_a_color);
		_wglTexParameteri(_wGL_TEXTURE_2D, _wGL_TEXTURE_MAG_FILTER, _wGL_NEAREST);
		_wglTexParameteri(_wGL_TEXTURE_2D, _wGL_TEXTURE_MIN_FILTER, _wGL_NEAREST);
		_wglUseProgram(programs.get("/glsl/bloom_b.glsl"));
		i = uniforms.get("/glsl/bloom_b.glsl")[0];
		UniformGL j = uniforms.get("/glsl/bloom_b.glsl")[6];
		_wglUniform2i(i, bloom_width, bloom_height);
		_wglUniform2i(j, 0, 1);
		_wglDrawArrays(_wGL_TRIANGLES, 0, 6);
		_wglBindTexture(_wGL_TEXTURE_2D, null);
		
		_wglBindFramebuffer(_wGL_FRAMEBUFFER, framebuffer_bloom_a);
		_wglActiveTexture(_wGL_TEXTURE0);
		_wglBindTexture(_wGL_TEXTURE_2D, framebuffer_bloom_b_color);
		_wglTexParameteri(_wGL_TEXTURE_2D, _wGL_TEXTURE_MAG_FILTER, _wGL_NEAREST);
		_wglTexParameteri(_wGL_TEXTURE_2D, _wGL_TEXTURE_MIN_FILTER, _wGL_NEAREST);
		_wglUniform2i(j, 1, 0);
		_wglDrawArrays(_wGL_TRIANGLES, 0, 6);
		_wglBindTexture(_wGL_TEXTURE_2D, null);
		
		_wglViewport(viewport[0], viewport[1], viewport[2], viewport[3]);
		
		_wglBindFramebuffer(_wGL_FRAMEBUFFER, null);
		
	}
	
	/*
	private static void makeSSAOTexture() {
		if(framebuffer_ssao == -1) {
			framebuffer_ssao = _wglCreateFramebuffer();
			framebuffer_ssao_color = _wglGenTextures();

			_wglBindTexture(_wGL_TEXTURE_2D, framebuffer_ssao_color);
			_wglTexParameteri(_wGL_TEXTURE_2D, _wGL_TEXTURE_MAG_FILTER, _wGL_LINEAR);
			_wglTexParameteri(_wGL_TEXTURE_2D, _wGL_TEXTURE_MIN_FILTER, _wGL_LINEAR);
			_wglTexParameteri(_wGL_TEXTURE_2D, _wGL_TEXTURE_WRAP_S, _wGL_CLAMP);
			_wglTexParameteri(_wGL_TEXTURE_2D, _wGL_TEXTURE_WRAP_T, _wGL_CLAMP);
			
			_wglBindFramebuffer(framebuffer_ssao);
			_wglFramebufferTexture2D(_wGL_COLOR_ATTACHMENT0, framebuffer_ssao_color);
		}
		
		int sssao_width = width;
		int sssao_height = height;
		if(sssao_width != ssao_width || sssao_height != ssao_height) {
			ssao_width = sssao_width;
			ssao_height = sssao_height;
			
			_wglBindTexture(_wGL_TEXTURE_2D, framebuffer_ssao_color);
			_wglTexImage2D(_wGL_TEXTURE_2D, 0, _wGL_RGB, ssao_width, ssao_height, 0, _wGL_RGB, _wGL_UNSIGNED_BYTE, (ByteBuffer) null);
		}
		
		int[] viewport = _wglGetParameter(_wGL_VIEWPORT, 4);
		_wglBindFramebuffer(framebuffer_ssao);
		_wglViewport(0, 0, ssao_width, ssao_height);
		_wglUseProgram(programs.get("/glsl/ssao_a.glsl"));
		setUniforms(uniforms.get("/glsl/ssao_a.glsl"));
		_wglColorMask(true, false, false, false);
		_wglDrawArrays(_wGL_TRIANGLES, 0, 6);
		_wglColorMask(true, true, true, true);
		_wglViewport(viewport[0], viewport[1], viewport[2], viewport[3]);
	}
	*/

	public static void setupPipeline(String[] shaders, String[] load) {
		pipeline_tmp = shaders;
		for(int i = 0; i < load.length; i++) {
			if(!programs.containsKey(load[i])) {
				ProgramGL prog = _wglCreateProgram();
				ShaderGL f = _wglCreateShader(_wGL_FRAGMENT_SHADER);
				
				_wglShaderSource(f, _wgetShaderHeader() + "\n" + fileContents(load[i]));
				_wglCompileShader(f);
				
				if (!_wglGetShaderCompiled(f)) System.err.println(("\n" + _wglGetShaderInfoLog(f)).replace("\n", "\n["+load[i]+"][CC_FRAG] ") + "\n");

				_wglAttachShader(prog, pvert_shader);
				_wglAttachShader(prog, f);
				_wglLinkProgram(prog);
				_wglDetachShader(prog, pvert_shader);
				_wglDetachShader(prog, f);
				_wglDeleteShader(f);
				
				if(!_wglGetProgramLinked(prog)) {
					System.err.println(("\n"+_wglGetProgramInfoLog(prog)).replace("\n", "\n["+load[i]+"][LINKER] ") + "\n");
					pipeline_tmp = new String[0];
					return;
				}
				
				_wglUseProgram(prog);
				
				UniformGL c = _wglGetUniformLocation(prog, "f_color");
				if(c != null) _wglUniform1i(c, 0);
				UniformGL d = _wglGetUniformLocation(prog, "f_depth");
				if(d != null) _wglUniform1i(d, 1);
				
				_wglBindAttributeLocation(prog, 0, "a_pos");
				
				if(_wglGetUniformLocation(prog, "ssao_kernel[0]") != null) {
					EaglercraftRandom r = new EaglercraftRandom("eeeaglerrENOPHILEr".hashCode());
					for(int j = 0; j < 24; j++) {
						float x = r.nextFloat() * 2.0f - 1.0f;
						float y = r.nextFloat() * 2.0f - 1.0f;
						float z = r.nextFloat() * 2.0f - 1.0f;
						
						float s = 0.3f + 0.7f * r.nextFloat();
						float hypot = (1.0f / (float) Math.sqrt(x*x + y*y + z*z)) * s;
						x *= hypot;
						y *= hypot;
						z *= hypot;
						
						_wglUniform3f(_wglGetUniformLocation(prog, "ssao_kernel["+j+"]"), x, y, z);
					}
				}
				
				programs.put(load[i], prog);
				uniforms.put(load[i], new UniformGL[] {
						_wglGetUniformLocation(prog, "screenSize"),
						_wglGetUniformLocation(prog, "randomFloat"),
						_wglGetUniformLocation(prog, "f_ssao"),
						_wglGetUniformLocation(prog, "matrix_p"),
						_wglGetUniformLocation(prog, "matrix_p_inv"),
						_wglGetUniformLocation(prog, "f_bloom"),
						_wglGetUniformLocation(prog, "direction"),
						_wglGetUniformLocation(prog, "randomInter"),
						_wglGetUniformLocation(prog, "randomInterF")
				});
			}
		}
	}

}
