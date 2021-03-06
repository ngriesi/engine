#version 330

in vec2 outTexCord;
in vec3 mvPos;
in vec4 color;

struct Edge {

    vec4 startColor;
    vec4 endColor;
    float size;
    float position;
    int blendMode;

};

out vec4 fragColor;
out float gl_FragDepth;

uniform sampler2D texture_sampler;
uniform int hasTexture;
uniform int isText;
uniform int maskMode;
uniform int depth;
uniform float keepCornerProportion;
uniform float keepEdgeProportion;
uniform float cornerSize;
uniform float full;
uniform Edge outerEdge;
uniform Edge middleEdge;
uniform Edge innerEdge;

float smoothing;

void useColor(float dist);

void setDepth();

void addEdge(Edge edge, float dist);

void cornerDist(float pow1_1,float pow1_2,float pow1_mult,float pow2_1,float pow2_2,float pow2_div,float value);

void sideDist(float proportion,float pos);

void calculateCorners(float proportionValue1,float proportionValue2);

void handleText();

void main() {


    if(isText == 0) {

        if(cornerSize > 0) {
            if(keepCornerProportion < 0) {
                calculateCorners(-keepCornerProportion,1);
            } else if(keepCornerProportion > 0){
                calculateCorners(1,keepCornerProportion);
            } else {
                calculateCorners(1,1);
            }
        } else {

            if(keepEdgeProportion == 0) {
                 useColor(max(abs(mvPos.x),abs(mvPos.y))*2);
            }
            else if(keepEdgeProportion > 0){
                 useColor(max(abs(mvPos.x * keepEdgeProportion) - 0.5f,abs(mvPos.y))*2);
            } else {
                 useColor(max(abs(mvPos.x),abs(mvPos.y * ((keepEdgeProportion+1)*0.5f - 1)))*2);
            }

        }
    } else {

        handleText();

    }
}

void handleText() {
    float smoothing = 0.9f / (0.5 * 100);

    float distance = texture(texture_sampler, outTexCord).a;
    float alpha = smoothstep(full - smoothing, full + smoothing, distance);
    fragColor = vec4(color.rgb, color.a * alpha);
    setDepth();
}

void useColor(float dist) {
    if( hasTexture == 1) {
        fragColor = color * texture(texture_sampler, outTexCord);
    }else{

        fragColor = color;

        addEdge(outerEdge,dist);
        addEdge(middleEdge,dist);
        addEdge(innerEdge,dist);


    }

    setDepth();

    //fragColor = gl_FragDepth * 7000000 * vec4(1,1,1,1);
}

void addEdge(Edge edge, float dist) {
    if(edge.size == 0) return;
    if(dist > 1 - (edge.position + edge.size) && dist < 1 - edge.position) {
        if(edge.blendMode == 0) {
            fragColor = edge.startColor * (((1 - dist) - edge.position)/edge.size) + edge.endColor * (1 - (((1- dist) - edge.position)/edge.size));
        } else if(edge.blendMode == 1) {
            fragColor = color *  edge.startColor * (((1 - dist) - edge.position)/edge.size) + edge.endColor * (1 - (((1- dist) - edge.position)/edge.size));
        }
    }
}

void setDepth() {
    if( maskMode == 0) {
        if(fragColor.a == 0) {
            discard;
        }
        gl_FragDepth = depth/16777215.0;
    } else {
        gl_FragDepth = depth/16777215.0;
    }
}

void calculateCorners(float proportionValue1, float proportionValue2) {



    float startX =  -0.5 + cornerSize/proportionValue2;
    float endX = -startX;

    float startY = -0.5 + cornerSize * proportionValue1;
    float endY = -startY;

    if(mvPos.x < startX) {
         if(mvPos.y < startY) {         cornerDist(mvPos.x,startX,proportionValue2,mvPos.y,startY,proportionValue1,cornerSize);
         } else if(mvPos.y > endY) {    cornerDist(mvPos.x,startX,proportionValue2,mvPos.y,endY,proportionValue1,cornerSize);
         } else {                       sideDist(cornerSize/proportionValue2,mvPos.x);}
    } else if (mvPos.x > endX) {
         if(mvPos.y < startY) {         cornerDist(mvPos.x,endX,proportionValue2,mvPos.y,startY,proportionValue1,cornerSize);
         } else if(mvPos.y > endY) {    cornerDist(mvPos.x,endX,proportionValue2,mvPos.y,endY,proportionValue1,cornerSize);
         } else {                       sideDist(cornerSize/proportionValue2,mvPos.x);}
    } else {
        sideDist(cornerSize * proportionValue1,mvPos.y);
    }
}



void sideDist(float proportion,float pos) {
    useColor(pow(max((abs(pos) - (0.5f - proportion)) * 2,0),2) * (25)/(pow(proportion * 10,2)));
}

void cornerDist(float pow1_1,float pow1_2,float pow1_mult,float pow2_1,float pow2_2,float pow2_div,float value) {


    float t = pow((pow1_1 - pow1_2) * pow1_mult,2) + pow((pow2_1 - pow2_2)/pow2_div,2) - pow(value,2);

    if(t > 0) {
         fragColor = color * 0;
         setDepth();
    } else {
         useColor(1+t/pow(cornerSize,2));
    }
}
