package net.runelite.client.plugins.custom.QuestHelper.steps;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.ItemID;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;

import net.runelite.client.plugins.custom.QuestHelper.ItemRequirement;
import net.runelite.client.plugins.custom.QuestHelper.QuestHelperPlugin;
import net.runelite.client.plugins.custom.QuestHelper.QuestHelperWorldMapPoint;
import net.runelite.client.plugins.custom.QuestHelper.questhelpers.QuestHelper;
import net.runelite.client.ui.overlay.OverlayUtil;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;
import net.runelite.client.ui.overlay.worldmap.WorldMapPointManager;

public class DigStep extends QuestStep
{
	@Inject
	Client client;

	@Inject
	ItemManager itemManager;

	@Inject
	WorldMapPointManager worldMapPointManager;

	private final WorldPoint worldPoint;
	private final List<ItemRequirement> itemRequirements = new ArrayList<>();

	public DigStep(QuestHelper questHelper, WorldPoint worldPoint, String text, ItemRequirement... itemRequirements)
	{
		super(questHelper, text);
		this.worldPoint = worldPoint;
		this.itemRequirements.add(0, new ItemRequirement(ItemID.SPADE));
		Collections.addAll(this.itemRequirements, itemRequirements);
	}

	@Override
	public void startUp()
	{
		worldMapPointManager.add(new QuestHelperWorldMapPoint(worldPoint, getQuestImage()));
	}

	@Override
	public void shutDown()
	{
		worldMapPointManager.removeIf(QuestHelperWorldMapPoint.class::isInstance);
		client.clearHintArrow();
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		System.out.println("test");
		if (worldPoint != null)
		{
			client.setHintArrow(worldPoint);
		}
	}

	@Override
	public void makeOverlayHint(PanelComponent panelComponent, QuestHelperPlugin plugin)
	{
		super.makeOverlayHint(panelComponent, plugin);

		panelComponent.getChildren().add(LineComponent.builder().left("Required Items:").build());
		for (ItemRequirement itemRequirement : itemRequirements)
		{
			String text = itemRequirement.getQuantity() + " x " + itemManager.getItemDefinition(itemRequirement.getId()).getName();
			Color color;
			if (itemRequirement.check(client))
			{
				color = Color.GREEN;
			}
			else
			{
				color = Color.RED;
			}
			panelComponent.getChildren().add(LineComponent.builder()
				.left(text)
				.leftColor(color)
				.build());
		}
	}

	@Override
	public void makeWorldOverlayHint(Graphics2D graphics, QuestHelperPlugin plugin)
	{
		LocalPoint localLocation = LocalPoint.fromWorld(client, worldPoint);

		if (localLocation == null)
		{
			return;
		}

		OverlayUtil.renderTileOverlay(client, graphics, localLocation, getSpadeImage(), Color.ORANGE);
	}

	private BufferedImage getSpadeImage()
	{
		return itemManager.getImage(ItemID.SPADE);
	}
}
