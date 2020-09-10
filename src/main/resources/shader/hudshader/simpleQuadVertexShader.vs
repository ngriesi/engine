#version 330

layout (location=0) in vec3 position;

out vec3 mvPos;
out vec4 color;

uniform mat4 projModelMatrix;
uniform vec4 inColor;


void main(){
    gl_Position = projModelMatrix * vec4(position, 1.0);
    mvPos = position;

    color = inColor;
}