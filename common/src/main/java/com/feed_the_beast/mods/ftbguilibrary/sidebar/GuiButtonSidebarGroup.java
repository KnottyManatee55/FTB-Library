package com.feed_the_beast.mods.ftbguilibrary.sidebar;

import com.feed_the_beast.mods.ftbguilibrary.icon.Color4I;
import com.feed_the_beast.mods.ftbguilibrary.widget.GuiHelper;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.TextComponent;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LatvianModder
 */
public class GuiButtonSidebarGroup extends AbstractButton
{
	public static Rect2i lastDrawnArea = new Rect2i(0, 0, 0, 0);

	private final AbstractContainerScreen gui;
	public final List<GuiButtonSidebar> buttons;
	private GuiButtonSidebar mouseOver;

	public GuiButtonSidebarGroup(AbstractContainerScreen g)
	{
		super(0, 0, 0, 0, TextComponent.EMPTY);
		gui = g;
		buttons = new ArrayList<>();
	}

	@Override
	public void render(PoseStack matrixStack, int mx, int my, float partialTicks)
	{
		buttons.clear();
		mouseOver = null;
		int rx, ry = 0;
		boolean addedAny;

		for (SidebarButtonGroup group : SidebarButtonManager.INSTANCE.groups)
		{
			rx = 0;
			addedAny = false;

			for (SidebarButton button : group.getButtons())
			{
				if (button.isActuallyVisible())
				{
					buttons.add(new GuiButtonSidebar(rx, ry, button));
					rx++;
					addedAny = true;
				}
			}

			if (addedAny)
			{
				ry++;
			}
		}

		for (GuiButtonSidebar button : buttons)
		{
			button.x = 1 + button.buttonX * 17;
			button.y = 1 + button.buttonY * 17;
		}

		x = Integer.MAX_VALUE;
		y = Integer.MAX_VALUE;
		int maxX = Integer.MIN_VALUE;
		int maxY = Integer.MIN_VALUE;

		for (GuiButtonSidebar b : buttons)
		{
			if (b.x >= 0 && b.y >= 0)
			{
				x = Math.min(x, b.x);
				y = Math.min(y, b.y);
				maxX = Math.max(maxX, b.x + 16);
				maxY = Math.max(maxY, b.y + 16);
			}

			if (mx >= b.x && my >= b.y && mx < b.x + 16 && my < b.y + 16)
			{
				mouseOver = b;
			}
		}

		x -= 2;
		y -= 2;
		maxX += 2;
		maxY += 2;

		width = maxX - x;
		height = maxY - y;
		//zLevel = 0F;

		matrixStack.pushPose();
		matrixStack.translate(0, 0, 500);

		Font font = Minecraft.getInstance().font;

		for (GuiButtonSidebar b : buttons)
		{
			GuiHelper.setupDrawing();
			b.button.getIcon().draw(matrixStack, b.x, b.y, 16, 16);

			if (b == mouseOver)
			{
				Color4I.WHITE.withAlpha(33).draw(matrixStack, b.x, b.y, 16, 16);
			}

			if (b.button.getCustomTextHandler() != null)
			{
				String text = b.button.getCustomTextHandler().get();

				if (!text.isEmpty())
				{
					int nw = font.width(text);
					int width = 16;
					Color4I.LIGHT_RED.draw(matrixStack, b.x + width - nw, b.y - 1, nw + 1, 9);
					font.draw(matrixStack, text, b.x + width - nw + 1, b.y, 0xFFFFFFFF);
					RenderSystem.color4f(1F, 1F, 1F, 1F);
				}
			}
		}

		if (mouseOver != null)
		{
			GuiHelper.setupDrawing();
			int mx1 = mx + 10;
			int my1 = Math.max(3, my - 9);

			List<String> list = new ArrayList<>();
			list.add(I18n.get(mouseOver.button.getLangKey()));

			if (mouseOver.button.getTooltipHandler() != null)
			{
				mouseOver.button.getTooltipHandler().accept(list);
			}

			int tw = 0;

			for (String s : list)
			{
				tw = Math.max(tw, font.width(s));
			}

			matrixStack.translate(0, 0, 500);

			Color4I.DARK_GRAY.draw(matrixStack, mx1 - 3, my1 - 2, tw + 6, 2 + list.size() * 10);

			for (int i = 0; i < list.size(); i++)
			{
				font.draw(matrixStack, list.get(i), mx1, my1 + i * 10, 0xFFFFFFFF);
			}
		}

		GuiHelper.setupDrawing();
		//zLevel = 0F;

		lastDrawnArea = new Rect2i(x, y, width, height);
		matrixStack.popPose();
	}

	@Override
	public void onPress()
	{
		if (mouseOver != null)
		{
			mouseOver.button.onClicked(Screen.hasShiftDown());
		}
	}
}