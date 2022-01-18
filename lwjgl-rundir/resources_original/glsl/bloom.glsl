#line 0
precision lowp float;

varying lowp vec2 pos;

uniform sampler2D f_color;
uniform sampler2D f_depth;
uniform sampler2D f_bloom;

uniform ivec2 screenSize;

void main(){
	vec3 cc = texture2D(f_color, pos).rgb;
	gl_FragColor = vec4(max(cc, cc * vec3(0.5) + texture2D(f_bloom, pos).rgb), 1.0);
}
