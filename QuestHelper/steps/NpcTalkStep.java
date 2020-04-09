package net.runelite.client.plugins.custom.QuestHelper.steps;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Arrays;
import java.util.List;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.NpcDespawned;
import net.runelite.api.events.NpcSpawned;
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

import static net.runelite.client.plugins.custom.QuestHelper.QuestHelperWorldOverlay.IMAGE_Z_OFFSET;

public class NpcTalkStep extends QuestStep
{
	@Inject
	protected Client client;

	@Inject
	protected ItemManager itemManager;

	@Inject
	protected WorldMapPointManager worldMapPointManager;

	private int npcID;
	private WorldPoint worldPoint;
	private NPC npc;
	List<ItemRequirement> itemRequirements;

	public NpcTalkStep(QuestHelper questHelper, int npcID, WorldPoint worldPoint, String text, ItemRequirement... itemRequirements)
	{
		super(questHelper, text);
		this.npcID = npcID;
		this.worldPoint = worldPoint;
		this.itemRequirements = Arrays.asList(itemRequirements);
	}

	@Override
	public void startUp()
	{
		for (NPC npc : client.getNpcs())
		{
			if (npcID == npc.getId())
			{
				this.npc = npc;
				client.setHintArrow(npc);
			}
		}
		worldMapPointManager.add(new QuestHelperWorldMapPoint(worldPoint, getQuestImage()));
	}

	@Override
	public void shutDown()
	{
		npc = null;
		client.clearHintArrow();
		worldMapPointManager.removeIf(QuestHelperWorldMapPoint.class::isInstance);
	}

	@Subscribe
	public void onNpcSpawned(NpcSpawned event)
	{
		if (event.getNpc().getId() == npcID && npc == null)
		{
			npc = event.getNpc();
			client.setHintArrow(npc);
		}
	}

	@Subscribe
	public void onNpcDespawned(NpcDespawned event)
	{
		if (event.getNpc().equals(npc))
		{
			npc = null;
		}
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		if (worldPoint != null
			&& (client.getHintArrowNpc() == null
			|| !client.getHintArrowNpc().equals(npc)))
		{
			client.setHintArrow(worldPoint);
		}
	}

	@Override
	public void makeOverlayHint(PanelComponent panelComponent, QuestHelperPlugin plugin)
	{
		super.makeOverlayHint(panelComponent, plugin);

		if (itemRequirements.isEmpty())
		{
			return;
		}

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
		if (!worldPoint.isInScene(client))
		{
			return;
		}

		if (npc == null)
		{
			return;
		}

		OverlayUtil.renderActorOverlayImage(graphics, npc, getQuestImage(), Color.CYAN, IMAGE_Z_OFFSET);
	}
}
