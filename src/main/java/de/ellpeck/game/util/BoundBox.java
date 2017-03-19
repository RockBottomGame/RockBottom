package de.ellpeck.game.util;

public class BoundBox{

    private double minX;
    private double minY;

    private double maxX;
    private double maxY;

    public BoundBox(){
        this(0, 0, 0, 0);
    }

    public BoundBox(double minX, double minY, double maxX, double maxY){
        this.set(minX, minY, maxX, maxY);
    }

    public BoundBox set(BoundBox box){
        return this.set(box.minX, box.minY, box.maxX, box.maxY);
    }

    public BoundBox set(double minX, double minY, double maxX, double maxY){
        this.minX = Math.min(minX, maxX);
        this.minY = Math.min(minY, maxY);

        this.maxX = Math.max(maxX, minX);
        this.maxY = Math.max(maxY, minY);

        return this;
    }

    public BoundBox add(double x, double y){
        this.minX += x;
        this.minY += y;

        this.maxX += x;
        this.maxY += y;

        return this;
    }

    public boolean intersects(BoundBox other){
        return this.intersects(other.minX, other.minY, other.maxX, other.maxY);
    }

    public boolean intersects(double minX, double minY, double maxX, double maxY){
        return this.minX < maxX && this.maxX > minX && this.minY < maxY && this.maxY > minY;
    }

    public boolean contains(double x, double y){
        return this.minX <= x && this.minY <= y && this.maxX >= x && this.maxY >= y;
    }

    public boolean isEmpty(){
        return this.minX >= this.maxX || this.minY >= this.maxY;
    }

    @Override
    public boolean equals(Object o){
        if(this == o){
            return true;
        }
        if(o == null || this.getClass() != o.getClass()){
            return false;
        }

        BoundBox boundBox = (BoundBox)o;
        return Double.compare(boundBox.minX, this.minX) == 0 && Double.compare(boundBox.minY, this.minY) == 0 && Double.compare(boundBox.maxX, this.maxX) == 0 && Double.compare(boundBox.maxY, this.maxY) == 0;
    }

    @Override
    public int hashCode(){
        int result;
        long temp;
        temp = Double.doubleToLongBits(this.minX);
        result = (int)(temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(this.minY);
        result = 31*result+(int)(temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(this.maxX);
        result = 31*result+(int)(temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(this.maxY);
        result = 31*result+(int)(temp ^ (temp >>> 32));
        return result;
    }

    public double getMinX(){
        return this.minX;
    }

    public double getMinY(){
        return this.minY;
    }

    public double getMaxX(){
        return this.maxX;
    }

    public double getMaxY(){
        return this.maxY;
    }

    public double getXDistanceWithMax(BoundBox other, double offsetX){
        if(other.maxY > this.minY && other.minY < this.maxY){
            if(offsetX > 0 && other.maxX <= this.minX){
                double diff = this.minX-other.maxX;
                if(diff < offsetX){
                    offsetX = diff;
                }
            }
            else if(offsetX < 0 && other.minX >= this.maxX){
                double diff = this.maxX-other.minX;
                if(diff > offsetX){
                    offsetX = diff;
                }
            }
        }
        return offsetX;
    }

    public double getYDistanceWithMax(BoundBox other, double offsetY){
        if(other.maxX > this.minX && other.minX < this.maxX){
            if(offsetY > 0 && other.maxY <= this.minY){
                double diff = this.minY-other.maxY;
                if(diff < offsetY){
                    offsetY = diff;
                }
            }
            else if(offsetY < 0 && other.minY >= this.maxY){
                double diff = this.maxY-other.minY;
                if(diff > offsetY){
                    offsetY = diff;
                }
            }
        }
        return offsetY;
    }

    public BoundBox copy(){
        return new BoundBox(this.minX, this.minY, this.maxX, this.maxY);
    }
}
