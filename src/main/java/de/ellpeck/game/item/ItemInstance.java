package de.ellpeck.game.item;

import de.ellpeck.game.ContentRegistry;
import de.ellpeck.game.Game;
import de.ellpeck.game.Main;
import de.ellpeck.game.data.set.DataSet;
import de.ellpeck.game.world.tile.Tile;

public class ItemInstance{

    private final Item item;
    private final byte meta;

    private int amount;

    public ItemInstance(Tile tile){
        this(tile, 1);
    }

    public ItemInstance(Tile tile, int amount){
        this(tile, amount, (byte)0);
    }

    public ItemInstance(Tile tile, int amount, byte meta){
        this(tile.getItem(), amount, meta);
    }

    public ItemInstance(Item item){
        this(item, 1);
    }

    public ItemInstance(Item item, int amount){
        this(item, amount, (byte)0);
    }

    public ItemInstance(Item item, int amount, byte meta){
        if(item == null){
            Main.doExceptionInfo(Game.get(), new NullPointerException("Tried to create an ItemInstance with null item!"));
        }

        this.item = item;
        this.amount = amount;
        this.meta = meta;
    }

    public ItemInstance(DataSet set){
        this.item = ContentRegistry.ITEM_REGISTRY.get(set.getInt("item_id"));
        this.amount = set.getInt("amount");
        this.meta = set.getByte("meta");
    }

    public void save(DataSet set){
        set.addInt("item_id", ContentRegistry.ITEM_REGISTRY.getId(this.item));
        set.addInt("amount", this.amount);
        set.addByte("meta", this.meta);
    }

    public Item getItem(){
        return this.item;
    }

    public byte getMeta(){
        return this.meta;
    }

    public int getAmount(){
        return this.amount;
    }

    public ItemInstance setAmount(int amount){
        this.amount = amount;
        return this;
    }

    public ItemInstance add(int amount){
        this.setAmount(this.amount+amount);
        return this;
    }

    public ItemInstance remove(int amount){
        this.setAmount(this.amount-amount);
        return this;
    }

    public ItemInstance copy(){
        return new ItemInstance(this.item, this.amount, this.meta);
    }

    @Override
    public boolean equals(Object o){
        if(this == o){
            return true;
        }
        if(o == null || this.getClass() != o.getClass()){
            return false;
        }

        ItemInstance that = (ItemInstance)o;
        return this.meta == that.meta && this.amount == that.amount && this.item.equals(that.item);
    }

    public boolean isItemEqual(ItemInstance other){
        return other.getItem() == this.getItem();
    }

    @Override
    public int hashCode(){
        int result = this.item.hashCode();
        result = 31*result+this.meta;
        result = 31*result+this.amount;
        return result;
    }
}
