package net.runelite.client.plugins.custom.QuestHelper;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import javax.inject.Inject;

import net.runelite.client.plugins.custom.QuestHelper.questhelpers.QuestHelper;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.components.ComponentConstants;
import net.runelite.client.ui.overlay.components.PanelComponent;

public class QuestHelperOverlay extends Overlay
{
	public static final Color TITLED_CONTENT_COLOR = new Color(190, 190, 190);

	private final QuestHelperPlugin plugin;
	private final PanelComponent panelComponent = new PanelComponent();

	@Inject
	public QuestHelperOverlay(QuestHelperPlugin plugin)
	{
		this.plugin = plugin;
		setPriority(OverlayPriority.LOW);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		QuestHelper questHelper = plugin.getSelectedQuest();

		if (questHelper == null || questHelper.getCurrentStep() == null)
		{
			return null;
		}

		panelComponent.getChildren().clear();
		panelComponent.setPreferredSize(new Dimension(ComponentConstants.STANDARD_WIDTH, 0));

		questHelper.getCurrentStep().makeOverlayHint(panelComponent, plugin);

		return panelComponent.render(graphics);
	}
}
