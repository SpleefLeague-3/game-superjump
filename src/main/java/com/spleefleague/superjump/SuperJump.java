/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.superjump;

import com.spleefleague.core.Core;
import com.spleefleague.core.chat.Chat;
import com.spleefleague.core.game.arena.Arenas;
import com.spleefleague.core.game.battle.Battle;
import com.spleefleague.core.menu.InventoryMenuAPI;
import com.spleefleague.core.menu.InventoryMenuContainerChest;
import com.spleefleague.core.menu.InventoryMenuItem;
import com.spleefleague.core.menu.InventoryMenuUtils;
import com.spleefleague.core.menu.hotbars.main.GamemodeMenu;
import com.spleefleague.core.player.CoreDBPlayer;
import com.spleefleague.core.player.PlayerManager;
import com.spleefleague.core.plugin.CorePlugin;
import com.spleefleague.coreapi.chat.ChatColor;
import com.spleefleague.superjump.commands.SuperJumpCommand;
import com.spleefleague.superjump.game.SJMode;
import com.spleefleague.superjump.player.SuperJumpPlayer;
import com.spleefleague.superjump.util.SJUtils;
import net.kyori.adventure.text.Component;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;

/**
 * @author NickM13
 */
public class SuperJump extends CorePlugin {
    
    private static SuperJump instance;
    
    private InventoryMenuItem superJumpMenuItem;

    private PlayerManager<SuperJumpPlayer, CoreDBPlayer> playerManager;
    
    @Override
    public void init() {
        instance = this;

        setPluginDB("SuperJump");

        initCommands();
        
        playerManager = new PlayerManager<>(this, SuperJumpPlayer.class, CoreDBPlayer.class, getPluginDB().getCollection("Players"));
        
        SJUtils.init();
        SJMode.init();
        for (SJMode mode : SJMode.values()) {
            if (mode.getBattleMode() != null) {
                addBattleManager(mode.getBattleMode());
            }
        }
        
        //SJArena.init();
        initMenu();
        initLeaderboards();
    }
    
    public static SuperJump getInstance() {
        return instance;
    }
    
    @Override
    public void close() {
        playerManager.close();
    }

    public PlayerManager<SuperJumpPlayer, CoreDBPlayer> getPlayers() {
        return playerManager;
    }

    @Override
    public Component getChatPrefix() {
        return Component.text(Chat.TAG_BRACE + "[" + Chat.TAG + "SuperJump" + Chat.TAG_BRACE + "] ");
    }
    
    private void initCommands() {
        Core.getInstance().addCommand(new SuperJumpCommand());
    }
    
    public InventoryMenuItem getSJMenuItem() {
        return superJumpMenuItem;
    }

    private int getCurrentlyPlaying() {
        int playing = 0;
        for (SJMode mode : SJMode.values()) {
            for (Battle<?> battle : mode.getBattleMode().getOngoingBattles().values()) {
                playing += battle.getBattlers().size();
            }
        }
        return playing;
    }

    private void addMenuItem(InventoryMenuContainerChest container, SJMode mode, int x, int y) {
        if (mode.getBattleMode() == null) {
            container.addStaticItem(InventoryMenuUtils.createLockedMenuItem(StringUtils.capitalize(mode.name())), x, y);
        } else {
            InventoryMenuItem proMenu = Arenas.createMenu(getInstance(), mode.getBattleMode());
            container.addStaticItem(proMenu, x, y);
        }
    }
    
    private void initMenu() {
        superJumpMenuItem = InventoryMenuAPI.createItemDynamic()
                .setName(ChatColor.GOLD + "" + ChatColor.BOLD + "SuperJump")
                .setDescription("Jump and run your way to the finish line as fast as you can. Whether you are racing a single opponent, a group of friends, or even the clock, the objective is the same!"
                        /*"\n\n&7&lCurrently Playing: &6" + getCurrentlyPlaying()*/)
                .setDisplayItem(Material.LEATHER_BOOTS, 1)
                .setLinkedContainer(Arenas.createMenu(getInstance(), SJMode.CLASSIC.getBattleMode()).getLinkedChest());
        
        InventoryMenuContainerChest container = superJumpMenuItem.getLinkedChest();

        addMenuItem(container, SJMode.SHUFFLE, 2, 1);
        addMenuItem(container, SJMode.CONQUEST, 3, 1);
        addMenuItem(container, SJMode.ENDLESS, 4, 1);
        addMenuItem(container, SJMode.CLASSIC, 5, 1);
        addMenuItem(container, SJMode.PRO, 6, 1);
        addMenuItem(container, SJMode.PARTY, 3, 2);
        container.addStaticItem(InventoryMenuUtils.createLockedMenuItem("Tetronimo"), 4, 2);
        container.addStaticItem(InventoryMenuUtils.createLockedMenuItem("Memory"), 6, 2);
        container.addStaticItem(InventoryMenuUtils.createLockedMenuItem("Practice"), 5, 2);

        GamemodeMenu.getItem().getLinkedChest().addStaticItem(superJumpMenuItem, 5, 1);
    }
    
    private void initLeaderboards() {
        //EndlessSJArena.initLeaderboard();
    }
    
}
