package io.github.regularcommands.stylize;

import net.md_5.bungee.api.ChatColor;

/**
 * Contains a basic global stylizer which allows all of vanilla Minecraft's standard text effects.
 */
public class Stylizer {
    public static final TextStylizer STYLIZER;

    static {
        STYLIZER = new TextStylizer();

        STYLIZER.addComponent("reset", in -> {
            in.setColor(ChatColor.WHITE);
            in.setBold(false);
            in.setObfuscated(false);
            in.setStrikethrough(false);
            in.setUnderlined(false);
            in.setItalic(false);
        });

        STYLIZER.addComponent("url", in -> {
            in.setColor(ChatColor.BLUE);
            in.setBold(true);
            in.setUnderlined(true);
        });

        STYLIZER.addComponent("blue", in -> in.setColor(ChatColor.BLUE));
        STYLIZER.addComponent("red", in -> in.setColor(ChatColor.RED));
        STYLIZER.addComponent("white", in -> in.setColor(ChatColor.WHITE));
        STYLIZER.addComponent("aqua", in -> in.setColor(ChatColor.AQUA));
        STYLIZER.addComponent("black", in -> in.setColor(ChatColor.BLACK));
        STYLIZER.addComponent("dark_aqua", in -> in.setColor(ChatColor.DARK_AQUA));
        STYLIZER.addComponent("dark_blue", in -> in.setColor(ChatColor.DARK_BLUE));
        STYLIZER.addComponent("dark_gray", in -> in.setColor(ChatColor.DARK_GRAY));
        STYLIZER.addComponent("dark_green", in -> in.setColor(ChatColor.DARK_GREEN));
        STYLIZER.addComponent("dark_purple", in -> in.setColor(ChatColor.DARK_PURPLE));
        STYLIZER.addComponent("dark_red", in -> in.setColor(ChatColor.DARK_RED));
        STYLIZER.addComponent("gold", in -> in.setColor(ChatColor.GOLD));
        STYLIZER.addComponent("gray", in -> in.setColor(ChatColor.GRAY));
        STYLIZER.addComponent("green", in -> in.setColor(ChatColor.GREEN));
        STYLIZER.addComponent("strikethrough", in -> in.setStrikethrough(true));
        STYLIZER.addComponent("bold", in -> in.setBold(true));
        STYLIZER.addComponent("obfuscate", in -> in.setObfuscated(true));
        STYLIZER.addComponent("underline", in -> in.setUnderlined(true));
        STYLIZER.addComponent("italicize", in -> in.setItalic(true));
    }
}