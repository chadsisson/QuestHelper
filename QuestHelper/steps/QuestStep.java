package net.runelite.client.plugins.custom.QuestHelper.steps;

import com.google.inject.Binder;
import com.google.inject.Inject;
import com.google.inject.Module;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.SpriteID;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.plugins.custom.QuestHelper.QuestHelperPlugin;
import net.runelite.client.plugins.custom.QuestHelper.questhelpers.QuestHelper;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import static net.runelite.client.plugins.custom.QuestHelper.QuestHelperOverlay.TITLED_CONTENT_COLOR;

public abstract class QuestStep implements Module
{
	@Inject
	SpriteManager spriteManager;

	@Setter
	@Getter
	private final String text;

	@Getter
	private final QuestHelper questHelper;

	public QuestStep(QuestHelper questHelper, String text)
	{
		this.text = text;
		this.questHelper = questHelper;
	}

	@Override
	public void configure(Binder binder)
	{
	}

	public void startUp()
	{
	}

	public void shutDown()
	{
	}

	public void makeOverlayHint(PanelComponent panelComponent, QuestHelperPlugin plugin)
	{
		panelComponent.getChildren().add(TitleComponent.builder().text(questHelper.getQuest().getName()).build());

		panelComponent.getChildren().add(LineComponent.builder().left("Step: " +  questHelper.getVar()).build());
		panelComponent.getChildren().add(LineComponent.builder()
			.left(text)
			.leftColor(TITLED_CONTENT_COLOR)
			.build());
	}

	public abstract void makeWorldOverlayHint(Graphics2D graphics, QuestHelperPlugin plugin);

	public BufferedImage getQuestImage()
	{
		return spriteManager.getSprite(SpriteID.TAB_QUESTS, 0);
	}
}
