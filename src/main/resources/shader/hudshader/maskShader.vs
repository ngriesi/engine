#version 330

layout (location=0) in vec3 position;

out vec3 mvPos;

uniform mat4 projModelMatrix;

void main(){
    gl_Position = projModelMatrix * vec4(position, 1.0);
    mvPos = position;
}