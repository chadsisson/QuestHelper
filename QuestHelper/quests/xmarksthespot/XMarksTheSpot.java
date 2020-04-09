package net.runelite.client.plugins.custom.QuestHelper.quests.xmarksthespot;

import java.util.HashMap;
import java.util.Map;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.Quest;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.custom.QuestHelper.ItemRequirement;
import net.runelite.client.plugins.custom.QuestHelper.QuestDescriptor;
import net.runelite.client.plugins.custom.QuestHelper.questhelpers.BasicQuestHelper;
import net.runelite.client.plugins.custom.QuestHelper.steps.DigStep;
import net.runelite.client.plugins.custom.QuestHelper.steps.NpcTalkStep;
import net.runelite.client.plugins.custom.QuestHelper.steps.QuestStep;

@QuestDescriptor(
	quest = Quest.X_MARKS_THE_SPOT
)
public class XMarksTheSpot extends BasicQuestHelper
{
	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		Map<Integer, QuestStep> steps = new HashMap<>();

		steps.put(0, new NpcTalkStep(this, NpcID.VEOS_8484, new WorldPoint(3228, 3242, 0),
			"Talk to Veos in The Sheared Ram pub in Lumbridge to start the quest."));

		steps.put(1, steps.get(0));

		steps.put(2, new DigStep(this, new WorldPoint(3230, 3209, 0),
			"Dig north of Bob's Brilliant Axes, on the west side of the plant against the wall of his house.",
			new ItemRequirement(ItemID.TREASURE_SCROLL)));

		steps.put(3, new DigStep(this, new WorldPoint(3203, 3212, 0),
			"Dig behind Lumbridge Castle, just outside the kitchen door.",
			new ItemRequirement(ItemID.TREASURE_SCROLL_23068)));

		steps.put(4, new DigStep(this, new WorldPoint(3109, 3264, 0),
			"Dig north-west of the Draynor Village jail, just by the wheat farm.",
			new ItemRequirement(ItemID.MYSTERIOUS_ORB_23069)));

		steps.put(5, new DigStep(this, new WorldPoint(3078, 3259, 0),
			"Dig in the pig pen just west where Martin the Master Gardener is.",
			new ItemRequirement(ItemID.TREASURE_SCROLL_23070)));

		steps.put(6, new NpcTalkStep(this, NpcID.VEOS_8484, new WorldPoint(3054, 3245, 0),
			"Talk to Veos directly south of the Rusty Anchor Inn in Port Sarim to finish the quest.",
			new ItemRequirement(ItemID.ANCIENT_CASKET)));

		steps.put(7, new NpcTalkStep(this, NpcID.VEOS_8484, new WorldPoint(3054, 3245, 0),
			"Talk to Veos directly south of the Rusty Anchor Inn in Port Sarim to finish the quest."));

		return steps;
	}
}
