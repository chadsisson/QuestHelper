package net.runelite.client.plugins.custom.QuestHelper.steps;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.custom.QuestHelper.QuestHelperPlugin;
import net.runelite.client.plugins.custom.QuestHelper.QuestHelperWorldMapPoint;
import net.runelite.client.plugins.custom.QuestHelper.questhelpers.QuestHelper;
import net.runelite.client.ui.overlay.OverlayUtil;
import net.runelite.client.ui.overlay.worldmap.WorldMapPointManager;

public class TileStep extends QuestStep
{
	@Inject
	private Client client;

	@Inject
	private WorldMapPointManager worldMapPointManager;

	private WorldPoint worldPoint;

	public TileStep(QuestHelper questHelper, WorldPoint worldPoint, String text)
	{
		super(questHelper, text);
		this.worldPoint = worldPoint;
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

	@Override
	public void makeWorldOverlayHint(Graphics2D graphics, QuestHelperPlugin plugin) {
		LocalPoint lp = LocalPoint.fromWorld(client, worldPoint);
		if (lp == null)
		{
			return;
		}

		Polygon poly = Perspective.getCanvasTilePoly(client, lp);
		if (poly == null)
		{
			return;
		}

		OverlayUtil.renderPolygon(graphics, poly, Color.cyan);
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		if (worldPoint != null)
		{
			client.setHintArrow(worldPoint);
		}
	}

}
