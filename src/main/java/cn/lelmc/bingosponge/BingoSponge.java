package cn.lelmc.bingosponge;

import cn.lelmc.bingosponge.config.ConfigLoader;
import cn.lelmc.bingosponge.listener.event;
import com.google.inject.Inject;
import com.pixelmonmod.pixelmon.Pixelmon;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

@Plugin(
        id = "bingo-sponge",
        name = "Bingo Sponge",
        description = "bingo go go go..",
        authors = {
                "lelmc"
        }
)
public class BingoSponge {
    public static BingoSponge instance;
    public static Map<UUID, menu> go = new HashMap<>();

    @Inject
    private Logger logger;

    @Inject
    @ConfigDir(sharedRoot = false)
    public Path path;

    @Listener
    public void onServerStart(GameStartedServerEvent event) throws IOException {
        instance = this;
        new ConfigLoader(path);

        Pixelmon.EVENT_BUS.register(new event());
        Sponge.getCommandManager().register(this, BINGO, "bingo");
        Sponge.getCommandManager().register(this, RELOAD, "bingoReload");
    }

    @Listener
    public void onJoin(ClientConnectionEvent.Join event){
        if (!go.containsKey(event.getTargetEntity().getUniqueId())){
            String s = ConfigLoader.instance.getConfig().setting.MsgJoin.replace("&", "§");
            event.getTargetEntity().sendMessage(Text.of(s));
        }
    }

    CommandSpec BINGO = CommandSpec.builder()
            .executor((src, args) -> {
                if (src instanceof Player){
                    Player player = (Player)src;
                    if (go.containsKey(player.getUniqueId())){
                        menu menu = go.get(player.getUniqueId());
                        player.openInventory(menu.inventory);
                    } else {
                        menu menu = new menu(player.getUniqueId());
                        player.openInventory(menu.inventory);
                        go.put(player.getUniqueId(), menu);
                    }
                }
                return CommandResult.success();
            })
            .build();

    CommandSpec RELOAD = CommandSpec.builder()
            .permission("bingo.reload")
            .executor((src, args) -> {
                ConfigLoader.instance.load();
                src.sendMessage(Text.of("Bingo 配置文件已经重新加载"));
                return CommandResult.success();
            })
            .build();

}
