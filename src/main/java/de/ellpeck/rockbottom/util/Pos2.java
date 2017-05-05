package de.ellpeck.rockbottom.util;

public class Pos2{

    private int x;
    private int y;

    public Pos2(){
        this(0, 0);
    }

    public Pos2(int x, int y){
        this.set(x, y);
    }

    public int getX(){
        return this.x;
    }

    public int getY(){
        return this.y;
    }

    public Pos2 set(int x, int y){
        this.x = x;
        this.y = y;

        return this;
    }

    public Pos2 add(int x, int y){
        return this.set(this.x+x, this.y+y);
    }

    @Override
    public boolean equals(Object o){
        if(this == o){
            return true;
        }
        if(o == null || this.getClass() != o.getClass()){
            return false;
        }

        Pos2 pos2 = (Pos2)o;
        return this.x == pos2.x && this.y == pos2.y;
    }

    @Override
    public int hashCode(){
        int result = this.x;
        result = 31*result+this.y;
        return result;
    }
}
