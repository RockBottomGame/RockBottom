package de.ellpeck.rockbottom.render.engine;

import de.ellpeck.rockbottom.api.render.engine.IVAO;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

public class VertexArrayObject implements IVAO {

    private static int boundVAO;

    private final int id;

    public VertexArrayObject() {
        this.id = GL30.glGenVertexArrays();
    }

    public static void unbindAll() {
        GL30.glBindVertexArray(0);
        boundVAO = -1;
    }

    @Override
    public void bind() {
        if (boundVAO != this.id) {
            GL30.glBindVertexArray(this.id);
            boundVAO = this.id;
        }
    }

    @Override
    public void draw(int amount) {
        this.bind();
        GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, amount);
    }

    @Override
    public void unbind() {
        if (boundVAO == this.id) {
            unbindAll();
        }
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public void dispose() {
        this.unbind();
        GL30.glDeleteVertexArrays(this.id);
    }
}
