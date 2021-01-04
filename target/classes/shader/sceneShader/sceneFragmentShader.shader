#version 330

//CONSTANTS
// maximum number of point lights
const int MAX_POINT_LIGHTS = 5;

// maximum number of spot lights
const int MAX_SPOT_LIGHTS = 5;

// STRUCTS
// Attenuation describes the decrease in light intensity over distance
struct Attenuation
{
    float constant;
    float linear;
    float exponent;
};

// PointLight is a light at a specific point that shines in all directions
struct PointLight
{
    vec3 color;
    // Light position is assumed to be in view cordinates
    vec3 position;
    float intensity;
    Attenuation att;
};

// SpotLight is a like a point light but it emmits its light only in one diraction in a 3D cone
struct SpotLight
{
    vec3 coneDirection;
    float cutoff;
    PointLight pointLight;
};

// DirectionalLight is a light that only has a direction not a position because it is infinitly far away (behaves like the sun)
struct DirectionalLight
{
    vec3 color;
    vec3 direction;
    float intensity;
};

// The Material struct contains the attributes of a surface
struct Material
{
    vec4 ambient;
    vec4 diffuse;
    vec4 specular;
    int hasTexture;
    float reflectance;
    int hasNormalMap;
};

// The Fog struct contains the values to calculate the fog in the scene
struct Fog
{
    int activeValue;
    vec3 color;
    float density;
};

// VARIABLES
// input variables from the vertex shader
in vec2 outTexCoord;
in vec3 mvVertexNormal;
in vec3 mvVertexPos;
in mat4 outModelViewMatrix;
in vec4 mLightviewVertexPosition;
in mat3 TBN;

in vec3 posT;

// output variables
out vec4 fragColor;

// global variables
vec4 ambientC;
vec4 diffuseC;
vec4 specularC;

// UNIFORMS
// texture uniforms
uniform sampler2D texture_sampler;
uniform sampler2D normalMap;
uniform sampler2D shadowMap;

// light uniforms
uniform vec3 ambientLight;
uniform float specularPower;
uniform PointLight pointLights[MAX_POINT_LIGHTS];
uniform DirectionalLight directionalLight;
uniform SpotLight spotLights[MAX_SPOT_LIGHTS];

// Fog of the scene
uniform Fog fog;

// Material of the fragment
uniform Material material;

// Position of the camera
uniform vec3 camera_pos;

// method calculates the normal for the current fragment
vec3 calcNormal(Material material, vec3 normal, vec2 text_cord, mat4 modelViewMatrix)
{
    vec3 newNormal = normal;
    // check if the material has a normal map
    if(material.hasNormalMap == 1)
    {
        // access the data form the normal map
        newNormal = texture(normalMap, text_cord).rgb;

        // change the vectors to be in the rang of [-1;1] instead of [0;1]
        newNormal = newNormal * 2 - 1;

        // apply transformation to world coordinates
        newNormal = normalize(TBN * newNormal);

        // transform the vector into view coordinate space
        newNormal = normalize(modelViewMatrix * vec4(newNormal, 0.0)).xyz;

    }
    return newNormal;
}

// method to set the colors of the fragment
void setupColors(Material material, vec2 textCord)
{
    if(material.hasTexture == 1)
    {
        ambientC = texture(texture_sampler, textCord);
        //ambientC = vec4(ambientC.r,ambientC.r,ambientC.r,1);
        diffuseC = ambientC;
        specularC = ambientC;
    }
    else
    {
        ambientC = material.ambient;
        diffuseC = material.diffuse;
        specularC = material.specular;
    }
}

// calculates the Light color (diffuse and specular) component without attenuation
vec4 calcLightColor(vec3 light_color, float light_intensity, vec3 position, vec3 to_light_dir, vec3 normal)
{
    vec4 diffuseColor = vec4(0, 0, 0, 0);
    vec4 specColor = vec4(0, 0, 0, 0);

    // gives a value between 1 if the vertex faces the light and 0 if
    // the angle between its normal and the to_light_source vector is
    // greater than 90°
    float diffuseFactor = max(dot(normal, to_light_dir), 0.0);

    // calculates the diffuse color of the fragment
    diffuseColor = diffuseC * vec4(light_color,1.0) * light_intensity * diffuseFactor;

    // SPECULAR LIGHT
    // calculates the vector from the fragment to the camera
    vec3 camera_direction = normalize(-position);

    // calculates the vector from the light source to the fragment
    vec3 from_light_source = - to_light_dir;

    // calculates the vector of the reflected light
    vec3 reflected_light = normalize(reflect(from_light_source,normal));

    // gives a value between 0 and 1 dependend on how big the angle between the
    // reflected light vector and the vector to the camera is. 1 means the light gets
    // reflected directly into the camera
    float specularFactor = max( dot(camera_direction, reflected_light), 0.0);

    // adjusts the specularFactor value
    specularFactor = pow(specularFactor,specularPower);

    // calculates the specular power of the fragment
    specColor = specularC * specularFactor * material.reflectance * vec4(light_color, 1.0);

    // returns the color of the light at this fragment
    return (diffuseColor + specColor);
}

// Method calculates the color set by the point lights
vec4 calcPointLight(PointLight light, vec3 position, vec3 normal)
{

    // LIGHT COLOR
    // calculates the vector from the vertex position to the light position
    vec3 light_direction = light.position - position;

    // normalizes the vector to the light position
    vec3 to_light_dir = normalize(light_direction);

    // calculates the color of the light (diffuse and specular component)
    vec4 light_color = calcLightColor(light.color, light.intensity, position,to_light_dir,normal);


    //ATTENUATION
    // calculates the distance form the fragment to the light source
    float distance = length(light_direction);

    // calculates the attenuation
    float attenuationInv = light.att.constant + light.att.linear * distance + light.att.exponent * distance * distance;


    // returns the colors the PointLight adds to the fragment
    return (light_color) / attenuationInv;
}

// Method calculates the color added by the spot lights
vec4 calcSpotLights( SpotLight light, vec3 position, vec3 normal)
{

    // LIGHT COLOR
    // calculates the vector from the vertex position to the light position
    vec3 light_direction = light.pointLight.position - position;

    // normalizes the vector to the light position
    vec3 to_light_dir = normalize(light_direction);

    // calculates the vector from the light to the fragment
    vec3 from_light_direction = - to_light_dir;

    // calculates the dot product between the vector form the light to the fragment
    // and the cone direction vector of the spot
    float spot_alpha = dot(from_light_direction, normalize(light.coneDirection));

    vec4 color = vec4(0,0,0,0);

    // calculates the light intensity and color if the fragment is inside the cone
    if( spot_alpha > light.cutoff)
    {
        color = calcPointLight(light.pointLight, position,normal);
        color *= (1.0 - (1.0 - spot_alpha)/(1.0 - light.cutoff));
    }

    // returns the final light color for this spot light
    return color;
}

// Method calculates the color added by the directional light
vec4 calcDirectionalLight( DirectionalLight light, vec3 position, vec3 normal)
{
    // calculates the light color
    return calcLightColor(light.color, light.intensity, position, normalize(light.direction),normal);

}

// Method calculates the effect the fog has to the color of this fragment
vec4 calcFog(vec3 pos, vec4 color, Fog fog, vec3 ambientLight, DirectionalLight directionalLight)
{
    // calculates teh fog color taking the ambient and directional light into consideration
    vec3 fogColor = fog.color * (ambientLight + directionalLight.color * directionalLight.intensity);

    // distance between the fragment and the camera
    float distance = length(pos);

    // calculates the fog factor and sets it between 0 and 1
    float fogFactor = 1.0 / exp((distance * fog.density) * (distance * fog.density));
    fogFactor = clamp( fogFactor, 0.0, 1.0);

    // calculates the result color
    vec3 resultColor = mix(fogColor, color.xyz, fogFactor);
    // returns the result color
    return vec4(resultColor.xyz,color.w);
}

// method for calculating the shadow
float calcShadow( vec4 position)
{

    vec3 projCords = position.xyz;

    // transformation from screen coordinates to texture coordinates
    projCords = projCords * 0.5 + 0.5;

    // bias factor to fix shadows beeing at wrong places du to the limitations of the depth buffer
    float bias = 0.05f;

    // factor that get subtracted from one and than multiplied with the fragment color
    float shadowFactor = 0.0;

    // calculates the size of one pixel of the shadow map
    vec2 inc = 1.0 / textureSize(shadowMap, 0);

    // averages the shadow values with its neighbours to get smother edges
    for(int row = -1; row <= 1; ++row)
    {
        for(int col = -1; col <= 1; ++col)
        {
            // averaging of the shadow value
            float textDepth = texture(shadowMap, projCords.xy + vec2(row,col) * inc).r;
            shadowFactor += projCords.z - bias > textDepth ? 1.0 : 0.0;
        }
    }

    // divided by 9 because it is composed out of 9 seperate shadow values that got added together
    shadowFactor /= 9.0;

    if ( projCords.z - bias < texture(shadowMap, projCords.xy).r )
    {
        // current fragment is not in shade
        shadowFactor = 0;
    }

    return 1 - shadowFactor;
}

void main()
{
    // sets up (light) colors
    setupColors(material, outTexCoord);

    // calculates the normals
    vec3 currNormal = calcNormal(material,mvVertexNormal,outTexCoord,outModelViewMatrix);

    // calculates the diffuse and specular component of the DirectionalLight
    vec4 diffuseSpecularComp = calcDirectionalLight(directionalLight,mvVertexPos, currNormal);

    // calculates the diffuse and specular component of the PointLights
    for(int i = 0; i < MAX_POINT_LIGHTS; i++)
    {
        if( pointLights[i].intensity > 0)
        {
            diffuseSpecularComp += calcPointLight(pointLights[i], mvVertexPos, currNormal);
        }
    }


    // calculates the diffuse and specular component of the SpotLights
    for(int i = 0; i < MAX_SPOT_LIGHTS; i++)
    {
        if( spotLights[i].pointLight.intensity > 0)
        {
            diffuseSpecularComp += calcSpotLights(spotLights[i], mvVertexPos, currNormal);
        }
    }

    // calculates the shadow
    float shadow = calcShadow(mLightviewVertexPosition);

    // sets the final fragment color after the lights
    fragColor = clamp(ambientC * vec4(ambientLight, 1) + diffuseSpecularComp, 0 , 1);

    // applies the fog effect to the fragment
    if(fog.activeValue == 1)
    {
        fragColor = calcFog(mvVertexPos, fragColor, fog, ambientLight, directionalLight);
    }

    if(posT != vec3(2,2,2))
    {
        fragColor = vec4((posT + 1)/2,1);
    }

}