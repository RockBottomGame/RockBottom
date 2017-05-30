package de.ellpeck.rockbottom.gui.component;

import de.ellpeck.rockbottom.RockBottom;
import de.ellpeck.rockbottom.assets.AssetManager;
import de.ellpeck.rockbottom.assets.font.Font;
import de.ellpeck.rockbottom.gui.Gui;
import de.ellpeck.rockbottom.util.Util;
import org.newdawn.slick.Graphics;

import java.util.Arrays;
import java.util.List;

public class ComponentMessageBox extends ComponentButton{

    private final float textScale;
    private final List<String> text;

    private final int[] letterAmountPerPage;
    private final int possibleLineAmount;
    private final int pageAmount;

    private int page;
    private float typedLetter;

    public ComponentMessageBox(Gui gui, int id, int x, int y, int sizeX, int sizeY, float textScale, String... dialogLocKeys){
        super(gui, id, x, y, sizeX, sizeY, null);
        this.textScale = textScale;

        Font font = RockBottom.get().assetManager.getFont();
        this.text = font.splitTextToLength(this.sizeX-10, textScale, true, dialogLocKeys);
        this.possibleLineAmount = Util.ceil((this.sizeY-10)/font.getHeight(textScale));

        this.pageAmount = Util.ceil((float)this.text.size()/(float)this.possibleLineAmount);
        this.letterAmountPerPage = new int[this.pageAmount];

        for(int page = 0; page < this.pageAmount; page++){
            int letterAmount = 0;
            for(int i = 0; i < this.possibleLineAmount; i++){
                int index = i+(this.possibleLineAmount*page);

                if(this.text.size() > index){
                    letterAmount += font.removeFormatting(this.text.get(index)).length();
                }
                else{
                    break;
                }
            }
            this.letterAmountPerPage[page] = letterAmount;
        }
    }

    @Override
    public void render(RockBottom game, AssetManager manager, Graphics g){
        super.render(game, manager, g);

        Font font = manager.getFont();
        float height = font.getHeight(this.textScale);

        int accumulatedLength = 0;
        for(int line = 0; line < this.possibleLineAmount; line++){
            int index = line+(this.possibleLineAmount*this.page);

            if(this.text.size() > index){
                String s = this.text.get(index);
                int length = font.removeFormatting(s).length();

                if(this.typedLetter-accumulatedLength < length){
                    font.drawCutOffString(this.x+5, this.y+5+(line*height), s, this.textScale, (int)this.typedLetter-accumulatedLength, false, true);
                    break;
                }
                else{
                    font.drawString(this.x+5, this.y+5+(line*height), s, this.textScale);
                    accumulatedLength += length;
                }
            }
            else{
                break;
            }

        }
    }

    @Override
    public void update(RockBottom game){
        super.update(game);

        if(this.typedLetter < this.letterAmountPerPage[this.page]){
            this.typedLetter += game.settings.textSpeed;
        }
    }

    @Override
    public boolean onPressed(RockBottom game){
        int lastTyped = this.letterAmountPerPage[this.page];

        if(this.typedLetter < lastTyped){
            this.typedLetter = lastTyped;
            return true;
        }
        else if(this.page < this.pageAmount-1){
            this.page++;
            this.typedLetter = 0;
            return true;
        }
        else{
            return false;
        }
    }
}
