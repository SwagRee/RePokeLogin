package io.github.swagree.repokelogin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class Cmd implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length==0||args[0].equalsIgnoreCase("help")){
            sender.sendMessage("§b<§m*-----======= §b热宝可梦登录§b §m=======-----§b>");
            sender.sendMessage("§e/rpl reload §f- 重载配置文件");
            return true;

        }
        if(args[0].equalsIgnoreCase("reload")){
            Main.plugin.reloadConfig();
            sender.sendMessage("重载成功");
            return true;

        }
        return false;
    }
}
