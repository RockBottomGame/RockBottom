/*
 * This file ("AnimationRow.java") is part of the RockBottomAPI by Ellpeck.
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

package de.ellpeck.rockbottom.assets;

public class AnimationRow{

    private final float[] frameTimes;
    private final float totalTime;

    public AnimationRow(float[] frameTimes){
        this.frameTimes = frameTimes;

        float accumulator = 0F;
        for(float f : frameTimes){
            accumulator += f;
        }
        this.totalTime = accumulator;
    }

    public int getFrameAmount(){
        return this.frameTimes.length;
    }

    public float getTotalTime(){
        return this.totalTime;
    }

    public float getTime(int frame){
        return this.frameTimes[frame];
    }
}
