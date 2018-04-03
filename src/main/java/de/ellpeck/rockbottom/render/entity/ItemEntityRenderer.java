/*
 * This file ("ItemEntityRenderer.java") is part of the RockBottomAPI by Ellpeck.
 * View the source code at <https://github.com/RockBottomGame/>.
 * View information on the project at <https://rockbottom.ellpeck.de/>.
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
 *
 * © 2017 Ellpeck
 */

package de.ellpeck.rockbottom.render.entity;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.IRenderer;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.item.Item;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.render.entity.IEntityRenderer;
import de.ellpeck.rockbottom.api.render.item.IItemRenderer;
import de.ellpeck.rockbottom.api.util.ApiInternal;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.world.entity.EntityItem;

@ApiInternal
public class ItemEntityRenderer implements IEntityRenderer<EntityItem>{

    @Override
    public void render(IGameInstance game, IAssetManager manager, IRenderer g, IWorld world, EntityItem entity, float x, float y, int light){
        ItemInstance instance = entity.getItem();
        if(instance != null){
            Item item = instance.getItem();
            IItemRenderer renderer = item.getRenderer();
            if(renderer != null){
                float bob = (float)Math.sin(entity.ticksExisted/20D%(2*Math.PI))*0.1F;
                renderer.render(game, manager, g, item, instance, x-0.25F, y+bob-0.45F, 0.5F, light);
            }
        }
    }
}
