package net.runelite.client.plugins.custom.QuestHelper;

import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.InventoryID;
import net.runelite.api.Item;

public class ItemRequirement
{
	@Getter
	private int id;
	@Getter
	private int quantity;
	private boolean equip;

	public ItemRequirement(int id)
	{
		this(id, 1);
	}

	public ItemRequirement(int id, int quantity)
	{
		this.id = id;
		this.quantity = quantity;
		equip = false;
	}

	public ItemRequirement(int id, int quantity, boolean equip)
	{
		this(id, quantity);
		this.equip = equip;
	}

	public boolean check(Client client)
	{
		Item[] items;
		if (equip)
		{
			items = client.getItemContainer(InventoryID.EQUIPMENT).getItems();
		}
		else
		{
			items = client.getItemContainer(InventoryID.INVENTORY).getItems();
		}

		int tempQuantity = quantity;
		for (Item item : items)
		{
			if (item.getId() == id)
			{
				if (item.getQuantity() >= tempQuantity)
				{
					return true;
				}
				else
				{
					tempQuantity -= item.getQuantity();
				}
			}
		}
		return false;
	}
}
