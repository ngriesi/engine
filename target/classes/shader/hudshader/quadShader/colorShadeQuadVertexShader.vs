#version 330

layout (location=0) in vec3 position;

out vec3 mvPos;
out vec4 color;

uniform mat4 projModelMatrix;
uniform vec4 colors[];
uniform int useColorShade;


void main(){
    gl_Position = projModelMatrix * vec4(position, 1.0);
    mvPos = position;

    if(useColorShade==1) {

        float red = (((0.5 - position.x) * colors[0].x) + ((0.5 + position.x) * colors[1].x)) + (((0.5 - position.y) * colors[2].x) + ((0.5 +position.y) * colors[3].x));
        float green = (((0.5 - position.x) * colors[0].y) + ((0.5 + position.x) * colors[1].y)) + (((0.5 - position.y) * colors[2].y) + ((0.5 +position.y) * colors[3].y));
        float blue = (((0.5 - position.x) * colors[0].z) + ((0.5 + position.x) * colors[1].z)) + (((0.5 - position.y) * colors[2].z) + ((0.5 +position.y) * colors[3].z));
        float alpha = (((0.5 - position.x) * colors[0].w) + ((0.5 + position.x) * colors[1].w)) + (((0.5 - position.y) * colors[2].w) + ((0.5 +position.y) * colors[3].w));

        color = vec4(red,green,blue,alpha);
    } else {
        color = colors[0];
    }

}