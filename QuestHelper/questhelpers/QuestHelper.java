package net.runelite.client.plugins.custom.QuestHelper.questhelpers;

import com.google.inject.Binder;
import com.google.inject.CreationException;
import com.google.inject.Injector;
import com.google.inject.Module;
import java.util.Collection;
import javax.inject.Inject;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.Client;
import net.runelite.api.Quest;
import net.runelite.api.QuestState;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.plugins.custom.QuestHelper.steps.OwnerStep;
import net.runelite.client.plugins.custom.QuestHelper.steps.QuestStep;

public abstract class QuestHelper implements Module
{
	@Inject
	private Client client;

	@Inject
	private EventBus eventBus;

	@Getter
	private QuestStep currentStep;

	@Getter
	@Setter
	private Quest quest;

	@Setter
	private Injector injector;

	@Override
	public void configure(Binder binder)
	{
	}

	public abstract void startUp();

	public abstract void shutDown();

	public abstract boolean updateQuest();

	protected void startUpStep(QuestStep step)
	{
		if (step != null)
		{
			currentStep = step;
			currentStep.startUp();
			eventBus.register(currentStep);
		}
		else
		{
			currentStep = null;
		}
	}

	protected void shutDownStep()
	{
		if (currentStep != null)
		{
			eventBus.unregister(currentStep);
			currentStep.shutDown();
			currentStep = null;
		}
	}

	protected void instantiateSteps(Collection<QuestStep> steps)
	{
		for (QuestStep step : steps)
		{
			instantiateStep(step);
			if (step instanceof OwnerStep)
			{
				instantiateSteps(((OwnerStep) step).getSteps());
			}
		}
	}

	public void instantiateStep(QuestStep questStep)
	{
		try
		{
			injector.injectMembers(questStep);
		}
		catch (CreationException ex)
		{
			ex.printStackTrace();
		}
	}

	public boolean isCompleted()
	{
		return (quest.getState(client) == QuestState.FINISHED);
	}

	public int getVar(Quest quest)
	{
		return quest.getVar(quest).getId();
	}
}
