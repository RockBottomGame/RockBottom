package de.ellpeck.rockbottom.render.engine;

import de.ellpeck.rockbottom.api.render.engine.IVBO;
import org.lwjgl.opengl.GL15;

import java.nio.FloatBuffer;

public class VertexBufferObject implements IVBO {

    private static int boundVBO;

    private final int id;
    private final boolean isStatic;

    public VertexBufferObject(boolean isStatic) {
        this.id = GL15.glGenBuffers();
        this.isStatic = isStatic;
    }

    public static void unbindAll() {
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        boundVBO = -1;
    }

    @Override
    public void bind() {
        if (boundVBO != this.id) {
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, this.id);
            boundVBO = this.id;
        }
    }

    @Override
    public void data(long size) {
        this.bind();
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, size, this.getDrawMode());
    }

    @Override
    public void subData(FloatBuffer vertices) {
        this.bind();
        GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, vertices);
    }

    @Override
    public int getDrawMode() {
        return this.isStatic ? GL15.GL_STATIC_DRAW : GL15.GL_DYNAMIC_DRAW;
    }

    @Override
    public void unbind() {
        if (boundVBO == this.id) {
            unbindAll();
        }
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public boolean isStatic() {
        return this.isStatic;
    }

    @Override
    public void dispose() {
        this.unbind();
        GL15.glDeleteBuffers(this.id);
    }
}
