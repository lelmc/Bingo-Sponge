package cn.lelmc.bingosponge.listener;

import cn.lelmc.bingosponge.BingoSponge;
import cn.lelmc.bingosponge.config.ConfigLoader;
import cn.lelmc.bingosponge.config.main;
import cn.lelmc.bingosponge.menu;
import com.pixelmonmod.pixelmon.api.events.CaptureEvent;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.property.SlotIndex;
import org.spongepowered.api.item.inventory.query.QueryOperationTypes;
import org.spongepowered.api.text.Text;

import java.util.*;

public class event {

    public static List<UUID> row1 = new ArrayList<>();
    public static List<UUID> row2 = new ArrayList<>();
    public static List<UUID> row3 = new ArrayList<>();

    @SubscribeEvent
    public void onStartCapture(CaptureEvent.SuccessfulCapture event) {
        EntityPlayerMP player = event.player;
        if (!BingoSponge.go.containsKey(player.getUniqueID())) {
            return;
        }
        String name = event.getPokemon().getPokemonName();
        menu menu = BingoSponge.go.get(player.getUniqueID());
        if (!menu.bingo.containsValue(name)) {
            return;
        }
        for (int slots : menu.bingo.keySet()) {
            if (menu.bingo.get(slots).equals(name)) {
                menu.bingo.remove(slots);
                menu.inventory.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotIndex.of(slots))).set(menu.fulfil());
                BingoSponge.go.put(player.getUniqueID(), menu);
                handle(menu, slots);//奖励检查
                singleReward(menu);//发放奖励
                break;
            }
        }
    }

    public void handle(menu menu, int slots) {
        if (menu.bingo.isEmpty()){
            allReward(menu);
            return;
        }
        if (slots >= 10 && slots <= 16) {
            menu.row1.add(slots);
        }
        if (slots >= 19 && slots <= 25) {
            menu.row2.add(slots);
        }
        if (slots >= 28 && slots <= 34){
            menu.row3.add(slots);
        }
        if (menu.row1.size() == 7 && !row1.contains(menu.uuid)){
            rowReward(menu);
            row1.add(menu.uuid);
        }
        if (menu.row2.size() == 7 && !row2.contains(menu.uuid)){
            rowReward(menu);
            row2.add(menu.uuid);
        }
        if (menu.row3.size() == 7 && !row3.contains(menu.uuid)){
            rowReward(menu);
            row3.add(menu.uuid);
        }

    }

    public void singleReward(menu menu) {//发单个放奖励
        main.setting setting = ConfigLoader.instance.getConfig().setting;
        Player player = getPlayer(menu.uuid);
        setting.singleReward.forEach(s -> {
            Sponge.getCommandManager().process(Sponge.getGame().getServer()
                    .getConsole(), s.replace("%player%", player.getName()));
            player.sendMessage(Text.of(setting.MsgCapture.replace("&", "§")));
        });
    }

    public void rowReward(menu menu) {//发放一排奖励
        main.setting setting = ConfigLoader.instance.getConfig().setting;
        Player player = getPlayer(menu.uuid);
        setting.rowReward.forEach(s -> {
            Sponge.getCommandManager().process(Sponge.getGame().getServer()
                    .getConsole(), s.replace("%player%", player.getName()));
            player.sendMessage(Text.of(setting.MsgCaptureRow.replace("&", "§")));
        });
    }

    public void allReward(menu menu) {//发放全部奖励
        main.setting setting = ConfigLoader.instance.getConfig().setting;
        Player player = getPlayer(menu.uuid);
        setting.allReward.forEach(s -> {
            Sponge.getCommandManager().process(Sponge.getGame().getServer()
                    .getConsole(), s.replace("%player%", player.getName()));

            player.sendMessage(Text.of(setting.MsgCaptureAll.replace("&", "§")));
        });
    }

    public Player getPlayer(UUID uuid) {
        if (Sponge.getServer().getPlayer(uuid).isPresent()) {
            return Sponge.getServer().getPlayer(uuid).get();
        }
        return null;
    }

}
