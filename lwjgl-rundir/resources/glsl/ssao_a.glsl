#line 0
precision highp float;

varying lowp vec2 pos;

uniform sampler2D f_color;
uniform sampler2D f_depth;

uniform mat4 matrix_p;
uniform mat4 matrix_p_inv;

uniform vec3 ssao_kernel[24];

float LinearizeDepth(float zoverw){
	return (2.0 * 0.001) / (4000.0 + 0.001 - zoverw * (4000.0 - 0.001));
}

void main(){
	float r1 = sin(dot(pos,vec2(4.245693,3.24942)));
	
	vec4 posW = matrix_p_inv * vec4(pos * 2.0 - 1.0, texture2D(f_depth, pos).r, 1.0);
	posW.xyz /= posW.w;
	
	//int samples = int(clamp(32.0 - posW.z * -0.5, 8.0, 24.0));
	
	float f = 0.0;
	for(int i = 0; i < 24; ++i){
		vec4 trans = matrix_p * vec4(posW.xyz + (ssao_kernel[i] * 2.0) * (0.5 + vec3((fract(r1 * (2434.64775 * i)) - 0.5), (fract(r1 * (4365.76945 * i)) - 0.5), (fract(r1 * (2354.95634 * i)) - 0.5)) * 0.5), 1.0);
		trans.xyz /= trans.w;
		float A = LinearizeDepth(trans.z);
		float B = LinearizeDepth(texture2D(f_depth, trans.xy * 0.5 + 0.5).r);
		
		f += ((A < B && B - A < 0.02) ? 0.0 : (2.2 / 24.0));
	}
	
	gl_FragColor = vec4(clamp(f, 0.0, 1.0), 0.0, 0.0, 1.0);
}
