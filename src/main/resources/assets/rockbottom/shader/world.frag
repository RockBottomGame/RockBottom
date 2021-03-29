#version 150 core

in vec4 vertexColorPass;
in vec2 texCoordPass;

out vec4 fragColor;

uniform sampler2D texImage;

void main(){
    if(texCoordPass.x == 0.0 && texCoordPass.y == 0.0){
        fragColor = vertexColorPass;
    }
    else{
        vec4 textureColor = texture(texImage, texCoordPass);
        fragColor = vertexColorPass * textureColor;
    }
}