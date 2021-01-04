#version 330

void main()
{
    // passing the depth value
    gl_FragDepth = gl_FragCoord.z;
}