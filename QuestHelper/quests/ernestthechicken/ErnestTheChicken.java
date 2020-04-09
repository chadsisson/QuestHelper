package net.runelite.client.plugins.custom.QuestHelper.quests.ernestthechicken;

import java.util.HashMap;
import java.util.Map;
import net.runelite.api.NpcID;
import net.runelite.api.Quest;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.custom.QuestHelper.QuestDescriptor;
import net.runelite.client.plugins.custom.QuestHelper.questhelpers.BasicQuestHelper;
import net.runelite.client.plugins.custom.QuestHelper.steps.NpcTalkStep;
import net.runelite.client.plugins.custom.QuestHelper.steps.QuestStep;

@QuestDescriptor(
	quest = Quest.ERNEST_THE_CHICKEN
)
public class ErnestTheChicken extends BasicQuestHelper
{
	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		Map<Integer, QuestStep> steps = new HashMap<>();

		//TODO fix this step
		steps.put(0, new NpcTalkStep(this, NpcID.WIZARD_MIZGOG, new WorldPoint(3103, 3163, 2),
			"Talk to Wizard Mizgog on the top floor of the Wizards' Tower with the required items to finish the quest."));

		//fish food id: 272 wp: 3108, 3356, 1
		//poison: id: 273: wp: 3097, 3366, 0
		//poisoned fish food: id: 274

		steps.put(1, new ErnestPuzzleStep(this));

		return steps;
	}
}
