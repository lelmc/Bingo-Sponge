package cn.lelmc.bingosponge;

import cn.lelmc.bingosponge.config.ConfigLoader;
import cn.lelmc.bingosponge.config.main;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.pokemon.PokemonSpec;
import com.pixelmonmod.pixelmon.enums.EnumSpecies;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.DyeColors;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.InventoryArchetypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.property.InventoryDimension;
import org.spongepowered.api.item.inventory.property.InventoryTitle;
import org.spongepowered.api.item.inventory.property.SlotIndex;
import org.spongepowered.api.item.inventory.query.QueryOperationTypes;
import org.spongepowered.api.text.Text;

import java.util.*;

public class menu {
    public UUID uuid;
    public Inventory inventory;
    public List<Integer> row1 = new ArrayList<>();
    public List<Integer> row2 = new ArrayList<>();
    public List<Integer> row3 = new ArrayList<>();
    public TreeMap<Integer, String> bingo = new TreeMap<>();
    public main.setting setting = ConfigLoader.instance.getConfig().setting;

    public menu(UUID uuid) {
        this.uuid = uuid;
        this.inventory = Inventory.builder().of(InventoryArchetypes.MENU_GRID)
                .property(InventoryTitle.of(Text.of(setting.Title.replace("&", "§"))))
                .property(InventoryDimension.PROPERTY_NAME, InventoryDimension.of(5, 9))
                .listener(ClickInventoryEvent.class, e -> e.setCancelled(true))
                .listener(ClickInventoryEvent.Primary.class, this::onClick)
                .build(BingoSponge.instance);
        loadBingo();
    }

    private void loadBingo() {
        List<Integer> slot = new ArrayList<>(Arrays.asList(17, 18, 26, 27));
        if (bingo.isEmpty()) {
            for (int i = 0; i < 45; i++) {
                if (i < 10 || i >= 35 || slot.contains(i)) {
                    this.inventory.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotIndex.of(i))).offer(pane());
                    if (i == 4) {
                        this.inventory.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotIndex.of(i))).set(hint());
                    }
                } else {
                    this.inventory.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotIndex.of(i))).offer(poke(i));
                }
            }
        }
    }

    public ItemStack poke(int slot) {
        Pokemon pokemon = new PokemonSpec(EnumSpecies.randomPoke(false).name).create();
        Optional<ItemType> item = Sponge.getGame().getRegistry().getType(ItemType.class, "pixelmon:pixelmon_sprite");
        if (item.isPresent()) {
            ItemType itemType = item.get();
            DataContainer dataContainer = ItemStack.of(itemType).toContainer()
                    .set(DataQuery.of("UnsafeData", "ndex"), (short) pokemon.getSpecies().getNationalPokedexInteger());
            ItemStack build = ItemStack.builder().fromContainer(dataContainer).build();
            build.offer(Keys.DISPLAY_NAME, Text.of("§a" + pokemon.getLocalizedName()));
            bingo.put(slot, pokemon.getSpecies().name);
            return build;
        }
        return pane();
    }

    public ItemStack pane() {
        ItemStack itemStack = ItemStack.of(ItemTypes.STAINED_GLASS_PANE);
        itemStack.offer(Keys.DISPLAY_NAME, Text.of("§7乐联欢迎您"));//UnsafeDamage
        itemStack.offer(Keys.DYE_COLOR, DyeColors.LIGHT_BLUE);
        return itemStack;
    }

    public ItemStack fulfil() {
        ItemStack itemStack = ItemStack.of(ItemTypes.STAINED_GLASS_PANE);
        itemStack.offer(Keys.DISPLAY_NAME, Text.of("§2已完成"));//UnsafeDamage
        itemStack.offer(Keys.DYE_COLOR, DyeColors.GREEN);
        return itemStack;
    }

    public ItemStack hint() {
        ItemStack itemStack = ItemStack.of(ItemTypes.PAINTING);
        itemStack.offer(Keys.DISPLAY_NAME, Text.of(setting.hintTitle.replace("&", "§")));
        itemStack.offer(Keys.ITEM_LORE, hintLore());
        return itemStack;
    }

    private List<Text> hintLore() {
        List<Text> lore = new ArrayList<>();
        for (String s : setting.hintLore) {
            lore.add(Text.of(s.replace("&", "§")));
        }
        return lore;
    }

    //点击物品
    private void onClick(ClickInventoryEvent.Primary event) {
        if (!event.getSlot().isPresent()) {
            return;
        }
        Player player = (Player) event.getSource();
        ItemStack stack = event.getTransactions().get(0).getOriginal().createStack();
        if (stack.get(Keys.DISPLAY_NAME).isPresent()) {
            Text text = stack.get(Keys.DISPLAY_NAME).get();
            String s = text.toString().replaceAll("[^\u4E00-\u9FA5]", "");
            if (s.contains("已完成") || noPoke(event.getSlot().get())) {
                return;
            }
            String command = "pwiki " + s;
            Sponge.getCommandManager().process(player, command);
        }
    }

    private boolean noPoke(Slot slot) {//点击的不是宝可梦
        List<Integer> slots = new ArrayList<>(Arrays.asList(17, 18, 26, 27));
        Integer value = slot.getProperties(SlotIndex.class).iterator().next().getValue();
        if (value != null) {
            return value == 4 || value < 10 || value >= 35 || slots.contains(value);
        }
        return false;
    }

}
