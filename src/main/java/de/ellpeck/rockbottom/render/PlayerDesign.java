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

import de.ellpeck.rockbottom.RockBottom;
import de.ellpeck.rockbottom.api.data.IDataManager;
import de.ellpeck.rockbottom.api.data.set.DataSet;
import de.ellpeck.rockbottom.api.render.IPlayerDesign;
import de.ellpeck.rockbottom.api.util.Util;
import org.newdawn.slick.Color;

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
    }

    private static Color loadColor(DataSet set, String key, Color def){
        if(set.hasKey(key)){
            return new Color(set.getInt(key));
        }
        else{
            return def;
        }
    }

    @Override
    public void saveToFile(){
        IDataManager dataManager = RockBottom.get().getDataManager();

        DataSet set = new DataSet();
        this.save(set);
        set.write(dataManager.getPlayerDesignFile());
    }

    @Override
    public void loadFromFile(){
        IDataManager dataManager = RockBottom.get().getDataManager();

        DataSet set = new DataSet();
        set.read(dataManager.getPlayerDesignFile());
        this.load(set);
    }

    @Override
    public void save(DataSet set){
        set.addString("name", this.name);
        set.addInt("color", Util.toIntColor(this.color));

        set.addInt("base", this.base);
        set.addInt("eye_color", Util.toIntColor(this.eyeColor));

        set.addInt("shirt", this.shirt);
        set.addInt("shirt_color", Util.toIntColor(this.shirtColor));

        set.addInt("sleeves", this.sleeves);
        set.addInt("sleeves_color", Util.toIntColor(this.sleevesColor));

        set.addInt("pants", this.pants);
        set.addInt("pants_color", Util.toIntColor(this.pantsColor));

        set.addInt("footwear", this.footwear);
        set.addInt("footwear_color", Util.toIntColor(this.footwearColor));

        set.addInt("hair", this.hair);
        set.addInt("hair_color", Util.toIntColor(this.hairColor));

        set.addInt("accessory", this.accessory);
    }

    @Override
    public void load(DataSet set){
        this.name = set.getString("name");
        this.color = new Color(set.getInt("color"));

        this.base = set.getInt("base");
        this.eyeColor = loadColor(set, "eye_color", Color.black);

        this.shirt = set.getInt("shirt");
        this.shirtColor = loadColor(set, "shirt_color", Color.white);

        this.sleeves = set.getInt("sleeves");
        this.sleevesColor = loadColor(set, "sleeves_color", Color.white);

        this.pants = set.getInt("pants");
        this.pantsColor = loadColor(set, "pants_color", Color.blue);

        this.footwear = set.getInt("footwear");
        this.footwearColor = loadColor(set, "footwear_color", Color.darkGray);

        this.hair = set.getInt("hair");
        this.hairColor = loadColor(set, "hair_color", new Color(0x331809));

        this.accessory = set.getInt("accessory");
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
}
