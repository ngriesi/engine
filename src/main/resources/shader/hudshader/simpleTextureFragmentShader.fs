#version 330

in vec2 outTexCord;
in vec3 mvPos;


out vec4 fragColor;

uniform sampler2D texture_sampler;

void main() {
    fragColor = vec4(1,1,1,1) * texture(texture_sampler, outTexCord);
}