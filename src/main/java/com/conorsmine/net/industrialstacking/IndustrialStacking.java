package com.conorsmine.net.industrialstacking;

import com.conorsmine.net.industrialstacking.cmd.IndustrialStackingCmd;
import com.conorsmine.net.industrialstacking.files.MachineConfigFile;
import com.conorsmine.net.industrialstacking.files.MachineSaveFile;
import com.conorsmine.net.industrialstacking.files.MachineSaveWrapper;
import com.conorsmine.net.industrialstacking.machinestack.StackableMachines;
import com.conorsmine.net.industrialstacking.machinestack.StackableMods;
import com.conorsmine.net.industrialstacking.modconfigs.ModConfigManager;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public final class IndustrialStacking extends JavaPlugin {

    private StackManager stackManager;
    private MachineConfigFile machineConfigFile;
    private MachineSaveFile machineSaveFile;
    private ModConfigManager modConfigManager;
    private final CommandSender consoleSender = getServer().getConsoleSender();
    private final IndustrialStackingCmd mainCommand = new IndustrialStackingCmd(this);

    @Override
    public void onEnable() {
        long startTime = System.currentTimeMillis();
        consoleSender.sendMessage(getPrefix());
        consoleSender.sendMessage(getFancyLogo());
        consoleSender.sendMessage(getPrefix());

        initConfig();

        consoleSender.sendMessage(getPrefix());
        consoleSender.sendMessage(String.format("%s§7Enabling §6machine stack manager§7...", getPrefix()));
        stackManager = new StackManager(this);
        stackManager.putAll(machineSaveFile.mapDeserializedData());
        getServer().getPluginManager().registerEvents(new EvenListener(this), this);
        getCommand("industrialStacking").setExecutor(mainCommand);

        consoleSender.sendMessage(getPrefix());
        consoleSender.sendMessage(String.format("%s§aSuccessfully enabled §r%s§r§7-§6%s", getPrefix(), getPrefix().replaceAll("\\s*$", ""), getDescription().getVersion()));
        consoleSender.sendMessage(String.format("%s§7Plugin took: §3%dms §7to enable.", getPrefix(), (System.currentTimeMillis() - startTime)));
    }

    @Override
    public void onDisable() {
        machineSaveFile.save();

        getServer().getConsoleSender().sendMessage(String.format("%s§3Goodbye §7( ^_^)/", getPrefix()));
    }

    private void initConfig() {
        this.saveDefaultConfig();

        consoleSender.sendMessage(String.format("%s§aLoading §6plugin config:§r", getPrefix()));
        machineConfigFile = new MachineConfigFile(this).initConfig();
        runConfigFileInfo(consoleSender);

        consoleSender.sendMessage(getPrefix());
        consoleSender.sendMessage(String.format("%s§aLoading §6save file:§r", getPrefix()));
        machineSaveFile = new MachineSaveFile(this);
        runSaveFileInfo(consoleSender);

        consoleSender.sendMessage(getPrefix());
        consoleSender.sendMessage(String.format("%s§aLoading §6mod configs:§r", getPrefix()));
        modConfigManager = new ModConfigManager(this);
        runModConfigInfo(consoleSender);
    }

    public void sendInfoDataMsg(CommandSender sender) {
        sender.sendMessage(String.format("%s§r§7§m     §r %s §7§m     §r", getPrefix(), getPrefix()));
        sender.sendMessage(String.format("%s§aEnabling %s§7-§6%s§r", getPrefix(), getPrefix(), getDescription().getVersion()));

        sendConfigFileInfo(sender);
        sendSaveFileInfo(sender);
        sendModConfigInfo(sender);
    }

    public void sendConfigFileInfo(CommandSender sender) {
        sender.sendMessage(getPrefix());
        sender.sendMessage(String.format("%s§aLoading §6plugin config§a:§r", getPrefix()));
        runConfigFileInfo(sender);
    }

    private void runConfigFileInfo(CommandSender sender) {
        Iterator<StackableMods> configIterator = machineConfigFile.getIdOffsetMap().keySet().iterator();
        while (configIterator.hasNext()) {
            StackableMods mods = configIterator.next();
            sender.sendMessage(String.format("%s§e%s §7machine configs:§r", getPrefix(), mods.name()));
            sender.sendMessage(String.format("%s §3id offset: §b%s§r", getPrefix(), machineConfigFile.getIdOffsetMap().getOrDefault(mods, 0)));
            sender.sendMessage(String.format("%s §3max stack sizes:§r", getPrefix()));

            for (StackableMachines machines : StackableMachines.getModMachines().get(mods)) {
                int maxStackSize = machineConfigFile.getMaxStackSizeMap().getOrDefault(machines.getConfigName(), 0);
                sender.sendMessage(String.format("%s  §7>> §3%s: §b%d§r", getPrefix(), machines.name(), maxStackSize));
            }

            if (configIterator.hasNext()) sender.sendMessage(getPrefix());
        }
    }

    public void sendSaveFileInfo(CommandSender sender) {
        sender.sendMessage(getPrefix());
        sender.sendMessage(String.format("%s§aLoading §6save file§a:§r", getPrefix()));
        runSaveFileInfo(sender);
    }

    private void runSaveFileInfo(CommandSender sender) {
        int total = 0;
        for (Map.Entry<Material, List<MachineSaveWrapper>> entry : machineSaveFile.deserialize().entrySet()) {
            int size = entry.getValue().size();
            if (size <= 0) continue;
            sender.sendMessage(String.format("%s  §7>> §3%s: §b%d §7instances§r", getPrefix(), StackableMachines.machineFromName(entry.getKey().name()), size));
            total += size;
        }
        sender.sendMessage(String.format("%s§7Loaded a total of §b%d §7stacked machines.§7", getPrefix(), total));
    }

    public void sendModConfigInfo(CommandSender sender) {
        sender.sendMessage(getPrefix());
        sender.sendMessage(String.format("%s§aLoading §6mod configs§a:§r", getPrefix()));
        runModConfigInfo(sender);
    }

    private void runModConfigInfo(CommandSender sender) {
        boolean isPlayer = (sender instanceof Player);
        boolean ifLoaded = modConfigManager.getForegoingConfigParser().isInstalled();
        boolean minerLoaded = modConfigManager.getVoidMinerConfigParser().isInstalled();
        if (isPlayer) sender.sendMessage(String.format("%s§7Note: Hover over the §3machine names §7for more info.§r", getPrefix()));
        sender.sendMessage(String.format("%s§eIndustrialForegoings §7config is §r%s§7.§r", getPrefix(),
                (ifLoaded ? "§apresent" : "§cmissing")));
        if (ifLoaded) {
            sender.sendMessage(String.format("%s§7The following machine configs have been found:§r", getPrefix()));

            for (StackableMachines ifMachine : modConfigManager.getForegoingConfig().keySet()) {
                String dataInfo = modConfigManager.getForegoingConfig().get(ifMachine).getConfigData().toString();
                TextComponent dataText = new TextComponent(String.format(" §7>> §3%s", ifMachine.name()));
                dataText.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(dataInfo).create()));

                ComponentBuilder componentBuilder = new ComponentBuilder(getPrefix());
                componentBuilder.append(dataText);
                sender.spigot().sendMessage(componentBuilder.create());
            }
            sender.sendMessage(getPrefix());
        }

        sender.sendMessage(String.format("%s§eCompactVoidMiners §7config is §r%s§7.§r", getPrefix(),
                (minerLoaded ? "§apresent" : "§cmissing")));
        if (minerLoaded) {
            sender.sendMessage(String.format("%s§7The following machine configs have been found:§r", getPrefix()));

            for (StackableMachines minerMachine : modConfigManager.getVoidMinerConfig().keySet()) {
                String dataInfo = modConfigManager.getVoidMinerConfig().get(minerMachine).getConfigData().toString();
                TextComponent dataText = new TextComponent(String.format("§7>> §3%s", minerMachine.name()));
                dataText.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(dataInfo).create()));

                ComponentBuilder componentBuilder = new ComponentBuilder(getPrefix());
                componentBuilder.append(dataText);
                sender.spigot().sendMessage(componentBuilder.create());
            }
        }


        // Funnily enough, they won't... since I'm not even adding machines of non-existent mods
        // to the StackManager map.
        if (!(ifLoaded && minerLoaded))
            sender.sendMessage(String.format("%s§cWARNING: §7Stacked machines of missing mods might be §cdeleted§7!§r", getPrefix()));
    }

    public String[] getFancyLogo() {
        // Img from: https://discover.hubpages.com/art/ascii

        return new String[] { getPrefix() + "§8ooooo  §6 .oooooo.. §r",
                              getPrefix() + "§8`888'  §6d8P'    `Y8§r",
                              getPrefix() + "§8 888   §6Y88bo.     §r   " + getPrefix(),
                              getPrefix() + "§8 888   §6 `\"Y8888bo.§r   §7Version: §6" + getDescription().getVersion(),
                              getPrefix() + "§8 888   §6     `\"Y88b§r",
                              getPrefix() + "§8 888   §6oo     .d8P§r",
                              getPrefix() + "§8o888o  §68\"\"88888P' §r" };
    }

    public File getModsConfigDir() {
        final File rootFile = getServer().getWorldContainer().getParentFile();
        return new File(rootFile, "config");
    }

    public String getPrefix() {
        return "§c§l[§8Industrial§6Stacking§c§l]§r ";
    }

    public StackManager getStackManager() {
        return stackManager;
    }

    public MachineConfigFile getMachineConfigFile() {
        return machineConfigFile;
    }

    public MachineSaveFile getMachineSaveFile() {
        return machineSaveFile;
    }

    public ModConfigManager getModConfigManager() {
        return modConfigManager;
    }
}
