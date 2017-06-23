/*
 * This file ("PlayerInfo.java") is part of the RockBottomAPI by Ellpeck.
 * View the source code at <https://github.com/Ellpeck/RockBottomAPI>.
 *
 * The RockBottomAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The RockBottomAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the RockBottomAPI. If not, see <http://www.gnu.org/licenses/>.
 */

package de.ellpeck.rockbottom.render;

import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.anim.Animation;
import de.ellpeck.rockbottom.api.data.set.DataSet;
import de.ellpeck.rockbottom.api.net.NetUtil;
import de.ellpeck.rockbottom.api.render.IPlayerDesign;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;
import io.netty.buffer.ByteBuf;
import org.newdawn.slick.Color;

public class PlayerDesign implements IPlayerDesign{

    private final int[] indices = new int[LAYERS.size()];
    private final Animation[] animations = new Animation[LAYERS.size()];

    private String name;
    private Color color;

    public PlayerDesign(){
        for(int i = 0; i < LAYERS.size(); i++){
            this.setAnimation(i);
        }
    }

    @Override
    public void setAnimation(int layer){
        IResourceName animName = LAYERS.get(layer).get(this.indices[layer]);
        String layerName = LAYER_NAMES.get(layer);

        IResourceName path = animName.addPrefix("player."+layerName+".");
        this.animations[layer] = RockBottomAPI.getGame().getAssetManager().getAnimation(path);
    }

    @Override
    public void save(DataSet set){
        for(int i = 0; i < this.indices.length; i++){
            set.addInt("design_layer_"+i, this.indices[i]);
        }
        set.addString("design_name", this.name);

        set.addFloat("design_color_r", this.color.r);
        set.addFloat("design_color_g", this.color.g);
        set.addFloat("design_color_b", this.color.b);
    }

    @Override
    public void load(DataSet set){
        for(int i = 0; i < this.indices.length; i++){
            int index = set.getInt("design_layer_"+i);

            if(index != this.indices[i]){
                this.indices[i] = index;
                this.setAnimation(i);
            }
        }

        this.name = set.getString("design_name");
        this.color = new Color(set.getFloat("design_color_r"), set.getFloat("design_color_g"), set.getFloat("design_color_b"));
    }

    @Override
    public void toBuf(ByteBuf buf){
        for(int index : this.indices){
            buf.writeInt(index);
        }
        NetUtil.writeStringToBuffer(this.name, buf);

        buf.writeFloat(this.color.r);
        buf.writeFloat(this.color.g);
        buf.writeFloat(this.color.b);
    }

    @Override
    public void fromBuf(ByteBuf buf){
        for(int i = 0; i < this.indices.length; i++){
            int index = buf.readInt();

            if(index != this.indices[i]){
                this.indices[i] = index;
                this.setAnimation(i);
            }
        }
        this.name = NetUtil.readStringFromBuffer(buf);
        this.color = new Color(buf.readFloat(), buf.readFloat(), buf.readFloat());
    }

    @Override
    public int[] getIndices(){
        return this.indices;
    }

    @Override
    public Animation[] getAnimations(){
        return this.animations;
    }

    @Override
    public Color getColor(){
        return this.color;
    }

    @Override
    public void setColor(Color color){
        this.color = color;
    }

    @Override
    public String getName(){
        return this.name;
    }

    @Override
    public void setName(String name){
        this.name = name;
    }
}
