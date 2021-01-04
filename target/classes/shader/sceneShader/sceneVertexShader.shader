
#version 330

// constants
const int MAX_JOINTS = 50;
const int MAX_WEIGHTS = 3;

// input variables form the java code
layout (location=0) in vec3 position;
layout (location=1) in vec2 texCoord;
layout (location=2) in vec3 vertexNormal;
layout (location=3) in vec3 tangent;
layout (location=4) in vec3 biTangent;
layout (location=5) in vec3 weights;
layout (location=6) in ivec3 jointIndices;

// ouput variables to the fragment shader
out vec2 outTexCoord;
out vec3 mvVertexNormal;
out vec3 mvVertexPos;
out vec4 mLightviewVertexPosition;
out mat4 outModelViewMatrix;
out mat3 TBN;

out vec3 posT;

// uniform variables
uniform mat4 modelViewMatrix;
uniform mat4 projectionMatrix;
uniform mat4 modelLightViewMatrix;
uniform mat4 orthoProjectionMatrix;
uniform mat4 jointTransforms[MAX_JOINTS];
uniform int isAnimation;

void main()
{

    posT = position;

    // animation position calculations
    vec4 totalLocalPos = vec4(0.0);
    vec4 totalNormal = vec4(0.0);

    if (isAnimation==1)
    {
        for (int i = 0; i < MAX_WEIGHTS; i++)
        {
            mat4 jointTransform = jointTransforms[jointIndices[i]];
            vec4 posePosition = jointTransform * vec4(position, 1.0);
            totalLocalPos += posePosition * weights[i];


            vec4 worldNormal = jointTransform * vec4(vertexNormal, 0.0);
            totalNormal += worldNormal * weights[i];
        }
    } else {
        totalNormal = vec4(vertexNormal,0.0);
        totalLocalPos = vec4(position,1.0);
    }


    // gets the position of the Item from the modelViewMatrix
    vec4 mvPos =  modelViewMatrix * totalLocalPos;

    // applies the projection matrix to the items position
    gl_Position = projectionMatrix * mvPos;

    // passes on the texture cordinate
    outTexCoord = texCoord;

    // calculates the normal vector of the vertex relative to the world
    mvVertexNormal = normalize(modelViewMatrix * totalNormal).xyz;

    // the position of the vertex relative to the world
    mvVertexPos = mvPos.xyz;

    // calculating the vector for the shadow calculation
    mLightviewVertexPosition = orthoProjectionMatrix * modelLightViewMatrix * vec4(position, 1.0);

    // passes the model view matrix to the fragment shader
    outModelViewMatrix = modelViewMatrix;

    // calculate TBN matrix for normal map orientation
    vec3 T = normalize(vec3(modelViewMatrix * vec4(tangent, 0.0)));
    vec3 B = normalize(vec3(modelViewMatrix * vec4(biTangent, 0.0)));
    vec3 N = normalize(vec3(modelViewMatrix * totalNormal));
    TBN = mat3(T,B,N);

}