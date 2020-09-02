#line 0
precision lowp float;

varying lowp vec2 pos;

uniform sampler2D f_color;
uniform sampler2D f_depth;

uniform ivec2 direction;
uniform ivec2 screenSize;

void main(){
	vec3 color = vec3(0.0);
	vec2 pix = direction * vec2(1.0 / screenSize);
	color += texture2D(f_color, pos - pix * 10.0).rgb * 0.011090;
	color += texture2D(f_color, pos - pix * 9.0).rgb * 0.016196;
	color += texture2D(f_color, pos - pix * 8.0).rgb * 0.022729;
	color += texture2D(f_color, pos - pix * 7.0).rgb * 0.030651;
	color += texture2D(f_color, pos - pix * 6.0).rgb * 0.039717;
	color += texture2D(f_color, pos - pix * 5.0).rgb * 0.049455;
	color += texture2D(f_color, pos - pix * 4.0).rgb * 0.059173;
	color += texture2D(f_color, pos - pix * 3.0).rgb * 0.068033;
	color += texture2D(f_color, pos - pix * 2.0).rgb * 0.075163;
	color += texture2D(f_color, pos - pix * 1.0).rgb * 0.079795;
	color += texture2D(f_color, pos).rgb * 0.081402;
	color += texture2D(f_color, pos + pix * 1.0).rgb * 0.079795;
	color += texture2D(f_color, pos + pix * 2.0).rgb * 0.075163;
	color += texture2D(f_color, pos + pix * 3.0).rgb * 0.068033;
	color += texture2D(f_color, pos + pix * 4.0).rgb * 0.059173;
	color += texture2D(f_color, pos + pix * 5.0).rgb * 0.049455;
	color += texture2D(f_color, pos + pix * 6.0).rgb * 0.039717;
	color += texture2D(f_color, pos + pix * 7.0).rgb * 0.030651;
	color += texture2D(f_color, pos + pix * 8.0).rgb * 0.022729;
	color += texture2D(f_color, pos + pix * 9.0).rgb * 0.016196;
	color += texture2D(f_color, pos + pix * 10.0).rgb * 0.011090;
	gl_FragColor = vec4(color, 1.0);
}
