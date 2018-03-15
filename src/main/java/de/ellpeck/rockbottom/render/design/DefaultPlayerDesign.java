package de.ellpeck.rockbottom.render.design;

import de.ellpeck.rockbottom.api.render.IPlayerDesign;
import de.ellpeck.rockbottom.api.util.Colors;

public class DefaultPlayerDesign implements IPlayerDesign{

    @Override
    public int getFavoriteColor(){
        return Colors.WHITE;
    }

    @Override
    public void setFavoriteColor(int color){
        this.unsupported();
    }

    @Override
    public String getName(){
        return "Defaulty";
    }

    @Override
    public void setName(String name){
        this.unsupported();
    }

    @Override
    public int getBase(){
        return 0;
    }

    @Override
    public int getEyeColor(){
        return -14346752;
    }

    @Override
    public int getShirt(){
        return 0;
    }

    @Override
    public int getShirtColor(){
        return -327685;
    }

    @Override
    public int getSleeves(){
        return 1;
    }

    @Override
    public int getSleevesColor(){
        return -65537;
    }

    @Override
    public int getPants(){
        return 1;
    }

    @Override
    public int getPantsColor(){
        return -16768439;
    }

    @Override
    public int getFootwear(){
        return 0;
    }

    @Override
    public int getFootwearColor(){
        return -14330624;
    }

    @Override
    public int getHair(){
        return 4;
    }

    @Override
    public int getHairColor(){
        return -13559552;
    }

    @Override
    public int getAccessory(){
        return 0;
    }

    @Override
    public int getEyebrows(){
        return 0;
    }

    @Override
    public int getMouth(){
        return 0;
    }

    @Override
    public int getBeard(){
        return 0;
    }

    @Override
    public int getBeardColor(){
        return -16772862;
    }

    @Override
    public int getEyebrowsColor(){
        return -15136256;
    }

    @Override
    public boolean isFemale(){
        return false;
    }

    @Override
    public void setBase(int base){
        this.unsupported();
    }

    @Override
    public void setEyeColor(int eyeColor){
        this.unsupported();
    }

    @Override
    public void setShirt(int shirt){
        this.unsupported();
    }

    @Override
    public void setShirtColor(int shirtColor){
        this.unsupported();
    }

    @Override
    public void setSleeves(int sleeves){
        this.unsupported();
    }

    @Override
    public void setSleevesColor(int sleevesColor){
        this.unsupported();
    }

    @Override
    public void setPants(int pants){
        this.unsupported();
    }

    @Override
    public void setPantsColor(int pantsColor){
        this.unsupported();
    }

    @Override
    public void setFootwear(int footwear){
        this.unsupported();
    }

    @Override
    public void setFootwearColor(int footwearColor){
        this.unsupported();
    }

    @Override
    public void setHair(int hair){
        this.unsupported();
    }

    @Override
    public void setHairColor(int hairColor){
        this.unsupported();
    }

    @Override
    public void setAccessory(int accessory){
        this.unsupported();
    }

    @Override
    public void setEyebrows(int eyebrows){
        this.unsupported();
    }

    @Override
    public void setMouth(int mouth){
        this.unsupported();
    }

    @Override
    public void setEyebrowsColor(int eyebrowsColor){
        this.unsupported();
    }

    @Override
    public void setBeard(int beard){
        this.unsupported();
    }

    @Override
    public void setBeardColor(int beardColor){
        this.unsupported();
    }

    @Override
    public void setFemale(boolean female){
        this.unsupported();
    }

    private void unsupported(){
        throw new UnsupportedOperationException("Cannot change the default player design");
    }
}
