#version 150 core

in vec4 vertexColorPass;
in vec2 texCoordPass;
in vec2 breakTexCoordPass;

out vec4 fragColor;

uniform sampler2D texImage;
uniform sampler2D breakImage;

void main(){
    vec4 textureColor = texture(texImage, texCoordPass);

    if(textureColor.w > 0.0){
        vec4 breakColor = texture(breakImage, breakTexCoordPass);
        if(breakColor.w > 0.0){
            fragColor = vertexColorPass * breakColor;
            return;
        }
    }

    fragColor = vertexColorPass * textureColor;
}
