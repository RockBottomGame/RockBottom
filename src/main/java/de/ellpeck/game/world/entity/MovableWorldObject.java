package de.ellpeck.game.world.entity;

import de.ellpeck.game.util.BoundBox;
import de.ellpeck.game.world.World;

import java.util.List;

public abstract class MovableWorldObject{

    public World world;

    public double x;
    public double y;

    public double motionX;
    public double motionY;

    public boolean collidedHor;
    public boolean collidedVert;
    public boolean onGround;

    public MovableWorldObject(World world){
        this.world = world;
    }

    public void setPos(double x, double y){
        this.x = x;
        this.y = y;
    }

    public void move(double motionX, double motionY){
        if(motionX != 0 || motionY != 0){
            double motionXBefore = motionX;
            double motionYBefore = motionY;

            BoundBox ownBox = this.getBoundingBox();
            BoundBox tempBox = ownBox.copy().add(this.x+motionX, this.y+motionY);
            List<BoundBox> boxes = this.world.getCollisions(tempBox);

            if(motionY != 0){
                if(!boxes.isEmpty()){
                    BoundBox currBox = tempBox.set(ownBox).add(this.x, this.y);

                    for(BoundBox box : boxes){
                        if(motionY != 0){
                            if(!box.isEmpty()){
                                motionY = box.getYDistanceWithMax(currBox, motionY);
                            }
                        }
                        else{
                            break;
                        }
                    }
                }

                this.y += motionY;
            }

            if(motionX != 0){
                if(!boxes.isEmpty()){
                    BoundBox currBox = tempBox.set(ownBox).add(this.x, this.y);

                    for(BoundBox box : boxes){
                        if(motionX != 0){
                            if(!box.isEmpty()){
                                motionX = box.getXDistanceWithMax(currBox, motionX);
                            }
                        }
                        else{
                            break;
                        }
                    }
                }

                this.x += motionX;
            }

            this.collidedHor = motionX != motionXBefore;
            this.collidedVert = motionY != motionYBefore;
            this.onGround = this.collidedVert && motionYBefore < 0;
        }
    }

    public abstract BoundBox getBoundingBox();
}
