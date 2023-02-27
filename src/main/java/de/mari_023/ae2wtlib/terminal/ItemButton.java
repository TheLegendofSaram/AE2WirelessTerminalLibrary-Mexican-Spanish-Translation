package de.mari_023.ae2wtlib.terminal;

import java.util.Collections;
import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import org.jetbrains.annotations.NotNull;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import appeng.client.gui.widgets.ITooltip;

public class ItemButton extends Button implements ITooltip {

    private final ResourceLocation texture;
    public static final ResourceLocation TEXTURE_STATES = new ResourceLocation("ae2", "textures/guis/states.png");

    public ItemButton(OnPress onPress, ResourceLocation texture) {
        super(0, 0, 16, 16, Component.empty(), onPress, Button.DEFAULT_NARRATION);
        this.texture = texture;
    }

    public void setVisibility(final boolean vis) {
        visible = vis;
        active = vis;
    }

    @Override
    public void renderWidget(PoseStack matrices, final int mouseX, final int mouseY, float partial) {
        if (!visible)
            return;
        matrices.pushPose();
        RenderSystem.setShaderTexture(0, TEXTURE_STATES);
        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        if (isFocused()) {
            fill(matrices, getX() - 1, getY() - 1, getX() + 17, getY() + 17, 0xFFFFFFFF);
        }
        blit(matrices, getX(), getY(), 16, 16, 240, 240, 16, 16, 256, 256);
        if (active)
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        else
            RenderSystem.setShaderColor(0.5f, 0.5f, 0.5f, 1.0f);
        RenderSystem.setShaderTexture(0, texture);
        blit(matrices, getX(), getY(), 16, 16, 0, 0, 512, 512, 512, 512);
        RenderSystem.enableDepthTest();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        matrices.popPose();
    }

    @Override
    public @NotNull List<Component> getTooltipMessage() {
        return Collections.singletonList(getMessage());
    }

    @Override
    public Rect2i getTooltipArea() {
        return new Rect2i(getX(), getY(), 16, 16);
    }

    @Override
    public boolean isTooltipAreaVisible() {
        return visible;
    }
}
