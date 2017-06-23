package de.ellpeck.rockbottom.gui;

import de.ellpeck.rockbottom.api.gui.Gui;
import de.ellpeck.rockbottom.api.util.Util;

public class GuiPlayerEditor extends Gui{

    private static final String[] DEFAULT_NAMES = new String[]{"Bob", "Doley", "Jason", "Huffelpuff", "Megan", "Jennifer", "Bottle", "Bus Stop", "ThePlayer99", "Genelele", "Karina", "Heinz", "Ketchup", "Dan", "David", "Penguin", "Hubert", "Penny", "Vinny", "Xx_TheBestLP_xX", "Bozo", "Patrick", "InigoMontoya", "Pebbles", "Noodles", "Milkshake"};

    public GuiPlayerEditor(int sizeX, int sizeY, Gui parent){
        super(sizeX, sizeY, parent);
    }

    public static String getRandomChatName(){
        return DEFAULT_NAMES[Util.RANDOM.nextInt(DEFAULT_NAMES.length)];
    }
}
