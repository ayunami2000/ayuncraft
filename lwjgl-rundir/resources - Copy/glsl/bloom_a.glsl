#line 0
precision lowp float;

varying lowp vec2 pos;

uniform sampler2D f_color;
uniform sampler2D f_depth;

uniform ivec2 screenSize;

vec3 ramp(vec3 v){
	return (v * vec3(0.5)) * (1.0 + pow(v.r*2.5+v.g-(v.b*v.g*3.0), 3.0)*0.06);
}

void main(){
	vec2 offset = 1.0 / screenSize;
	gl_FragColor = vec4((ramp(texture2D(f_color, pos).rgb) + ramp(texture2D(f_color, pos + vec2(offset.x, 0.0)).rgb) + ramp(texture2D(f_color, pos + vec2(0.0, offset.y)).rgb) + ramp(texture2D(f_color, pos + offset).rgb)) * 0.2, 1.0);
}
