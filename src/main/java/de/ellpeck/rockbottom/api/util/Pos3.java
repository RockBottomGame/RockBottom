package de.ellpeck.rockbottom.api.util;

public class Pos3{

    private int x;
    private int y;
    private int z;

    public Pos3(){
        this(0, 0, 0);
    }

    public Pos3(int x, int y, int z){
        this.set(x, y, z);
    }

    public int getX(){
        return this.x;
    }

    public int getY(){
        return this.y;
    }

    public int getZ(){
        return this.z;
    }

    public Pos3 set(int x, int y, int z){
        this.x = x;
        this.y = y;
        this.z = z;

        return this;
    }

    @Override
    public boolean equals(Object o){
        if(this == o){
            return true;
        }
        if(o == null || this.getClass() != o.getClass()){
            return false;
        }

        Pos3 pos3 = (Pos3)o;
        return this.x == pos3.x && this.y == pos3.y && this.z == pos3.z;
    }

    @Override
    public int hashCode(){
        int result = this.x;
        result = 31*result+this.y;
        result = 31*result+this.z;
        return result;
    }
}
