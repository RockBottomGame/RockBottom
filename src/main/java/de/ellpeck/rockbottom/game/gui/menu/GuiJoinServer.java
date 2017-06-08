package de.ellpeck.rockbottom.game.gui.menu;

import de.ellpeck.rockbottom.game.RockBottom;
import de.ellpeck.rockbottom.game.gui.Gui;
import de.ellpeck.rockbottom.game.gui.component.ComponentButton;
import de.ellpeck.rockbottom.game.gui.component.ComponentInputField;
import de.ellpeck.rockbottom.game.net.NetHandler;
import de.ellpeck.rockbottom.game.net.packet.toserver.PacketJoin;
import org.newdawn.slick.util.Log;

public class GuiJoinServer extends Gui{

    private ComponentInputField inputField;

    public GuiJoinServer(Gui parent){
        super(100, 100, parent);
    }

    @Override
    public void initGui(RockBottom game){
        super.initGui(game);

        this.inputField = new ComponentInputField(this, this.guiLeft+this.sizeX/2-80, this.guiTop, 160, 16, true, true, false, 128, false);
        this.components.add(this.inputField);

        this.components.add(new ComponentButton(this, 0, this.guiLeft, this.guiTop+20, this.sizeX, 16, game.assetManager.localize("button.connect")));
        this.components.add(new ComponentButton(this, -1, this.guiLeft+this.sizeX/2-40, (int)game.getHeightInGui()-30, 80, 16, game.assetManager.localize("button.back")));
    }

    @Override
    public boolean onButtonActivated(RockBottom game, int button){
        if(button == -1){
            game.guiManager.openGui(this.parent);
            return true;
        }
        else if(button == 0){
            try{
                String[] separated = this.inputField.getText().split(":");
                if(separated.length == 1){
                    NetHandler.init(separated[0], 8000, false);
                }
                else{
                    int port = Integer.parseInt(separated[1]);
                    NetHandler.init(separated[0], port, false);
                }

                Log.info("Attempting to join server");
                NetHandler.sendToServer(new PacketJoin(game.uniqueId, RockBottom.VERSION));
            }
            catch(Exception e){
                Log.error("Couldn't connect to server", e);
            }
        }
        return false;
    }
}
