package de.ellpeck.rockbottom.game.item;

import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.item.Item;
import de.ellpeck.rockbottom.game.RockBottom;
import de.ellpeck.rockbottom.game.data.set.DataSet;
import de.ellpeck.rockbottom.api.tile.Tile;
import org.newdawn.slick.util.Log;

public class ItemInstance{

    private final Item item;
    private final short meta;

    private int amount;

    public ItemInstance(Tile tile){
        this(tile, 1);
    }

    public ItemInstance(Tile tile, int amount){
        this(tile, amount, 0);
    }

    public ItemInstance(Tile tile, int amount, int meta){
        this(tile.getItem(), amount, meta);
    }

    public ItemInstance(Item item){
        this(item, 1);
    }

    public ItemInstance(Item item, int amount){
        this(item, amount, 0);
    }

    public ItemInstance(Item item, int amount, int meta){
        if(item == null){
            throw new NullPointerException("Tried to create an ItemInstance with null item!");
        }
        if(meta < 0 || meta > Short.MAX_VALUE){
            throw new IndexOutOfBoundsException("Tried assigning meta "+meta+" to item instance with item "+item+" and amount "+amount+" which is less than 0 or greater than max "+Short.MAX_VALUE+"!");
        }

        this.item = item;
        this.amount = amount;
        this.meta = (short)meta;
    }

    public static ItemInstance load(DataSet set){
        String name = set.getString("item_name");
        Item item = RockBottomAPI.ITEM_REGISTRY.get(name);

        if(item != null){
            int amount = set.getInt("amount");
            short meta = set.getShort("meta");

            return new ItemInstance(item, amount, meta);
        }
        else{
            Log.info("Could not load item instance from data set "+set+" because name "+name+" is missing!");

            return null;
        }
    }

    public void save(DataSet set){
        set.addString("item_name", RockBottomAPI.ITEM_REGISTRY.getId(this.item));
        set.addInt("amount", this.amount);
        set.addShort("meta", this.meta);
    }

    public Item getItem(){
        return this.item;
    }

    public int getMeta(){
        return this.meta;
    }

    public int getAmount(){
        return this.amount;
    }

    public boolean fitsAmount(int amount){
        return this.getAmount()+amount <= this.getMaxAmount();
    }

    public int getMaxAmount(){
        return this.item.getMaxAmount();
    }

    public ItemInstance setAmount(int amount){
        this.amount = amount;
        return this;
    }

    public ItemInstance addAmount(int amount){
        return this.setAmount(this.amount+amount);
    }

    public ItemInstance removeAmount(int amount){
        return this.setAmount(this.amount-amount);
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

    public String getDisplayName(){
        return RockBottom.get().assetManager.localize(this.item.getUnlocalizedName(this));
    }

    @Override
    public int hashCode(){
        int result = this.item.hashCode();
        result = 31*result+this.meta;
        result = 31*result+this.amount;
        return result;
    }
}
