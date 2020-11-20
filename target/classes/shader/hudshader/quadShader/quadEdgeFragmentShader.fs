#version 330

in vec3 mvPos;
in vec4 color;

out vec4 fragColor;

void useColor();

uniform float keepCornerProportion;
uniform vec2 cornerScale;
uniform float cornerSize;



void main() {
    useColor();
}

void useColor() {

    float alpha = 0;

    if(cornerSize > 0) {
        if(keepCornerProportion==0) {
            alpha = length(max(vec2(abs(mvPos.x),abs(mvPos.y))  - cornerScale + cornerSize, 0.0));
        } else if(keepCornerProportion < 0) {
            alpha = length(max(vec2(abs(mvPos.x),abs(mvPos.y/keepCornerProportion)) - cornerScale + cornerSize, 0.0));
        } else {
            alpha = length(max(vec2(abs(mvPos.x * keepCornerProportion),abs(mvPos.y)) + cornerScale + cornerSize, 0.0));
        }
        alpha = alpha * 2;
    } else {
        alpha = max(abs(mvPos.x),abs(mvPos.y)) * 2;
    }


    float a = alpha>0.5f?1:0;


    fragColor = vec4(color.r,color.g,color.b,color.a);
}






