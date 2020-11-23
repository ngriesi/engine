#version 330

in vec3 mvPos;
in vec2 outTexCord;
in vec4 color;


out vec4 fragColor;
out float gl_FragDepth;

uniform int transparencyMode;

uniform float keepCornerProportion;

uniform vec2 cornerScale;

uniform float cornerSize;

uniform int useTexture;

uniform sampler2D texture2d;

uniform float edgeValue;

uniform vec4 edgeStartColor;
uniform vec4 edgeEndColor;
uniform float edgeSize;
uniform int edgeBlendMode;

void main() {

    if(useTexture==2) {
        float smoothing = 0.9f / (0.5 * 100);

        float full = 0.5f;

        float dist = texture(texture2d, outTexCord).a;
        float alphaText = smoothstep(full - smoothing, full + smoothing, dist);
        fragColor = vec4(color.rgb, color.a * alphaText);
    } else {
        float alpha;

        if(cornerSize != 0) {
            if(keepCornerProportion==0) {
                alpha = length(max(vec2(abs(mvPos.x),abs(mvPos.y)) - cornerScale + cornerSize, 0.0));
            } else if(keepCornerProportion < 0) {
                alpha = length(max(vec2(abs(mvPos.x),abs(mvPos.y/keepCornerProportion)) - cornerScale + cornerSize, 0.0));
            } else {
                alpha = length(max(vec2(abs(mvPos.x * keepCornerProportion),abs(mvPos.y)) + cornerScale + cornerSize, 0.0));
            }
        } else if(edgeSize != 1) {
            if(keepCornerProportion==0) {
                alpha = max(abs(mvPos.x),abs(mvPos.y)) * 2;
            } else if(keepCornerProportion < 0) {
                alpha = max(abs(mvPos.x),0.5f - (0.5f - abs(mvPos.y))/-keepCornerProportion) * 2;
            } else {
                alpha = max((0.5f - (0.5f - abs(mvPos.x)) * keepCornerProportion),abs(mvPos.y)) * 2;
            }
        } else {
            alpha = 0;
        }

        if(cornerSize > 0 && alpha > cornerSize) {
            discard;
        }

        float cornerValue = alpha<edgeSize?0:alpha;


        if(useTexture==1) {
            fragColor = texture(texture2d, outTexCord);
        } else if(useTexture==0) {
            fragColor = color;
        }

        if(cornerValue > 0) {
            if(cornerSize > 0 && edgeSize > 0) {
                vec4 edgeColor = (cornerValue - edgeSize)/(cornerSize - edgeSize) * edgeStartColor + (1- ((cornerValue - edgeSize)/(cornerSize - edgeSize))) * edgeEndColor;
                fragColor = edgeBlendMode==0?edgeColor:(edgeColor * edgeColor.a + (fragColor * (1-edgeColor.a)));
            } else if (edgeSize > 0) {
                vec4 edgeColor = (cornerValue - edgeSize)/(edgeSize) * edgeStartColor + (1- ((cornerValue - edgeSize)/(edgeSize))) * edgeEndColor;
                fragColor = edgeBlendMode==0?edgeColor:(edgeColor * edgeColor.a + (fragColor * (1-edgeColor.a)));
            }
        }
    }



    if((transparencyMode==0 && fragColor.a == 0)) {
        discard;
    }
}





