package net.runelite.client.plugins.custom.QuestHelper.steps;

import java.awt.Graphics2D;
import java.util.Collection;
import java.util.LinkedHashMap;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.custom.QuestHelper.QuestHelperPlugin;
import net.runelite.client.plugins.custom.QuestHelper.Zone;
import net.runelite.client.plugins.custom.QuestHelper.questhelpers.QuestHelper;
import net.runelite.client.ui.overlay.components.PanelComponent;

public class ZoneStep extends QuestStep implements OwnerStep
{
	@Inject
	protected Client client;

	@Inject
	protected EventBus eventBus;

	private LinkedHashMap<Zone, QuestStep> steps;
	private QuestStep currentStep;
	private WorldPoint lastWorldPoint;

	public ZoneStep(QuestHelper questHelper, LinkedHashMap<Zone, QuestStep> steps)
	{
		super(questHelper, null);
		this.steps = steps;
	}

	@Override
	public void startUp()
	{
		updateSteps();
	}

	@Override
	public void shutDown()
	{
		shutDownStep();
		currentStep = null;
		lastWorldPoint = null;
	}

	@Override
	public void makeWorldOverlayHint(Graphics2D graphics, QuestHelperPlugin plugin) {
		currentStep.makeWorldOverlayHint(graphics, plugin);
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		WorldPoint currentWorldPoint = client.getLocalPlayer().getWorldLocation();
		if (lastWorldPoint == null || !currentWorldPoint.equals(lastWorldPoint))
		{
			updateSteps();
			lastWorldPoint = currentWorldPoint;
		}
	}

	private void updateSteps()
	{
		for (Zone zone : steps.keySet())
		{
			if (zone != null || zone.contains(client.getLocalPlayer().getWorldLocation()))
			{
				if (currentStep == null || !steps.get(zone).equals(currentStep))
				{
					shutDownStep();
					startUpStep(steps.get(zone));
				}
				break;
			}
		}

		if (steps.containsKey(null))
		{
			shutDownStep();
			startUpStep(steps.get(null));
		}
	}

	private void startUpStep(QuestStep step)
	{
		if (step != null)
		{
			currentStep = step;
			eventBus.register(currentStep);
			currentStep.startUp();
		}
		else
		{
			currentStep = null;
		}
	}

	private void shutDownStep()
	{
		if (currentStep != null)
		{
			eventBus.unregister(currentStep);
			currentStep.shutDown();
			currentStep = null;
		}
	}

	@Override
	public void makeOverlayHint(PanelComponent panelComponent, QuestHelperPlugin plugin)
	{
		currentStep.makeOverlayHint(panelComponent, plugin);
	}


	@Override
	public Collection<QuestStep> getSteps()
	{
		return steps.values();
	}
}
