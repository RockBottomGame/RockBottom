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

import de.ellpeck.rockbottom.init.AbstractGame;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.data.IDataManager;
import de.ellpeck.rockbottom.api.data.set.DataSet;
import de.ellpeck.rockbottom.api.render.IPlayerDesign;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;
import org.newdawn.slick.Color;

import java.util.List;

public class PlayerDesign implements IPlayerDesign{

    private static final String[] DEFAULT_NAMES = new String[]{"Bob", "Doley", "Jason", "Huffelpuff", "Megan", "Jennifer", "Bottle", "Bus Stop", "ThePlayer99", "Genelele", "Karina", "Heinz", "Ketchup", "Dan", "David", "Penguin", "Hubert", "Penny", "Vinny", "Xx_TheBestLP_xX", "Bozo", "Patrick", "InigoMontoya", "Pebbles", "Noodles", "Milkshake"};
    private String name;
    private Color color;

    private int base;
    private Color eyeColor;

    private int shirt;
    private Color shirtColor;

    private int sleeves;
    private Color sleevesColor;

    private int pants;
    private Color pantsColor;

    private int footwear;
    private Color footwearColor;

    private int hair;
    private Color hairColor;

    private int accessory;

    private int mouth;

    private int eyebrows;
    private Color eyebrowsColor;

    private int beard;
    private Color beardColor;

    public static String getRandomName(){
        return DEFAULT_NAMES[Util.RANDOM.nextInt(DEFAULT_NAMES.length)];
    }

    public static void randomizeDesign(IPlayerDesign design){
        design.setName(getRandomName());
        design.setFavoriteColor(Util.randomColor(Util.RANDOM));

        design.setBase(Util.RANDOM.nextInt(BASE.size()));
        design.setEyeColor(Util.randomColor(Util.RANDOM));

        design.setHair(Util.RANDOM.nextInt(HAIR.size()));
        design.setHairColor(Util.randomColor(Util.RANDOM));

        design.setShirt(Util.RANDOM.nextInt(SHIRT.size()));
        design.setShirtColor(Util.randomColor(Util.RANDOM));

        design.setSleeves(Util.RANDOM.nextInt(SLEEVES.size()));
        design.setSleevesColor(Util.randomColor(Util.RANDOM));

        design.setPants(Util.RANDOM.nextInt(PANTS.size()));
        design.setPantsColor(Util.randomColor(Util.RANDOM));

        design.setFootwear(Util.RANDOM.nextInt(FOOTWEAR.size()));
        design.setFavoriteColor(Util.randomColor(Util.RANDOM));

        design.setAccessory(Util.RANDOM.nextInt(ACCESSORIES.size()));

        design.setEyebrows(Util.RANDOM.nextInt(EYEBROWS.size()));
        design.setEyebrowsColor(Util.randomColor(Util.RANDOM));

        design.setMouth(Util.RANDOM.nextInt(MOUTH.size()));

        design.setBeard(Util.RANDOM.nextInt(BEARD.size()));
        design.setBeardColor(Util.randomColor(Util.RANDOM));
    }

    private static int loadIndex(String s, List<IResourceName> list){
        if(s != null && !s.isEmpty()){
            int index = list.indexOf(RockBottomAPI.createRes(s));
            return Math.max(0, index);
        }
        else{
            return 0;
        }
    }

    private static String saveIndex(int index, List<IResourceName> list){
        if(index >= 0 && index < list.size()){
            IResourceName name = list.get(index);
            if(name != null){
                return name.toString();
            }
        }
        return "";
    }

    @Override
    public void saveToFile(){
        IDataManager dataManager = AbstractGame.get().getDataManager();

        DataSet set = new DataSet();
        this.save(set);
        set.write(dataManager.getPlayerDesignFile());
    }

    @Override
    public void loadFromFile(){
        IDataManager dataManager = AbstractGame.get().getDataManager();

        DataSet set = new DataSet();
        set.read(dataManager.getPlayerDesignFile());
        this.load(set);
    }

    @Override
    public void save(DataSet set){
        set.addString("name", this.name);
        set.addInt("color", Util.toIntColor(this.color));

        set.addString("base", saveIndex(this.base, BASE));
        set.addInt("eye_color", Util.toIntColor(this.eyeColor));

        set.addString("shirt", saveIndex(this.shirt, SHIRT));
        set.addInt("shirt_color", Util.toIntColor(this.shirtColor));

        set.addString("sleeves", saveIndex(this.sleeves, SLEEVES));
        set.addInt("sleeves_color", Util.toIntColor(this.sleevesColor));

        set.addString("pants", saveIndex(this.pants, PANTS));
        set.addInt("pants_color", Util.toIntColor(this.pantsColor));

        set.addString("footwear", saveIndex(this.footwear, FOOTWEAR));
        set.addInt("footwear_color", Util.toIntColor(this.footwearColor));

        set.addString("hair", saveIndex(this.hair, HAIR));
        set.addInt("hair_color", Util.toIntColor(this.hairColor));

        set.addString("accessory", saveIndex(this.accessory, ACCESSORIES));

        set.addString("eyebrows", saveIndex(this.eyebrows, EYEBROWS));
        set.addInt("eyebrows_color", Util.toIntColor(this.eyebrowsColor));

        set.addString("mouth", saveIndex(this.mouth, MOUTH));

        set.addString("beard", saveIndex(this.beard, BEARD));
        set.addInt("beard_color", Util.toIntColor(this.beardColor));
    }

    @Override
    public void load(DataSet set){
        this.name = set.getString("name");
        this.color = new Color(set.getInt("color"));

        this.base = loadIndex(set.getString("base"), BASE);
        this.eyeColor = new Color(set.getInt("eye_color"));

        this.shirt = loadIndex(set.getString("shirt"), SHIRT);
        this.shirtColor = new Color(set.getInt("shirt_color"));

        this.sleeves = loadIndex(set.getString("sleeves"), SLEEVES);
        this.sleevesColor = new Color(set.getInt("sleeves_color"));

        this.pants = loadIndex(set.getString("pants"), PANTS);
        this.pantsColor = new Color(set.getInt("pants_color"));

        this.footwear = loadIndex(set.getString("footwear"), FOOTWEAR);
        this.footwearColor = new Color(set.getInt("footwear_color"));

        this.hair = loadIndex(set.getString("hair"), HAIR);
        this.hairColor = new Color(set.getInt("hair_color"));

        this.accessory = loadIndex(set.getString("accessory"), ACCESSORIES);

        this.eyebrows = loadIndex(set.getString("eyebrows"), EYEBROWS);
        this.eyebrowsColor = new Color(set.getInt("eyebrows_color"));

        this.mouth = loadIndex(set.getString("mouth"), MOUTH);

        this.beard = loadIndex(set.getString("beard"), BEARD);
        this.beardColor = new Color(set.getInt("beard_color"));
    }

    @Override
    public Color getFavoriteColor(){
        return this.color;
    }

    @Override
    public void setFavoriteColor(Color color){
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

    @Override
    public int getBase(){
        return this.base;
    }

    @Override
    public Color getEyeColor(){
        return this.eyeColor;
    }

    @Override
    public int getShirt(){
        return this.shirt;
    }

    @Override
    public Color getShirtColor(){
        return this.shirtColor;
    }

    @Override
    public int getSleeves(){
        return this.sleeves;
    }

    @Override
    public Color getSleevesColor(){
        return this.sleevesColor;
    }

    @Override
    public int getPants(){
        return this.pants;
    }

    @Override
    public Color getPantsColor(){
        return this.pantsColor;
    }

    @Override
    public int getFootwear(){
        return this.footwear;
    }

    @Override
    public Color getFootwearColor(){
        return this.footwearColor;
    }

    @Override
    public int getHair(){
        return this.hair;
    }

    @Override
    public Color getHairColor(){
        return this.hairColor;
    }

    @Override
    public int getAccessory(){
        return this.accessory;
    }

    @Override
    public int getEyebrows(){
        return this.eyebrows;
    }

    @Override
    public int getMouth(){
        return this.mouth;
    }

    @Override
    public int getBeard(){
        return this.beard;
    }

    @Override
    public Color getBeardColor(){
        return this.beardColor;
    }

    @Override
    public Color getEyebrowsColor(){
        return this.eyebrowsColor;
    }

    @Override
    public void setBase(int base){
        this.base = base;
    }

    @Override
    public void setEyeColor(Color eyeColor){
        this.eyeColor = eyeColor;
    }

    @Override
    public void setShirt(int shirt){
        this.shirt = shirt;
    }

    @Override
    public void setShirtColor(Color shirtColor){
        this.shirtColor = shirtColor;
    }

    @Override
    public void setSleeves(int sleeves){
        this.sleeves = sleeves;
    }

    @Override
    public void setSleevesColor(Color sleevesColor){
        this.sleevesColor = sleevesColor;
    }

    @Override
    public void setPants(int pants){
        this.pants = pants;
    }

    @Override
    public void setPantsColor(Color pantsColor){
        this.pantsColor = pantsColor;
    }

    @Override
    public void setFootwear(int footwear){
        this.footwear = footwear;
    }

    @Override
    public void setFootwearColor(Color footwearColor){
        this.footwearColor = footwearColor;
    }

    @Override
    public void setHair(int hair){
        this.hair = hair;
    }

    @Override
    public void setHairColor(Color hairColor){
        this.hairColor = hairColor;
    }

    @Override
    public void setAccessory(int accessory){
        this.accessory = accessory;
    }

    @Override
    public void setEyebrows(int eyebrows){
        this.eyebrows = eyebrows;
    }

    @Override
    public void setMouth(int mouth){
        this.mouth = mouth;
    }

    @Override
    public void setEyebrowsColor(Color eyebrowsColor){
        this.eyebrowsColor = eyebrowsColor;
    }

    @Override
    public void setBeard(int beard){
        this.beard = beard;
    }

    @Override
    public void setBeardColor(Color beardColor){
        this.beardColor = beardColor;
    }
}
