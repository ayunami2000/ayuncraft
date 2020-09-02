#line 0
precision lowp float;

varying lowp vec2 pos;

uniform sampler2D f_color;
uniform sampler2D f_depth;
uniform sampler2D f_ssao;
uniform ivec2 screenSize;
void main(){
	vec2 a_pixel = (1.0 / screenSize) * 3.0;
	gl_FragColor = vec4((texture2D(f_ssao, pos).r) * texture2D(f_color, pos).rgb, 1.0);
}
