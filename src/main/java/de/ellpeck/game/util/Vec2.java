package de.ellpeck.game.util;

public class Vec2{

    private int x;
    private int y;

    public Vec2(){
        this(0, 0);
    }

    public Vec2(int x, int y){
        this.set(x, y);
    }

    public int getX(){
        return this.x;
    }

    public int getY(){
        return this.y;
    }

    public Vec2 set(int x, int y){
        this.x = x;
        this.y = y;

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

        Vec2 vec2 = (Vec2)o;
        return this.x == vec2.x && this.y == vec2.y;
    }

    @Override
    public int hashCode(){
        int result = this.x;
        result = 31*result+this.y;
        return result;
    }
}
