#version 330

layout (location=0) in vec3 position;
layout (location=1) in vec2 texCord;

out vec2 outTexCord;
out vec3 mvPos;


uniform mat4 projModelMatrix;


void main() {
    gl_Position = projModelMatrix * vec4(position, 1.0);
    mvPos = position;
    outTexCord = texCord;


}