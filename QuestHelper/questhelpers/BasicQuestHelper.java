package net.runelite.client.plugins.custom.QuestHelper.questhelpers;

import net.runelite.client.plugins.custom.QuestHelper.steps.QuestStep;

import java.util.Map;

public abstract class BasicQuestHelper extends QuestHelper
{
	protected Map<Integer, QuestStep> steps;
	protected int var;

	@Override
	public void startUp()
	{
		steps = loadSteps();
		instantiateSteps(steps.values());
		var = getVar();
		startUpStep(steps.get(var));
	}

	@Override
	public void shutDown()
	{
		steps = null;
		shutDownStep();
	}

	@Override
	public boolean updateQuest()
	{
		if (var != getVar())
		{
			shutDownStep();
			startUpStep(steps.get(getVar()));
			return true;
		}
		return false;
	}

	public abstract Map<Integer, QuestStep> loadSteps();
}
