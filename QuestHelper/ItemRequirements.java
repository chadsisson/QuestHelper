package net.runelite.client.plugins.custom.QuestHelper;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import net.runelite.api.Client;

public class ItemRequirements
{
	Set<ItemRequirement> itemRequirements;

	Set<ItemRequirements> alternates = new HashSet<>();

	public ItemRequirements(int id)
	{
		this(id, 1);
	}

	public ItemRequirements(int id, int quantity)
	{
		this(id, quantity, false);
	}

	public ItemRequirements(int id, int quantity, boolean equip)
	{
		itemRequirements = new HashSet<>();
		itemRequirements.add(new ItemRequirement(id, quantity, equip));
	}

	public ItemRequirements(ItemRequirement... itemRequirements)
	{
		this.itemRequirements = new HashSet<>();
		Collections.addAll(this.itemRequirements, itemRequirements);
	}

	public void addAlternate(ItemRequirements... itemRequirements)
	{
		Collections.addAll(alternates, itemRequirements);
	}

	public boolean check(Client client)
	{
		for (ItemRequirements itemRequirements : alternates)
		{
			if (itemRequirements.check(client))
			{
				return true;
			}
		}

		for (ItemRequirement itemRequirement : itemRequirements)
		{
			if (!itemRequirement.check(client))
			{
				return false;
			}
		}

		return true;
	}
}
