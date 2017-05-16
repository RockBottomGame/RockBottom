package de.ellpeck.rockbottom.assets.font;

import de.ellpeck.rockbottom.util.Util;
import org.lwjgl.Sys;
import org.newdawn.slick.Color;

public class FormattingCode{

    public static final FormattingCode NONE = new FormattingCode(' ', Color.white, 0, "");
    public static final FormattingCode BLACK = new FormattingCode('0', Color.black, 2);
    public static final FormattingCode DARK_GRAY = new FormattingCode('1', Color.darkGray, 2);
    public static final FormattingCode GRAY = new FormattingCode('2', Color.gray, 2);
    public static final FormattingCode LIGHT_GRAY = new FormattingCode('3', Color.lightGray, 2);
    public static final FormattingCode WHITE = new FormattingCode('4', Color.white, 2);
    public static final FormattingCode YELLOW = new FormattingCode('5', Color.yellow, 2);
    public static final FormattingCode ORANGE = new FormattingCode('6', Color.orange, 2);
    public static final FormattingCode RED = new FormattingCode('7', Color.red, 2);
    public static final FormattingCode PINK = new FormattingCode('8', Color.pink, 2);
    public static final FormattingCode MAGENTA = new FormattingCode('9', Color.magenta, 2);
    public static final FormattingCode GREEN = new FormattingCode('a', new Color(0F, 0.5F, 0F), 2);

    public static final FormattingCode[] DEFAULT_CODES = new FormattingCode[]{BLACK, DARK_GRAY, GRAY, LIGHT_GRAY, WHITE, YELLOW, ORANGE, RED, PINK, MAGENTA, GREEN};

    private final char format;
    private final Color color;
    private final int length;
    private final String strg;

    public FormattingCode(char format, Color color, int length){
        this(format, color, length, "&"+format);
    }

    public FormattingCode(char format, Color color, int length, String strg){
        this.format = format;
        this.color = color;
        this.length = length;
        this.strg = strg;
    }

    public static FormattingCode getFormat(String s, int index){
        if(s.length() > index+1 && s.charAt(index) == '&'){
            char formatChar = s.charAt(index+1);

            if(formatChar == '('){
                int closingIndex = s.indexOf(")", index+2);
                if(closingIndex > index+2){
                    String code = s.substring(index+2, closingIndex);
                    String[] colors = code.split(",");

                    if(colors.length == 3){
                        try{
                            return new FormattingCode(' ', new Color(Float.parseFloat(colors[0]), Float.parseFloat(colors[1]), Float.parseFloat(colors[2])), code.length()+3, "&("+code+")");
                        }
                        catch(Exception ignored){
                        }
                    }
                }
            }
            else if(formatChar == 'r'){
                return new FormattingCode('r', Util.getWheelColor((System.currentTimeMillis()/10)%256), 2);
            }
            else{
                for(FormattingCode code : DEFAULT_CODES){
                    if(formatChar == code.format){
                        return code;
                    }
                }
            }
        }
        return NONE;
    }

    public Color getColor(){
        return this.color;
    }

    public int getLength(){
        return this.length;
    }

    @Override
    public String toString(){
        return this.strg;
    }
}
