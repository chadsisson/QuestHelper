package net.runelite.client.plugins.custom.QuestHelper;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import net.runelite.api.Point;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.worldmap.WorldMapPoint;
import net.runelite.client.util.ImageUtil;

public class QuestHelperWorldMapPoint extends WorldMapPoint
{
	private final BufferedImage questWorldImage;
	private final Point questWorldImagePoint;
	private final BufferedImage questImage;

	public QuestHelperWorldMapPoint(final WorldPoint worldPoint, BufferedImage image)
	{
		super(worldPoint, null);

		BufferedImage mapArrow = ImageUtil.getResourceStreamFromClass(getClass(), "/util/clue_arrow.png");
		questWorldImage = new BufferedImage(mapArrow.getWidth(), mapArrow.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics graphics = questWorldImage.getGraphics();
		graphics.drawImage(mapArrow, 0, 0, null);
		int buffer = mapArrow.getWidth() / 2 - image.getWidth() / 2;
		buffer = buffer < 0 ? 0 : buffer;
		graphics.drawImage(image, buffer, buffer, null);
		questWorldImagePoint = new Point(questWorldImage.getWidth() / 2, questWorldImage.getHeight());

		this.questImage = image;
		this.setSnapToEdge(true);
		this.setJumpOnClick(true);
		this.setImage(questWorldImage);
		this.setImagePoint(questWorldImagePoint);
	}

	@Override
	public void onEdgeSnap()
	{
		this.setImage(questImage);
		this.setImagePoint(null);
	}

	@Override
	public void onEdgeUnsnap()
	{
		this.setImage(questWorldImage);
		this.setImagePoint(questWorldImagePoint);
	}
}
