#version 330

// input variables form java code
layout (location = 0) in vec3 position;
layout (location = 1) in vec2 texCord;
layout (location = 2) in vec3 vertexNormals;

// uniform variables
uniform mat4 modelLightViewMatrix;
uniform mat4 orthoProjectionMatrix;

void main()
{
    // setting the position
    gl_Position = orthoProjectionMatrix * modelLightViewMatrix * vec4(position, 1.0f);
}