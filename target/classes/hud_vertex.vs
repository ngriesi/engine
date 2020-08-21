#version 330

layout (location=0) in vec3 position;
layout (location=1) in vec2 texCord;
layout (location=2) in vec3 vertexNormal;

out vec2 outTexCord;
out vec3 mvPos;
out vec4 color;

uniform mat4 projModelMatrix;
uniform vec4 colors[];

uniform int useColorShade;


void main(){
    gl_Position = projModelMatrix * vec4(position, 1.0);
    mvPos = position;
    outTexCord = texCord;


    if(useColorShade == 0) {
        color = colors[0];
    } else {
        float red = (((0.5 - position.x) * colors[0].x) + ((0.5 + position.x) * colors[1].x)) + (((0.5 - position.y) * colors[2].x) + ((0.5 +position.y) * colors[3].x));
        float green = (((0.5 - position.x) * colors[0].y) + ((0.5 + position.x) * colors[1].y)) + (((0.5 - position.y) * colors[2].y) + ((0.5 +position.y) * colors[3].y));
        float blue = (((0.5 - position.x) * colors[0].z) + ((0.5 + position.x) * colors[1].z)) + (((0.5 - position.y) * colors[2].z) + ((0.5 +position.y) * colors[3].z));
        float alpha = (((0.5 - position.x) * colors[0].w) + ((0.5 + position.x) * colors[1].w)) + (((0.5 - position.y) * colors[2].w) + ((0.5 +position.y) * colors[3].w));

        color = vec4(red*0.5,green*0.5,blue*0.5,alpha*0.5);
    }
}