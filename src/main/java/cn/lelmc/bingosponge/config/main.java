package cn.lelmc.bingosponge.config;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.ArrayList;
import java.util.List;

@ConfigSerializable
public class main {

    @Setting("连接配置")
    public setting setting;

    public main() {
        setting = new setting();
    }

    @ConfigSerializable
    public static class setting {
        @Setting(comment = "冰果任务菜单标题")
        public String Title = "§c§l乐联§7->§a§l冰果任务";
        @Setting(comment = "冰果任务介绍标题")
        public String hintTitle = "§a§l冰果任务介绍信息";
        @Setting(comment = "冰果任务介绍lore")
        public List<String> hintLore = new ArrayList<String>(){{
            add("&9&m----------------");
            add("§7每天给你一套随机的 §a21 &7只宝可梦卡片");
            add("§7只要找到并捕捉它就可以获得一份奖励");
            add("§b完成一横排&7/&b完成全部是会有额外奖励");
            add("");
            add("§e如果一只宝可梦出现了 §a2§e 次怎么办？");
            add("§7出现几次捕捉几次即可");
        }};

        @Setting(comment = "完成单个的奖励")
        public List<String> singleReward = new ArrayList<String>(){{
            add("give %player% lelmc:lucky_block 1");
        }};
        @Setting(comment = "完成一排的奖励")
        public List<String> rowReward = new ArrayList<String>(){{
            add("give %player% lelmc:lucky_block 1");
        }};
        @Setting(comment = "完成全部的奖励")
        public List<String> allReward = new ArrayList<String>(){{
            add("give %player% lelmc:lucky_block 1");
        }};

        @Setting(comment = "进入一些后的提示信息")
        public String MsgJoin = "     &e(!) &9为你开启了新的冰果任务\n     &9完成它可以获取&a23&9幸运方块\n&d输入&c/bingo&d可以查看,它祝你好运!";
        @Setting(comment = "完成一个提示")
        public String MsgCapture = "&9恭喜你完成了一个冰果任务";
        @Setting(comment = "完成一排提示")
        public String MsgCaptureRow = "&d恭喜你完成了一排的冰果任务";
        @Setting(comment = "完成全部提示")
        public String MsgCaptureAll = "&c恭喜你完成了全部的冰果任务";
    }
}
