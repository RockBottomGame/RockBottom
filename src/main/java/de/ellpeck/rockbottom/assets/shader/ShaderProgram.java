package de.ellpeck.rockbottom.assets.shader;

import de.ellpeck.rockbottom.api.assets.IShaderProgram;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryStack;

import java.util.HashMap;
import java.util.Map;

public class ShaderProgram implements IShaderProgram{

    private static ShaderProgram boundProgram;

    private final int id;
    private final Map<String, Integer> attributeLocations = new HashMap<>();
    private final Map<String, Integer> uniformLocations = new HashMap<>();
    private int componentsPerVertex;

    public ShaderProgram(Shader vertex, Shader fragment){
        this.id = GL20.glCreateProgram();
        GL20.glAttachShader(this.id, vertex.getId());
        GL20.glAttachShader(this.id, fragment.getId());

        vertex.dispose();
        fragment.dispose();
    }

    @Override
    public void setDefaultValues(int width, int height){
        this.bindFragmentDataLocation("fragColor", 0);
        this.link();
        this.bind();

        int floatSize = Float.BYTES;
        this.pointVertexAttribute(true, "position", 2, 8*floatSize, 0);
        this.pointVertexAttribute(true, "color", 4, 8*floatSize, 2*floatSize);
        this.pointVertexAttribute(true, "texCoord", 2, 8*floatSize, 6*floatSize);

        this.setComponentsPerVertex(8);

        this.setUniform("model", new Matrix4f());
        this.setUniform("view", new Matrix4f());
        this.setUniform("texImage", 0);

        this.updateProjection(width, height);
    }

    @Override
    public void updateProjection(int width, int height){
        this.setUniform("projection", new Matrix4f().ortho(0F, width, height, 0F, -1F, 1F));
    }

    @Override
    public void bindFragmentDataLocation(String name, int location){
        GL30.glBindFragDataLocation(this.id, location, name);
    }

    @Override
    public void link(){
        GL20.glLinkProgram(this.id);

        if(GL20.glGetProgrami(this.id, GL20.GL_LINK_STATUS) != GL11.GL_TRUE){
            throw new RuntimeException("Couldn't compile shader program:\n"+GL20.glGetProgramInfoLog(this.id));
        }
    }

    @Override
    public void bind(){
        if(boundProgram != this){
            GL20.glUseProgram(this.id);
            boundProgram = this;
        }
    }

    @Override
    public int getAttributeLocation(String name){
        this.bind();
        return this.attributeLocations.computeIfAbsent(name, s -> GL20.glGetAttribLocation(this.id, s));
    }

    @Override
    public int getUniformLocation(String name){
        this.bind();
        return this.uniformLocations.computeIfAbsent(name, s -> GL20.glGetUniformLocation(this.id, s));
    }

    @Override
    public void pointVertexAttribute(boolean enable, String name, int size, int stride, int offset){
        int location = this.getAttributeLocation(name);
        if(enable){
            GL20.glEnableVertexAttribArray(location);
        }
        GL20.glVertexAttribPointer(location, size, GL11.GL_FLOAT, false, stride, offset);
    }

    @Override
    public void setUniform(String name, Matrix4f matrix){
        MemoryStack stack = MemoryStack.stackPush();
        GL20.glUniformMatrix4fv(this.getUniformLocation(name), false, matrix.get(stack.mallocFloat(4*4)));
        stack.pop();
    }

    @Override
    public void setUniform(String name, int value){
        GL20.glUniform1i(this.getUniformLocation(name), value);
    }

    @Override
    public void setUniform(String name, float f){
        GL20.glUniform1f(this.getUniformLocation(name), f);
    }

    @Override
    public void setUniform(String name, float x, float y){
        GL20.glUniform2f(this.getUniformLocation(name), x, y);
    }

    @Override
    public void setUniform(String name, float x, float y, float z){
        GL20.glUniform3f(this.getUniformLocation(name), x, y, z);
    }

    @Override
    public void unbind(){
        if(boundProgram == this){
            unbindAll();
        }
    }

    public static void unbindAll(){
        GL20.glUseProgram(0);
        boundProgram = null;
    }

    @Override
    public int getId(){
        return this.id;
    }

    @Override
    public void setComponentsPerVertex(int components){
        this.componentsPerVertex = components;
    }

    @Override
    public int getComponentsPerVertex(){
        return this.componentsPerVertex;
    }

    @Override
    public void dispose(){
        this.unbind();
        GL20.glDeleteProgram(this.id);
    }
}
