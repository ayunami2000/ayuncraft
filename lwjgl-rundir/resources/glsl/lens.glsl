#line 0
precision lowp float;

varying lowp vec2 pos;

uniform sampler2D f_color;
uniform sampler2D f_depth;

uniform vec2 screenSize;
uniform vec2 randomInter;
uniform float randomInterF;

vec2 barrelDistortion(vec2 coord, float amt){
	vec2 cc = coord - 0.5;
	float dist = dot(cc, cc);
	return coord + cc * dist * amt;
}

#define amount 0.1
#define abberation 0.015

float noise(vec2 seed){
	float r = sin(dot(seed, vec2(12.10482130, 4.32894663)) * 763.345734);
	float r1 = fract(r * (1.0 + randomInter.x*34735.49057645));
	float r2 = fract(r * (1.0 + randomInter.y*74764.63566345));
    return (r1*r1*r1)*(randomInterF) + (r2*r2*r2)*(1.0 - randomInterF);
}

void main(){
	vec2 yeer = screenSize * 0.3;
	vec2 pos2 = pos * 0.93 + 0.035;
	vec3 color = vec3(0.0);
	color += vec3(0.8, 0.0, 0.0) * texture2D(f_color, barrelDistortion(pos2, (amount + abberation + abberation))).rgb;
	color += vec3(0.0, 1.0, 0.1) * texture2D(f_color, barrelDistortion(pos2, (amount + abberation))).rgb;
	color += vec3(0.2, 0.0, 0.9) * texture2D(f_color, barrelDistortion(pos2, (amount))).rgb;
	color *= 0.8;
	color += 0.2 * texture2D(f_color, barrelDistortion(pos2, (amount * 1.5))).rgb;
	float r = noise(floor(pos * yeer) / yeer);
	gl_FragColor = vec4(color.rgb + ((0.08 + color.rgb) * (r * 0.07 / (0.06+color.r+color.g*4.0+color.b))), 1.0);
}
