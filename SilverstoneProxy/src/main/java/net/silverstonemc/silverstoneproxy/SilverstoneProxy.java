package net.silverstonemc.silverstoneproxy;

import net.silverstonemc.silverstoneproxy.commands.*;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;

public class SilverstoneProxy extends Plugin implements Listener {

    private static SilverstoneProxy instance;
    private static LuckPerms luckPerms;

    public static SilverstoneProxy getInstance() {
        return instance;
    }

    @Override
    // Startup
    public void onEnable() {
        instance = this;
        luckPerms = LuckPermsProvider.get();

        PluginManager pluginManager = this.getProxy().getPluginManager();
        pluginManager.registerCommand(this, new FakeJoinLeave());
        pluginManager.registerCommand(this, new DragonSay());
        pluginManager.registerCommand(this, new Forums());
        pluginManager.registerCommand(this, new PandaSay());
        pluginManager.registerCommand(this, new Report());
        pluginManager.registerCommand(this, new Restart());
        pluginManager.registerCommand(this, new Say());
        pluginManager.registerCommand(this, new GlobalMsg());

        pluginManager.registerListener(this, new JoinEvent());

        getProxy().registerChannel("silverstone:pluginmsg");
    }

    public static LuckPerms getLuckPerms() {
        return luckPerms;
    }
}
