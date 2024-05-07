package cn.lelmc.bingosponge.config;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ConfigLoader {
    public static ConfigLoader instance;
    private final Path configFile;
    private main config;
    private ConfigurationLoader<CommentedConfigurationNode> loader;
    private CommentedConfigurationNode rootNode;

    public ConfigLoader(final Path configDir) throws IOException {
        instance = this;
        configFile = configDir.resolve("setting.conf");
        if (!Files.exists(configDir)) {
            Files.createDirectory(configDir);
        }
        if (!Files.exists(configFile)) {
            Files.createFile(configFile);
        }
        this.load();
    }

    public void load() {
        this.loader = HoconConfigurationLoader.builder().setPath(configFile).build();
        try {
            this.rootNode = this.loader.load(ConfigurationOptions.defaults().withShouldCopyDefaults(true));
            this.config = this.rootNode.getValue(TypeToken.of(main.class), new main());
            this.save();
        } catch (IOException | ObjectMappingException e) {
            e.printStackTrace();
        }
    }

    public void save() throws IOException {
        this.loader.save(this.rootNode);
    }

    public main getConfig() {
        return config;
    }
}
