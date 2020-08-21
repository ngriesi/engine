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

float cornerDist(float pow1_1,float pow1_2,float pow1_mult,float pow2_1,float pow2_2,float pow2_div,float value);

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

                    float t = cornerDist(mvPos.x,startX,1,mvPos.y,startY,-keepCornerProportion,value);

                    if(t > 0) {
                        fragColor = color * 0;
                    } else {
                        useColor(1+t/pow(value,2));
                    }
                } else if(mvPos.y > endY) {

                    float t = cornerDist(mvPos.x,startX,1,mvPos.y,endY,-keepCornerProportion,value);

                    if(t > 0) {
                        fragColor = color * 0;
                    } else {
                        useColor(1+t/pow(value,2));
                    }
                } else {
                    useColor(pow((abs(mvPos.x) - (0.5f - value)) * 2,2) * 25/(pow(value * 10,2)));
                }
            } else if (mvPos.x > endX) {
                if(mvPos.y < startY) {

                    float t = cornerDist(mvPos.x,endX,1,mvPos.y,startY,-keepCornerProportion,value);

                    if(t > 0) {
                        fragColor = color * 0;
                    } else {
                        useColor(1+t/pow(value,2));
                    }
                } else if(mvPos.y > endY) {

                    float t = cornerDist(mvPos.x,endX,1,mvPos.y,endY,-keepCornerProportion,value);

                    if(t > 0) {
                         fragColor = color * 0;
                    } else {
                         useColor(1+t/pow(value,2));
                    }
                } else {
                    useColor(pow((abs(mvPos.x) - (0.5f - value)) * 2,2) * 25/(pow(value * 10,2)));
                }
            } else {
                float val2 = value *-keepCornerProportion;
                float temp1 = (abs(mvPos.y) - (0.5f - val2)) * 2;
                float temp2 = temp1>0?pow(temp1,2):0;
                useColor(temp2 * (25)/(pow(val2 * 10,2)));
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

float cornerDist(float pow1_1,float pow1_2,float pow1_mult,float pow2_1,float pow2_2,float pow2_div,float value) {

    return pow((pow1_1 - pow1_2) * pow1_mult,2) + pow((pow2_1 - pow2_2)/pow2_div,2) - pow(value,2);

}
