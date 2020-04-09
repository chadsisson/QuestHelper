package net.runelite.client.plugins.custom.QuestHelper;


import static net.runelite.api.Constants.REGION_SIZE;
import net.runelite.api.coords.WorldPoint;

public class Zone
{
	private int minX;
	private int maxX;
	private int minY;
	private int maxY;
	private int minPlane = 0;
	private int maxPlane = 2;

	//The first plane of the "Overworld"
	public Zone()
	{
		minX = 1152;
		maxX = 3903;
		minY = 2496;
		maxY = 4159;
		maxPlane = 0;
	}

	public Zone(WorldPoint p1, WorldPoint p2)
	{
		minX = Math.min(p1.getX(), p2.getX());
		maxX = Math.max(p1.getX(), p2.getX());
		minY = Math.min(p1.getY(), p2.getY());
		maxY = Math.max(p1.getY(), p2.getY());
		minPlane = Math.min(p1.getPlane(), p2.getPlane());
		maxPlane = Math.max(p1.getPlane(), p2.getPlane());
	}

	public Zone(int regionID)
	{
		int regionX = (regionID >> 8) & 0xff;
		int regionY = regionID & 0xff;
		minX = regionX >> 6;
		maxX = minX + REGION_SIZE;
		minY = regionY >> 6;
		maxY = minY + REGION_SIZE;
	}

	public Zone(int regionID, int plane)
	{
		this(regionID);
		minPlane = plane;
		maxPlane = plane;
	}

	public boolean contains(WorldPoint worldPoint)
	{
		if (minX <= worldPoint.getX()
			&& worldPoint.getX() <= maxX
			&& minY <= worldPoint.getY()
			&& worldPoint.getY() <= maxY
			&& minPlane <= worldPoint.getPlane()
			&& worldPoint.getPlane() <= maxPlane)
		{
			return true;
		}

		return false;
	}
}
