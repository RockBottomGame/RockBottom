package de.ellpeck.rockbottom.render;

import com.google.gson.annotations.SerializedName;
import de.ellpeck.rockbottom.api.render.IPlayerDesign;
import de.ellpeck.rockbottom.api.util.Colors;
import de.ellpeck.rockbottom.api.util.Util;

public class PlayerDesign implements IPlayerDesign{

    private static final String[] DEFAULT_NAMES = new String[]{"Jake", "Craig", "Mariana", "Louise", "Rosie", "Flo", "Luke", "Abbie", "James", "Chris", "Kieran", "Fatima", "Adam", "Giles", "Megan", "Tim", "Calypso", "Hayley", "Aimee", "Megan", "Eleanor"};

    @SerializedName("name")
    private String name;
    @SerializedName("color")
    private int color;
    @SerializedName("female")
    private boolean female;

    @SerializedName("base")
    private int base;
    @SerializedName("eye_color")
    private int eyeColor;

    @SerializedName("shirt")
    private int shirt;
    @SerializedName("shirt_color")
    private int shirtColor;

    @SerializedName("sleeves")
    private int sleeves;
    @SerializedName("sleeves_color")
    private int sleevesColor;

    @SerializedName("pants")
    private int pants;
    @SerializedName("pants_color")
    private int pantsColor;

    @SerializedName("footwear")
    private int footwear;
    @SerializedName("footwear_color")
    private int footwearColor;

    @SerializedName("hair")
    private int hair;
    @SerializedName("hair_color")
    private int hairColor;

    @SerializedName("accessory")
    private int accessory;

    @SerializedName("mouth")
    private int mouth;

    @SerializedName("eyebrows")
    private int eyebrows;
    @SerializedName("eyebrows_color")
    private int eyebrowsColor;

    @SerializedName("beard")
    private int beard;
    @SerializedName("beard_color")
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
