package me.myster.tyshPlugin;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class TextComponentBuilder {
    private final TextComponent textComponent;

    public TextComponentBuilder(TextComponent base) {
        this.textComponent = base.duplicate();
    }

    public TextComponentBuilder(String text, ChatColor color) {
        this.textComponent = new TextComponentBuilder().setText(text).setColor(color).build();
    }

    public static TextComponent create(String text, ChatColor color) {
        return new TextComponentBuilder(text, color).build();
    }

    public TextComponentBuilder() {
        this.textComponent = new TextComponent();
    }

    public TextComponent build() {
        return textComponent.duplicate();
    }

    public TextComponentBuilder duplicate() {
        return new TextComponentBuilder(this.build());
    }

    public TextComponentBuilder setColor(ChatColor color) {
        textComponent.setColor(color);
        return this;
    }

    public TextComponentBuilder setText(String text) {
        textComponent.setText(text);
        return this;
    }

    public TextComponentBuilder setClickEvent(ClickEvent.Action action, String value) {
        textComponent.setClickEvent(new ClickEvent(action, value));
        return this;
    }
}
