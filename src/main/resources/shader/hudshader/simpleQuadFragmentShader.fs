#version 330

in vec3 mvPos;
in vec4 color;

out vec4 fragColor;

void useColor();



void main() {
    useColor();
}

void useColor() {
    fragColor = color;

}






