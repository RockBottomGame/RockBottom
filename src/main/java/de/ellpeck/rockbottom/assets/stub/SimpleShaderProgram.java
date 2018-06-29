package de.ellpeck.rockbottom.assets.stub;

import de.ellpeck.rockbottom.assets.shader.Shader;
import de.ellpeck.rockbottom.assets.shader.ShaderProgram;
import org.lwjgl.opengl.GL20;

public class SimpleShaderProgram extends ShaderProgram {

    public SimpleShaderProgram(int width, int height) {
        super(getVertex(), getFragment());
        this.setDefaultValues(width, height);
    }

    private static Shader getVertex() {
        return new Shader(GL20.GL_VERTEX_SHADER, "#version 150 core\n" +
                '\n' +
                "in vec2 position;\n" +
                "in vec4 color;\n" +
                "in vec2 texCoord;\n" +
                '\n' +
                "out vec4 vertexColorPass;\n" +
                "out vec2 texCoordPass;\n" +
                '\n' +
                "uniform mat4 model;\n" +
                "uniform mat4 view;\n" +
                "uniform mat4 projection;\n" +
                '\n' +
                "void main(){\n" +
                "    vertexColorPass = color;\n" +
                "    texCoordPass = texCoord;\n" +
                '\n' +
                "    mat4 mvp = projection * view * model;\n" +
                "    gl_Position = mvp * vec4(position, 0.0, 1.0);\n" +
                '}');
    }

    private static Shader getFragment() {
        return new Shader(GL20.GL_FRAGMENT_SHADER, "#version 150 core\n" +
                '\n' +
                "in vec4 vertexColorPass;\n" +
                "in vec2 texCoordPass;\n" +
                '\n' +
                "out vec4 fragColor;\n" +
                '\n' +
                "uniform sampler2D texImage;\n" +
                '\n' +
                "void main(){\n" +
                "    if(texCoordPass.x == 0.0 && texCoordPass.y == 0.0){\n" +
                "        fragColor = vertexColorPass;\n" +
                "    }\n" +
                "    else{\n" +
                "        vec4 textureColor = texture(texImage, texCoordPass);\n" +
                "        fragColor = vertexColorPass * textureColor;\n" +
                "    }\n" +
                '}');
    }
}
