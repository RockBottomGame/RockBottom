package de.ellpeck.rockbottom.assets;

import com.google.common.base.Charsets;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.ellpeck.rockbottom.api.IGraphics;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.ITexture;
import de.ellpeck.rockbottom.api.assets.font.FontProp;
import de.ellpeck.rockbottom.api.assets.font.FormattingCode;
import de.ellpeck.rockbottom.api.assets.font.IFont;
import de.ellpeck.rockbottom.api.util.Colors;
import de.ellpeck.rockbottom.api.util.Pos2;
import de.ellpeck.rockbottom.api.util.Util;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class Font implements IFont{

    private final Random fontRandom = new Random();

    private final String name;
    private final ITexture texture;

    private final Map<Character, Pos2> characters;

    private final int charWidth;
    private final int charHeight;

    public Font(String name, ITexture texture, int widthInChars, int heightInChars, Map<Character, Pos2> characters){
        this.name = name;
        this.texture = texture;
        this.characters = characters;

        this.charWidth = (int)(texture.getWidth()/widthInChars);
        this.charHeight = (int)(texture.getHeight()/heightInChars);
    }

    public static Font fromStream(ITexture texture, InputStream infoStream, String name) throws Exception{
        JsonParser parser = new JsonParser();
        JsonObject main = parser.parse(new InputStreamReader(infoStream, Charsets.UTF_8)).getAsJsonObject();

        int width = 0;
        Map<Character, Pos2> characters = new HashMap<>();

        JsonArray rows = main.getAsJsonArray("data");
        for(int y = 0; y < rows.size(); y++){
            JsonArray row = rows.get(y).getAsJsonArray();

            int length = row.size();
            if(length > width){
                width = length;
            }

            for(int x = 0; x < length; x++){
                String s = row.get(x).getAsString();
                if(s != null && !s.isEmpty()){
                    char c = s.charAt(0);
                    if(c != ' '){
                        characters.put(c, new Pos2(x, y));
                    }
                }
            }
        }

        int height = rows.size();
        RockBottomAPI.logger().config("Loaded font "+name+" with dimensions "+width+"x"+height+" and the following character map consisting of "+characters.size()+" characters: "+characters);

        return new Font(name, texture, width, height, characters);
    }

    @Override
    public void drawStringFromRight(float x, float y, String s, float scale){
        this.drawString(x-this.getWidth(s, scale), y, s, scale);
    }

    @Override
    public void drawCenteredString(float x, float y, String s, float scale, boolean centeredOnY){
        this.drawString(x-this.getWidth(s, scale)/2F, centeredOnY ? (y-this.getHeight(scale)/2F) : y, s, scale);
    }

    @Override
    public void drawFadingString(float x, float y, String s, float scale, float fadeTotal, float fadeInEnd, float fadeOutStart){
        int color = Colors.WHITE;

        if(fadeTotal <= fadeInEnd){
            color = Colors.multiplyA(color, fadeTotal/fadeInEnd);
        }
        else if(fadeTotal >= fadeOutStart){
            color = Colors.multiplyA(color, 1F-(fadeTotal-fadeOutStart)/(1F-fadeOutStart));
        }

        this.drawString(x, y, s, scale, color);
    }

    @Override
    public void drawString(float x, float y, String s, float scale){
        this.drawString(x, y, s, scale, Colors.WHITE);
    }

    @Override
    public void drawString(float x, float y, String s, float scale, int color){
        this.drawString(x, y, s, 0, s.length(), scale, color);
    }

    @Override
    public void drawCutOffString(float x, float y, String s, float scale, int length, boolean fromRight, boolean basedOnCharAmount){
        int strgLength = s.length();

        if((basedOnCharAmount ? strgLength : this.getWidth(s, scale)) <= length){
            this.drawString(x, y, s, scale);
        }

        int amount = 0;
        String accumulated = "";

        for(int i = 0; i < strgLength; i++){
            if(fromRight){
                accumulated = s.charAt(strgLength-1-i)+accumulated;
            }
            else{
                accumulated += s.charAt(i);
            }

            amount++;

            if((basedOnCharAmount ? this.removeFormatting(accumulated).length() : this.getWidth(accumulated, scale)) >= length){
                break;
            }
        }

        if(fromRight){
            this.drawString(x, y, s, strgLength-amount, strgLength, scale, Colors.WHITE);
        }
        else{
            this.drawString(x, y, s, 0, amount, scale, Colors.WHITE);
        }
    }

    @Override
    public void drawSplitString(float x, float y, String s, float scale, int length){
        List<String> split = this.splitTextToLength(length, scale, true, s);

        for(String string : split){
            this.drawString(x, y, string, scale);
            y += this.getHeight(scale);
        }
    }

    @Override
    public void drawString(float x, float y, String s, int drawStart, int drawEnd, float scale, int color, int shadowColor){
        int startColor = color;
        float initialAlpha = Colors.getA(color);
        float xOffset = 0F;
        FontProp prop = FontProp.NONE;

        char[] characters = s.toCharArray();
        for(int i = 0; i < Math.min(drawEnd, characters.length); i++){
            FormattingCode code = FormattingCode.getFormat(s, i);
            if(code != FormattingCode.NONE){
                int formatColor = code.getColor();
                if(formatColor != Colors.NO_COLOR){
                    if(formatColor == Colors.RESET_COLOR){
                        color = startColor;
                    }
                    else{
                        float formatAlpha = Colors.getA(formatColor);
                        if(initialAlpha != formatAlpha){
                            color = Colors.setA(color, formatAlpha);
                        }
                        else{
                            color = formatColor;
                        }
                    }
                }

                FontProp formatProp = code.getProp();
                if(formatProp != FontProp.NONE){
                    if(formatProp == FontProp.RESET){
                        prop = FontProp.NONE;
                    }
                    else{
                        prop = formatProp;
                    }
                }

                i += code.getLength()-1;
                continue;
            }

            if(i >= drawStart){
                this.drawCharacter(x+xOffset, y, characters[i], scale, color, prop, shadowColor);
                xOffset += (float)this.charWidth*scale;
            }
        }

    }

    @Override
    public void drawString(float x, float y, String s, int drawStart, int drawEnd, float scale, int color){
        this.drawString(x, y, s, drawStart, drawEnd, scale, color, Colors.BLACK);
    }

    @Override
    public void drawCharacter(float x, float y, char character, float scale, int color, FontProp prop, int shadowColor){
        IGraphics g = RockBottomAPI.getGame().getGraphics();

        float scaledWidth = (float)this.charWidth*scale;
        float scaledHeight = (float)this.charHeight*scale;

        boolean shadow = shadowColor != Colors.NO_COLOR;
        if(shadow){
            shadowColor = Colors.setA(Colors.BLACK, Colors.getA(color));
        }
        float shadowOffset = 2F*scale;

        if(character != ' '){
            if(prop == FontProp.RANDOM && character != '|'){
                String randomChars = "abcdefghijklmnopqrstuvxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";

                this.fontRandom.setSeed(Util.scrambleSeed((int)x, (int)y));
                double noise = (this.fontRandom.nextDouble()*(Util.getTimeMillis()/75))%1D;
                character = randomChars.charAt((int)(noise*(double)randomChars.length()));
            }

            Pos2 pos = this.characters.get(character);

            if(pos == null){
                pos = new Pos2(-1, -1);
                this.characters.put(character, pos);

                RockBottomAPI.logger().warning("Character "+character+" is missing from font with name "+this.name+"!");
            }

            if(pos.getX() >= 0 && pos.getY() >= 0){
                int srcX = pos.getX()*this.charWidth;
                int srcY = pos.getY()*this.charHeight;

                float x2 = x+scaledWidth;
                float y2 = y+scaledHeight;

                if(prop == FontProp.BOLD){
                    float boldness = 2F*scale;

                    x -= boldness;
                    y -= boldness;
                    x2 += boldness;
                    y2 += boldness;
                }

                boolean italics = prop == FontProp.ITALICS;
                boolean upsideDown = prop == FontProp.UPSIDE_DOWN;

                if(italics || upsideDown){
                    this.texture.setRotationCenter(0F, 0F);
                    this.texture.setRotation(italics ? 5F : 180F);

                    if(upsideDown){
                        y += scaledHeight;
                        y2 += scaledHeight;

                        x += scaledWidth;
                        x2 += scaledWidth;
                    }
                }

                if(shadow){
                    this.texture.draw(x+shadowOffset, y+shadowOffset, x2+shadowOffset, y2+shadowOffset, srcX, srcY, srcX+this.charWidth, srcY+this.charHeight, shadowColor);
                }
                this.texture.draw(x, y, x2, y2, srcX, srcY, srcX+this.charWidth, srcY+this.charHeight, color);

                if(italics || upsideDown){
                    this.texture.setRotation(0F);
                }
            }
            else{
                RockBottomAPI.getGame().getAssetManager().getMissingTexture().draw(x, y, this.charWidth*scale, this.charHeight*scale);
            }
        }

        boolean underlined = prop == FontProp.UNDERLINED;

        if(underlined || prop == FontProp.STRIKETHROUGH){
            float lineY = y+(underlined ? scaledHeight-4F*scale : scaledHeight/2F-3F*scale);

            if(shadow){
                g.fillRect(x+shadowOffset, lineY+shadowOffset, scaledWidth, 2F*scale, shadowColor);
            }
            g.fillRect(x, lineY, scaledWidth, 2F*scale, color);
        }

    }

    @Override
    public void drawCharacter(float x, float y, char character, float scale, int color, FontProp prop){
        this.drawCharacter(x, y, character, scale, color, prop, Colors.BLACK);
    }

    @Override
    public String removeFormatting(String s){
        String newString = "";
        for(int i = 0; i < s.length(); i++){
            FormattingCode code = FormattingCode.getFormat(s, i);
            if(code != FormattingCode.NONE){
                i += code.getLength()-1;
            }
            else{
                newString += s.charAt(i);
            }
        }
        return newString;
    }

    @Override
    public float getWidth(String s, float scale){
        return (float)this.charWidth*(float)this.removeFormatting(s).length()*scale;
    }

    @Override
    public float getHeight(float scale){
        return (float)this.charHeight*scale;
    }

    @Override
    public List<String> splitTextToLength(int length, float scale, boolean wrapFormatting, String... lines){
        return this.splitTextToLength(length, scale, wrapFormatting, Arrays.asList(lines));
    }

    @Override
    public List<String> splitTextToLength(int length, float scale, boolean wrapFormatting, List<String> lines){
        List<String> result = new ArrayList<>();
        String accumulated = "";

        for(String line : lines){
            FormattingCode trailingColor = FormattingCode.NONE;
            FormattingCode trailingProp = FormattingCode.NONE;

            for(String subLine : line.split("\n")){
                String[] words = subLine.split(" ");

                for(String word : words){
                    if(wrapFormatting){
                        for(int i = 0; i < word.length()-1; i++){
                            FormattingCode format = FormattingCode.getFormat(word, i);
                            if(format != FormattingCode.NONE){
                                if(format.getColor() != Colors.NO_COLOR){
                                    trailingColor = format;
                                }

                                if(format.getProp() != FontProp.NONE){
                                    trailingProp = format;
                                }
                            }
                        }
                    }

                    if(this.getWidth(accumulated+word, scale) >= length){
                        result.add(accumulated.trim());
                        accumulated = trailingColor.toString()+trailingProp+word+" ";
                    }
                    else{
                        accumulated += word+" ";
                    }
                }

                result.add(accumulated.trim());
                accumulated = trailingColor.toString()+trailingProp;
            }

            accumulated = "";
        }

        return result;
    }

}
