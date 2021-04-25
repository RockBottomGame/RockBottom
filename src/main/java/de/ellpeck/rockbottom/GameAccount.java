package de.ellpeck.rockbottom;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import de.ellpeck.rockbottom.api.IGameAccount;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.render.IPlayerDesign;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.render.design.DefaultPlayerDesign;
import de.ellpeck.rockbottom.render.design.PlayerDesign;

import java.util.UUID;

public class GameAccount implements IGameAccount {

    private String username;
    private UUID uuid;
    private boolean verified;
    private IPlayerDesign design;

    public static GameAccount from(JsonObject body) {
        GameAccount account = new GameAccount();
        account.username = body.get("username").getAsString();
        account.uuid = UUID.fromString(body.get("account_id").getAsString());
        account.verified = body.get("verified").getAsBoolean();
        IPlayerDesign design;
        try {
            JsonObject designJson = body.getAsJsonObject("player_design");
            if (designJson.keySet().isEmpty()) {
                design = new DefaultPlayerDesign();
            } else {
                design = Util.GSON.fromJson(designJson, PlayerDesign.class);
            }
        } catch (JsonSyntaxException e) {
            RockBottomAPI.logger().info("Received invalid json player design. Using default.");
            design = new DefaultPlayerDesign();
        }
        account.design = design;
        return account;
    }

    @Override
    public String getDisplayName() {
        return this.username;
    }

    @Override
    public IPlayerDesign getPlayerDesign() {
        return this.design;
    }

    @Override
    public boolean isVerified() {
        return this.verified;
    }

    @Override
    public void verify(boolean verify) {
        this.verified = verify;
    }

    @Override
    public UUID getUUID() {
        return this.uuid;
    }

    @Override
    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public void setDisplayName(String username) {
        this.username = username;
    }

    @Override
    public void setPlayerDesign(IPlayerDesign design) {
        this.design = design;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }
}
