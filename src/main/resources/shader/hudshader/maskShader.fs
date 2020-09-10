#version 330

in vec3 mvPos;

out vec4 fragColor;
out float gl_FragDepth;

uniform int useTransparent;

void main() {

    if(useTransparent==0 && fragColor.a == 0) {
        discard;
    }
}





