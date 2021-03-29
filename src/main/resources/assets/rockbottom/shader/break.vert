#version 150 core

in vec2 position;
in vec4 color;
in vec2 texCoord;
in vec2 breakTexCoord;

out vec4 vertexColorPass;
out vec2 texCoordPass;
out vec2 breakTexCoordPass;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;

void main(){
    vertexColorPass = color;
    texCoordPass = texCoord;
    breakTexCoordPass = breakTexCoord;

    mat4 mvp = projection * view * model;
    gl_Position = mvp * vec4(position, 0.0, 1.0);
}