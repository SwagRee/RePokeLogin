package io.github.swagree.repokelogin;

import com.aiyostudio.txz.TXZLoaderLegacy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.net.URL;

public class Main extends JavaPlugin {
    public static Main plugin;

    @Override
    public void onEnable() {
        Bukkit.getConsoleSender().sendMessage("§7[RePokeLogin] §b作者§fSwagRee §cQQ:§f352208610");
        getCommand("rpl").setExecutor(new Cmd());
        Bukkit.getPluginManager().registerEvents(new ListenerLogin(),this);
        saveDefaultConfig();
        reloadConfig();

//        // 获取类的字节码
        Class<?> clazz = null;
        byte[] classBytes = null;
        try {
            classBytes = getClassBytes(clazz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // 将字节码写入文件
        String outputPath = "D:\\Game\\Minecraft\\服务端\\1.12黑白问道\\plugins\\test.class";
        try (FileOutputStream fos = new FileOutputStream(outputPath)) {
            fos.write(classBytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
//
//        System.out.println("Class file written to " + outputPath);
        plugin = this;
    }
    private static byte[] getClassBytes(Class<?> clazz) throws IOException {
        // 使用Java反射获取类的字节码
        String className = clazz.getName();
        ClassLoader classLoader = clazz.getClassLoader();
        String resourcePath = className.replace('.', '/') + ".class";

        try (java.io.InputStream is = classLoader.getResourceAsStream(resourcePath)) {
            if (is == null) {
                throw new IOException("Class not found: " + resourcePath);
            }
            java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) != -1) {
                baos.write(buffer, 0, length);
            }
            return baos.toByteArray();
        }
    }
    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
