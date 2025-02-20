package io.github.swagree.repokelogin;

import catserver.api.bukkit.event.ForgeEvent;
import catserver.server.CatServer;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.dialogue.Dialogue;
import com.pixelmonmod.pixelmon.api.dialogue.DialogueInputScreen;
import com.pixelmonmod.pixelmon.api.events.dialogue.DialogueInputEvent;
import com.pixelmonmod.pixelmon.api.events.drops.CustomDropsEvent;
import com.pixelmonmod.pixelmon.battles.status.Screen;
import fr.xephi.authme.AuthMe;
import fr.xephi.authme.api.v3.AuthMeApi;
import fr.xephi.authme.api.v3.AuthMePlayer;
import fr.xephi.authme.events.AuthMeAsyncPreLoginEvent;
import fr.xephi.authme.events.LoginEvent;
import fr.xephi.authme.events.RegisterEvent;
import net.minecraft.client.model.ModelChicken;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.v1_12_R1.EntityPlayer;
import net.minecraftforge.fml.server.FMLServerHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.libs.it.unimi.dsi.fastutil.Hash;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ListenerLogin implements Listener {

    private static HashMap<Player, BukkitRunnable> hashMap = new HashMap();
    private static HashMap<Player, String> playerHashMap = new HashMap();

    private static HashMap<Player, Boolean> playerBooleanHashMap = new HashMap();

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent event) {
        SearchUI(event.getPlayer());
    }


    public void SearchUI(OfflinePlayer player) {
        EntityPlayer handle = ((CraftPlayer) player).getHandle();
        EntityPlayerMP entityPlayerMP = (EntityPlayerMP) (Object) handle;

        if (entityPlayerMP == null) {
            return;
        }

        Player isPlayer = (Player) player;
        boolean registered = AuthMeApi.getInstance().isRegistered(isPlayer.getName());
        if (registered) {
            String title = Main.plugin.getConfig().getString("LoginMessage.title").replace("&", "§");
            String text = Main.plugin.getConfig().getString("LoginMessage.text").replace("&", "§");
            if (true) {
                BukkitRunnable dialogTask = new BukkitRunnable() {
                    @Override
                    public void run() {
                        DialogueInputScreen.builder().setTitle(title).setText(text).sendTo(entityPlayerMP);
                    }

                };
                dialogTask.runTaskTimer(Main.plugin, 10L, 10000000L);

                hashMap.put(isPlayer, dialogTask);


            }
        } else {
            String title = Main.plugin.getConfig().getString("RegisterMessage.title").replace("&", "§");
            String text = Main.plugin.getConfig().getString("RegisterMessage.text").replace("&", "§");
            if (true) {
                BukkitRunnable dialogTask = new BukkitRunnable() {
                    @Override
                    public void run() {
                        DialogueInputScreen.builder().setTitle(title).setText(text).sendTo(entityPlayerMP);

                    }

                };
                dialogTask.runTaskTimer(Main.plugin, 10L, 10000000L);

                hashMap.put(isPlayer, dialogTask);
                playerHashMap.put(isPlayer, "");
                playerBooleanHashMap.put(isPlayer, false);


            }
        }


    }


    @EventHandler(priority = EventPriority.HIGHEST)
    public void onTest(ForgeEvent event) {

        if (event.getForgeEvent() instanceof DialogueInputEvent.Submitted) {
            DialogueInputEvent.Submitted submitted = (DialogueInputEvent.Submitted) event.getForgeEvent();
            EntityPlayerMP testPlayer = submitted.getPlayer();
            UUID uniqueID = testPlayer.getUniqueID();
            Player bukkitPlayer = Bukkit.getPlayer(uniqueID);

            boolean registered = AuthMeApi.getInstance().isRegistered(bukkitPlayer.getName());

            String input = submitted.getInput();
            if (input.equals("")) {
                SearchUI(bukkitPlayer);
                return;
            }
            if(input.length()<6||input.length()>21){
                SearchUI(bukkitPlayer);
                return;
            }
            if (registered) {
                bukkitPlayer.performCommand("login " + input);
            } else {
                if (!playerBooleanHashMap.get(bukkitPlayer)) {
                    String title = Main.plugin.getConfig().getString("OnceAgainRegisterMessage.title").replace("&", "§");
                    String text = Main.plugin.getConfig().getString("OnceAgainRegisterMessage.text").replace("&", "§");

                    playerHashMap.put(bukkitPlayer, input);
                    DialogueInputScreen.builder().setTitle(title).setText(text).sendTo(testPlayer);

                    playerBooleanHashMap.put(bukkitPlayer, true);
                    return;
                }
                if (playerBooleanHashMap.get(bukkitPlayer)) {
                    if (input.equalsIgnoreCase(playerHashMap.get(bukkitPlayer))) {
                        bukkitPlayer.performCommand("reg " + input + " " + input);
                    } else {
                        bukkitPlayer.sendMessage(Main.plugin.getConfig().getString("RegisterPasswordMessage.message").replace("&", "§"));
                        SearchUI(bukkitPlayer);
                    }
                }

            }

        }
        if (event.getForgeEvent() instanceof DialogueInputEvent.Submitted.Submitted.ClosedScreen) {
            DialogueInputEvent.ClosedScreen closedScreen = (DialogueInputEvent.ClosedScreen) event.getForgeEvent();
            EntityPlayerMP testPlayer = closedScreen.getPlayer();
            UUID uniqueID = testPlayer.getUniqueID();
            Player bukkitPlayer = Bukkit.getPlayer(uniqueID);
            if (!AuthMeApi.getInstance().isAuthenticated(bukkitPlayer)) {
                SearchUI(bukkitPlayer);
            }
        }
    }

    @EventHandler
    public void onLogin(LoginEvent event) {
        Player bukkitPlayer = event.getPlayer();
        BukkitRunnable bukkitRunnable = hashMap.get(bukkitPlayer);
        if (bukkitRunnable == null) {
            return;
        }
        bukkitRunnable.cancel();
        hashMap.remove(bukkitPlayer);
    }

    @EventHandler
    public void onReg(RegisterEvent event) {
        Player bukkitPlayer = event.getPlayer();
        BukkitRunnable bukkitRunnable = hashMap.get(bukkitPlayer);
        if (bukkitRunnable == null) {
            return;
        }
        bukkitRunnable.cancel();
        hashMap.remove(bukkitPlayer);

    }


}
