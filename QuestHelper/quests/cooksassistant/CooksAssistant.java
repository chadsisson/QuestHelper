package net.runelite.client.plugins.custom.QuestHelper.quests.cooksassistant;/

import java.util.HashMap;
import java.util.Map;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.Quest;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.custom.QuestHelper.ItemRequirement;
import net.runelite.client.plugins.custom.QuestHelper.QuestDescriptor;
import net.runelite.client.plugins.custom.QuestHelper.questhelpers.BasicQuestHelper;
import net.runelite.client.plugins.custom.QuestHelper.steps.NpcTalkStep;
import net.runelite.client.plugins.custom.QuestHelper.steps.QuestStep;

@QuestDescriptor(
	quest = Quest.COOKS_ASSISTANT
)
public class CooksAssistant extends BasicQuestHelper
{
	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		Map<Integer, QuestStep> steps = new HashMap<>();

		steps.put(0, new NpcTalkStep(this, NpcID.COOK_4626, new WorldPoint(3206, 3214, 0),
			"Give the Cook in Lumbridge Castle's kitchen the required items to finish the quest.",
			new ItemRequirement(ItemID.BUCKET_OF_MILK), new ItemRequirement(ItemID.POT_OF_FLOUR),
			new ItemRequirement(ItemID.EGG)));

		steps.put(1, steps.get(0));

		return steps;
	}
}