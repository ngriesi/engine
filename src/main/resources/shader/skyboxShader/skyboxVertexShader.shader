#version 330

// input variables form the java code
layout (location=0) in vec3 position;
layout (location=1) in vec2 texCord;
layout (location=2) in vec3 vertexNormal;

// output variables to the shader code
out vec2 outTexCord;

// unifomr variables
uniform mat4 modelViewMatrix;
uniform mat4 projectionMatrix;

void main()
{
    // applieing the transformation matrices
    gl_Position = projectionMatrix * modelViewMatrix * vec4(position, 1.0);

    // passing on the texture cordinates
    outTexCord = texCord;
}