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
import de.ellpeck.rockbottom.api.data.IDataManager;
import de.ellpeck.rockbottom.api.data.set.DataSet;
import de.ellpeck.rockbottom.api.render.IPlayerDesign;
import de.ellpeck.rockbottom.api.util.Colors;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;

import java.util.List;

public class PlayerDesign implements IPlayerDesign{

    private static final String[] DEFAULT_NAMES = new String[]{"Jake", "Craig", "Mariana", "Louise", "Rosie", "Flo", "Luke", "Abbie", "James", "Chris", "Kieran", "Fatima", "Adam", "Giles", "Megan", "Tim", "Calypso", "Hayley", "Aimee", "Megan", "Eleanor"};

    private String name;
    private int color;
    private boolean female;

    private int base;
    private int eyeColor;

    private int shirt;
    private int shirtColor;

    private int sleeves;
    private int sleevesColor;

    private int pants;
    private int pantsColor;

    private int footwear;
    private int footwearColor;

    private int hair;
    private int hairColor;

    private int accessory;

    private int mouth;

    private int eyebrows;
    private int eyebrowsColor;

    private int beard;
    private int beardColor;

    public static String getRandomName(){
        return DEFAULT_NAMES[Util.RANDOM.nextInt(DEFAULT_NAMES.length)];
    }

    public static void randomizeDesign(IPlayerDesign design){
        design.setName(getRandomName());
        design.setFavoriteColor(Colors.random(Util.RANDOM));
        design.setFemale(Util.RANDOM.nextBoolean());

        design.setBase(Util.RANDOM.nextInt(BASE.size()));
        design.setEyeColor(Colors.random(Util.RANDOM));

        design.setHair(Util.RANDOM.nextInt(HAIR.size()));
        design.setHairColor(Colors.random(Util.RANDOM));

        design.setShirt(Util.RANDOM.nextInt(SHIRT.size()));
        design.setShirtColor(Colors.random(Util.RANDOM));

        design.setSleeves(Util.RANDOM.nextInt(SLEEVES.size()));
        design.setSleevesColor(Colors.random(Util.RANDOM));

        design.setPants(Util.RANDOM.nextInt(PANTS.size()));
        design.setPantsColor(Colors.random(Util.RANDOM));

        design.setFootwear(Util.RANDOM.nextInt(FOOTWEAR.size()));
        design.setFavoriteColor(Colors.random(Util.RANDOM));

        design.setAccessory(Util.RANDOM.nextInt(ACCESSORIES.size()));

        design.setEyebrows(Util.RANDOM.nextInt(EYEBROWS.size()));
        design.setEyebrowsColor(Colors.random(Util.RANDOM));

        design.setMouth(Util.RANDOM.nextInt(MOUTH.size()));

        design.setBeard(Util.RANDOM.nextInt(BEARD.size()));
        design.setBeardColor(Colors.random(Util.RANDOM));
    }

    private static int loadIndex(String s, List<IResourceName> list){
        IResourceName res;

        if(s != null && !s.isEmpty()){
            res = RockBottomAPI.createRes(s);
        }
        else{
            res = null;
        }

        int index = list.indexOf(res);
        return Math.max(0, index);
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
        IDataManager dataManager = RockBottomAPI.getGame().getDataManager();

        DataSet set = new DataSet();
        this.save(set);
        set.write(dataManager.getPlayerDesignFile());
    }

    @Override
    public void loadFromFile(){
        IDataManager dataManager = RockBottomAPI.getGame().getDataManager();

        DataSet set = new DataSet();
        set.read(dataManager.getPlayerDesignFile());
        this.load(set);
    }

    @Override
    public void save(DataSet set){
        set.addString("name", this.name);
        set.addInt("color", this.color);
        set.addBoolean("female", this.female);

        set.addString("base", saveIndex(this.base, BASE));
        set.addInt("eye_color", this.eyeColor);

        set.addString("shirt", saveIndex(this.shirt, SHIRT));
        set.addInt("shirt_color", this.shirtColor);

        set.addString("sleeves", saveIndex(this.sleeves, SLEEVES));
        set.addInt("sleeves_color", this.sleevesColor);

        set.addString("pants", saveIndex(this.pants, PANTS));
        set.addInt("pants_color", this.pantsColor);

        set.addString("footwear", saveIndex(this.footwear, FOOTWEAR));
        set.addInt("footwear_color", this.footwearColor);

        set.addString("hair", saveIndex(this.hair, HAIR));
        set.addInt("hair_color", this.hairColor);

        set.addString("accessory", saveIndex(this.accessory, ACCESSORIES));

        set.addString("eyebrows", saveIndex(this.eyebrows, EYEBROWS));
        set.addInt("eyebrows_color", this.eyebrowsColor);

        set.addString("mouth", saveIndex(this.mouth, MOUTH));

        set.addString("beard", saveIndex(this.beard, BEARD));
        set.addInt("beard_color", this.beardColor);
    }

    @Override
    public void load(DataSet set){
        this.name = set.getString("name");
        this.color = set.getInt("color");
        this.female = set.getBoolean("female");

        this.base = loadIndex(set.getString("base"), BASE);
        this.eyeColor = set.getInt("eye_color");

        this.shirt = loadIndex(set.getString("shirt"), SHIRT);
        this.shirtColor = set.getInt("shirt_color");

        this.sleeves = loadIndex(set.getString("sleeves"), SLEEVES);
        this.sleevesColor = set.getInt("sleeves_color");

        this.pants = loadIndex(set.getString("pants"), PANTS);
        this.pantsColor = set.getInt("pants_color");

        this.footwear = loadIndex(set.getString("footwear"), FOOTWEAR);
        this.footwearColor = set.getInt("footwear_color");

        this.hair = loadIndex(set.getString("hair"), HAIR);
        this.hairColor = set.getInt("hair_color");

        this.accessory = loadIndex(set.getString("accessory"), ACCESSORIES);

        this.eyebrows = loadIndex(set.getString("eyebrows"), EYEBROWS);
        this.eyebrowsColor = set.getInt("eyebrows_color");

        this.mouth = loadIndex(set.getString("mouth"), MOUTH);

        this.beard = loadIndex(set.getString("beard"), BEARD);
        this.beardColor = set.getInt("beard_color");
    }

    @Override
    public int getFavoriteColor(){
        return this.color;
    }

    @Override
    public void setFavoriteColor(int color){
        this.color = color;
    }

    @Override
    public String getName(){
        return this.name;
    }

    @Override
    public void setName(String name){
        this.name = name;

        if("Jake".equals(this.name)){
            this.name += " <3";
        }
    }

    @Override
    public int getBase(){
        return this.base;
    }

    @Override
    public int getEyeColor(){
        return this.eyeColor;
    }

    @Override
    public int getShirt(){
        return this.shirt;
    }

    @Override
    public int getShirtColor(){
        return this.shirtColor;
    }

    @Override
    public int getSleeves(){
        return this.sleeves;
    }

    @Override
    public int getSleevesColor(){
        return this.sleevesColor;
    }

    @Override
    public int getPants(){
        return this.pants;
    }

    @Override
    public int getPantsColor(){
        return this.pantsColor;
    }

    @Override
    public int getFootwear(){
        return this.footwear;
    }

    @Override
    public int getFootwearColor(){
        return this.footwearColor;
    }

    @Override
    public int getHair(){
        return this.hair;
    }

    @Override
    public int getHairColor(){
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
    public int getBeardColor(){
        return this.beardColor;
    }

    @Override
    public int getEyebrowsColor(){
        return this.eyebrowsColor;
    }

    @Override
    public boolean isFemale(){
        return this.female;
    }

    @Override
    public void setBase(int base){
        this.base = base;
    }

    @Override
    public void setEyeColor(int eyeColor){
        this.eyeColor = eyeColor;
    }

    @Override
    public void setShirt(int shirt){
        this.shirt = shirt;
    }

    @Override
    public void setShirtColor(int shirtColor){
        this.shirtColor = shirtColor;
    }

    @Override
    public void setSleeves(int sleeves){
        this.sleeves = sleeves;
    }

    @Override
    public void setSleevesColor(int sleevesColor){
        this.sleevesColor = sleevesColor;
    }

    @Override
    public void setPants(int pants){
        this.pants = pants;
    }

    @Override
    public void setPantsColor(int pantsColor){
        this.pantsColor = pantsColor;
    }

    @Override
    public void setFootwear(int footwear){
        this.footwear = footwear;
    }

    @Override
    public void setFootwearColor(int footwearColor){
        this.footwearColor = footwearColor;
    }

    @Override
    public void setHair(int hair){
        this.hair = hair;
    }

    @Override
    public void setHairColor(int hairColor){
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
    public void setEyebrowsColor(int eyebrowsColor){
        this.eyebrowsColor = eyebrowsColor;
    }

    @Override
    public void setBeard(int beard){
        this.beard = beard;
    }

    @Override
    public void setBeardColor(int beardColor){
        this.beardColor = beardColor;
    }

    @Override
    public void setFemale(boolean female){
        this.female = female;
    }
}
