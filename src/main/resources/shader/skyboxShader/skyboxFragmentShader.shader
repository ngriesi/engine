#version 330

// input variables form the vertex shader
in vec2 outTexCord;
in vec3 mvPos;

// output variables
out vec4 fragColor;

// uniform variables
uniform sampler2D texture_sampler;
uniform vec3 ambientLight;

void main()
{
    // applieing the texture and the ambient light
    fragColor = vec4(ambientLight, 1) * texture(texture_sampler, outTexCord);
}