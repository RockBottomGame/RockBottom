package de.ellpeck.rockbottom.assets.shader;

import de.ellpeck.rockbottom.api.assets.IShaderProgram;
import de.ellpeck.rockbottom.render.engine.VertexArrayObject;
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
    private final VertexArrayObject vao;
    private final Map<String, Integer> attributeLocations = new HashMap<>();
    private final Map<String, Integer> uniformLocations = new HashMap<>();
    private int componentsPerVertex = 8;
    private int attributeOffset;

    public ShaderProgram(Shader vertex, Shader fragment){
        this.id = GL20.glCreateProgram();
        this.vao = new VertexArrayObject();

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

        this.pointVertexAttribute("position", 2);
        this.pointVertexAttribute("color", 4);
        this.pointVertexAttribute("texCoord", 2);

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
            boundProgram = this;
            GL20.glUseProgram(this.id);
            this.vao.bind();

            for(int i : this.attributeLocations.values()){
                GL20.glEnableVertexAttribArray(i);
            }
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
    public void pointVertexAttribute(String name, int size){
        int location = this.getAttributeLocation(name);
        GL20.glVertexAttribPointer(location, size, GL11.GL_FLOAT, false, this.componentsPerVertex*Float.BYTES, this.attributeOffset);
        this.attributeOffset += size*Float.BYTES;

        GL20.glEnableVertexAttribArray(location);
    }

    @Override
    public void setUniform(String name, Matrix4f matrix){
        int location = this.getUniformLocation(name);
        MemoryStack stack = MemoryStack.stackPush();
        GL20.glUniformMatrix4fv(location, false, matrix.get(stack.mallocFloat(4*4)));
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
            for(int i : this.attributeLocations.values()){
                GL20.glDisableVertexAttribArray(i);
            }

            this.vao.unbind();
            GL20.glUseProgram(0);
            boundProgram = null;
        }
    }

    public static void unbindAll(){
        if(boundProgram != null){
            boundProgram.unbind();
        }
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
    public void draw(int amount){
        this.vao.draw(amount);
    }

    @Override
    public void dispose(){
        this.unbind();
        GL20.glDeleteProgram(this.id);
    }
}
