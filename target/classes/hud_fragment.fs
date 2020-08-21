#version 330

in vec2 outTexCord;
in vec3 mvPos;
in vec4 color;

out vec4 fragColor;
out float gl_FragDepth;

uniform sampler2D texture_sampler;
uniform int hasTexture;
uniform int isText;
uniform int maskMode;
uniform int depth;
uniform float keepCornerProportion;
uniform float cornerSize;
uniform float full;

float smoothing;

void useColor(float dist);

void setDepth();


void main() {


    if(isText == 0) {
        float value = cornerSize;

        float startX;
        float endX;

        float startY;
        float endY;

        if(cornerSize > 0) {
        if(keepCornerProportion < 0) {

            startX =  -0.5 + value;
            endX = -startX;

            startY = -0.5 + value * -keepCornerProportion;
            endY = -startY;

            if(mvPos.x < startX) {
                if(mvPos.y < startY) {
                    float t = pow((mvPos.x - startX),2) + pow((mvPos.y - startY) / -keepCornerProportion,2) - pow(value,2);
                    if(t > 0) {
                        fragColor = color * 0;
                    } else {
                        useColor(1+t/pow(value,2));
                    }
                } else if(mvPos.y > endY) {
                    float t =  pow((mvPos.x - startX),2) + pow((mvPos.y - endY) / -keepCornerProportion,2) - pow(value,2);
                    if(t > 0) {
                        fragColor = color * 0;
                    } else {
                        useColor(1+t/pow(value,2));
                    }
                } else {
                    useColor((abs(mvPos.x * 2) - (1 - 2 * value)) * 1/value );
                }
            } else if (mvPos.x > endX) {
                if(mvPos.y < startY) {
                    float t = pow((mvPos.x - endX),2) + pow((mvPos.y - startY) / -keepCornerProportion,2) - pow(value,2);
                    if(t > 0) {
                        fragColor = color * 0;
                    } else {
                        useColor(t);
                    }
                } else if(mvPos.y > endY) {
                    float t = pow((mvPos.x - endX),2) + pow((mvPos.y - endY) / -keepCornerProportion,2) - pow(value,2);
                    if(t > 0) {
                         fragColor = color * 0;
                    } else {
                         useColor(t);
                    }
                } else {
                    useColor(0.5f - mvPos.x);
                }
            } else {
                useColor(abs(mvPos.y));
            }
        } else if(keepCornerProportion > 0){

            startX =  -0.5 + value/keepCornerProportion;
            endX = -startX;

            startY = -0.5 + value;
            endY = -startY;

            if(mvPos.x < startX) {
                if(mvPos.y < startY) {
                    float t = -pow((mvPos.x - startX) * keepCornerProportion,2) + pow(mvPos.y - startY,2) - pow(value,2);
                    if(t > 0) {
                         fragColor = color * 0;
                    } else {
                        useColor(t);
                    }
                } else if(mvPos.y > endY) {
                    float t = -pow((mvPos.x - startX) * keepCornerProportion,2) + pow(mvPos.y - endY,2) - pow(value,2);
                    if(t > 0) {
                         fragColor = color * 0;
                    } else {
                        useColor(t);
                    }
                } else {
                    useColor(mvPos.y);
                }
            } else if (mvPos.x > endX) {
                if(mvPos.y < startY) {
                    float t = -pow((mvPos.x - endX) * keepCornerProportion,2) + pow(mvPos.y - startY,2) - pow(value,2);
                    if(t > 0) {
                         fragColor = color * 0;
                    } else {
                        useColor(t);
                    }
                } else if(mvPos.y > endY) {
                    float t = -pow((mvPos.x - endX) * keepCornerProportion,2) + pow(mvPos.y - endY,2) - pow(value,2);
                    if(t > 0) {
                         fragColor = color * 0;
                    } else {
                         useColor(t);
                    }
                } else {
                    useColor(mvPos.y);
                }
            } else {
                useColor(mvPos.x);
            }

        } else {
            startX =  -0.5 + value;
            endX = -startX;

            startY = -0.5 + value;
            endY = -startY;

            if(mvPos.x < startX) {
                if(mvPos.y < startY) {

                    if(pow((mvPos.x - startX),2) + pow(mvPos.y - startY,2) > pow(value,2)) {
                         fragColor = color * 0;
                    } else {
                        useColor(1);
                    }
                } else if(mvPos.y > endY) {
                    if(pow((mvPos.x - startX),2) + pow(mvPos.y - endY,2) > pow(value,2)) {
                         fragColor = color * 0;
                    } else {
                        useColor(1);
                    }
                } else {
                    useColor(1);
                }
            } else if (mvPos.x > endX) {
                if(mvPos.y < startY) {
                    if(pow((mvPos.x - endX),2) + pow(mvPos.y - startY,2) > pow(value,2)) {
                         fragColor = color * 0;
                    } else {
                        useColor(1);
                    }
                } else if(mvPos.y > endY) {
                    if(pow((mvPos.x - endX),2) + pow(mvPos.y - endY,2) > pow(value,2)) {
                         fragColor = color * 0;
                    } else {
                         useColor(1);
                    }
                } else {
                    useColor(1);
                }
            } else {
                useColor(1);
            }
        }
        } else {
            useColor(1);
        }
    } else {


        float smoothing = 0.9f / (0.5 * 100);

        float distance = texture(texture_sampler, outTexCord).a;
        float alpha = smoothstep(full - smoothing, full + smoothing, distance);
        fragColor = vec4(color.rgb, color.a * alpha);


        setDepth();
    }
}

void useColor(float dist) {
    if( hasTexture == 1) {
        fragColor = color * texture(texture_sampler, outTexCord);
    }else{
        fragColor = color * dist;


    }

    setDepth();

    //fragColor = gl_FragDepth * 7000000 * vec4(1,1,1,1);
}

void setDepth() {
    if( maskMode == 0) {
            gl_FragDepth = depth/16777215.0;
            if(fragColor.a == 0) {
                discard;
            }
        } else {
                gl_FragDepth = depth/16777215.0;

        }

}