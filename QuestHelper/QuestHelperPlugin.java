package net.runelite.client.plugins.custom.QuestHelper;
import com.google.common.collect.ImmutableSet;
import com.google.common.primitives.Ints;
import com.google.common.reflect.ClassPath;
import com.google.inject.Binder;
import com.google.inject.CreationException;
import com.google.inject.Injector;
import com.google.inject.Module;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.util.Text;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.RuneLite;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.custom.QuestHelper.questhelpers.QuestHelper;
import net.runelite.client.ui.overlay.OverlayManager;

@PluginDescriptor(
	name = "Quest Helper",
	description = "Helps you with your quests"
)
@Slf4j
public class QuestHelperPlugin extends Plugin
{
	private static final int[] QUESTLIST_WIDGET_IDS = new int[]
		{
			WidgetInfo.QUESTLIST_FREE_CONTAINER.getId(),
			WidgetInfo.QUESTLIST_MEMBERS_CONTAINER.getId(),
			WidgetInfo.QUESTLIST_MINIQUEST_CONTAINER.getId(),
		};

	private static final int[] QUESTTAB_WIDGET_IDS = new int[]
		{
			WidgetInfo.RESIZABLE_VIEWPORT_BOTTOM_LINE_QUESTS_ICON.getId(),
			WidgetInfo.RESIZABLE_VIEWPORT_QUESTS_TAB.getId(),
			WidgetInfo.FIXED_VIEWPORT_QUESTS_TAB.getId(),
			WidgetInfo.QUESTTAB_QUEST_TAB.getId(),
		};

	private static final String QUEST_PACKAGE = "net.runelite.client.plugins.questhelper.quests";

	private static final String MENUOP_STARTHELPER = "Start Quest Helper"; //menu opcodes are always integers, where is this in Runelite? sec
	private static final String MENUOP_STOPHELPER = "Stop Quest Helper";

	@Inject
	private Client client;

	@Inject
	private EventBus eventBus;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private QuestHelperOverlay questHelperOverlay;

	@Inject
	private QuestHelperWorldOverlay questHelperWorldOverlay;

	@Getter
	private QuestHelper selectedQuest = null;

	private Map<String, QuestHelper> quests;

	@Override
	protected void startUp() throws IOException
	{

		quests = scanAndInstantiate(getClass().getClassLoader(), QUEST_PACKAGE);
		overlayManager.add(questHelperOverlay);
		overlayManager.add(questHelperWorldOverlay);
	}

	@Override
	protected void shutDown() throws Exception
	{
		overlayManager.remove(questHelperOverlay);
		overlayManager.remove(questHelperWorldOverlay);
		quests = null;
		shutDownQuest();
	}

	@Subscribe
	public void onVarbitChanged(VarbitChanged event)
	{
		if (!(client.getGameState() == GameState.LOGGED_IN))
		{
			return;
		}

		if (selectedQuest != null
			&& selectedQuest.updateQuest()
			&& selectedQuest.getCurrentStep() == null)
		{
			shutDownQuest();
		}
	}

	@Subscribe
	private void onMenuOptionClicked(MenuOptionClicked event)
	{
		if (event.getMenuOpcode() == MenuOpcode.RUNELITE)
		{
			switch (event.getOption())
			{
				case MENUOP_STARTHELPER:
					event.consume();
					shutDownQuest();
					String quest = Text.removeTags(event.getTarget());
					startUpQuest(quests.get(quest));
					break;
				case MENUOP_STOPHELPER:
					event.consume();
					shutDownQuest();
			}
		}
	}

	@Subscribe
	public void onMenuEntryAdded(MenuEntryAdded event)
	{
		int widgetIndex = event.getOpcode();
		int widgetID = event.getParam1();
		MenuEntry[] menuEntries = client.getMenuEntries();
		String target = Text.removeTags(event.getTarget());

		if (Ints.contains(QUESTLIST_WIDGET_IDS, widgetID) && "Read Journal:".equals(event.getOption()))
		{
			QuestHelper questHelper = quests.get(target);
			if (questHelper != null && !questHelper.isCompleted())
			{
				menuEntries = Arrays.copyOf(menuEntries, menuEntries.length + 1);

				MenuEntry menuEntry = menuEntries[menuEntries.length - 1] = new MenuEntry();
				menuEntry.setTarget(event.getTarget());
				menuEntry.setParam0(widgetIndex);
				menuEntry.setParam1(widgetID);
//				menuEntry.seto(MenuOpcode.RUNELITE.getId());

				if (selectedQuest != null && selectedQuest.getQuest().getName().equals(target))
				{
					menuEntry.setOption(MENUOP_STOPHELPER);
				}
				else
				{
					menuEntry.setOption(MENUOP_STARTHELPER);
				}

				client.setMenuEntries(menuEntries);
			}
		}

		if (Ints.contains(QUESTTAB_WIDGET_IDS, widgetID)
			&& "Quest List".equals(event.getOption())
			&& selectedQuest != null)
		{
			menuEntries = Arrays.copyOf(menuEntries, menuEntries.length + 1);

			MenuEntry menuEntry = menuEntries[menuEntries.length - 1] = new MenuEntry();
			menuEntry.setTarget(event.getTarget());
			menuEntry.setParam0(widgetIndex);
			menuEntry.setParam1(widgetID);
//			menuEntry.setType(MenuOpcode.RUNELITE.getId());
			menuEntry.setOption(MENUOP_STOPHELPER);

			client.setMenuEntries(menuEntries);
		}
	}

	private void startUpQuest(QuestHelper questHelper)
	{
		if (!questHelper.isCompleted())
		{
			selectedQuest = questHelper;
			eventBus.subscribe(selectedQuest); //issue is they have an extra event for this we dont, so i'm out of luck or is there a way around this?
			selectedQuest.startUp();
		}
		else
		{
			selectedQuest = null;
		}
	}

	private void shutDownQuest()
	{
		if (selectedQuest != null)
		{
			selectedQuest.shutDown();
			eventBus.unregister(selectedQuest);
			selectedQuest = null;
		}
	}

	private Map<String, QuestHelper> scanAndInstantiate(ClassLoader classLoader, String packageName) throws IOException
	{
		Map<Quest, Class<? extends QuestHelper>> quests = new HashMap<>();

		Map<String, QuestHelper> scannedQuests = new HashMap<>();
		ClassPath classPath = ClassPath.from(classLoader);

		ImmutableSet<ClassPath.ClassInfo> classes = packageName == null ? classPath.getAllClasses()
			: classPath.getTopLevelClassesRecursive(packageName);
		for (ClassPath.ClassInfo classInfo : classes)
		{
			Class<?> clazz = classInfo.load();
			QuestDescriptor questDescriptor = clazz.getAnnotation(QuestDescriptor.class);

			if (questDescriptor == null)
			{
				if (clazz.getSuperclass() == QuestHelper.class)
				{
					log.warn("Class {} is a quest helper, but has no quest descriptor",
						clazz);
				}
				continue;
			}

			if (clazz.isAssignableFrom(QuestHelper.class))
			{
				log.warn("Class {} has quest descriptor, but is not a quest helper",
					clazz);
				continue;
			}

			Class<QuestHelper> questClass = (Class<QuestHelper>) clazz;
			quests.put(questDescriptor.quest(), questClass);
		}

		for (Map.Entry<Quest, Class<? extends QuestHelper>> questClazz : quests.entrySet())
		{
			QuestHelper questHelper;
			try
			{
				questHelper = instantiate((Class<QuestHelper>) questClazz.getValue(), questClazz.getKey());
			}
			catch (QuestInstantiationException ex)
			{
				log.warn("Error instantiating quest helper!", ex);
				continue;
			}

			scannedQuests.put(questClazz.getKey().getName(), questHelper);
		}

		return scannedQuests;
	}

	private QuestHelper instantiate(Class<QuestHelper> clazz, Quest quest) throws QuestInstantiationException
	{
		QuestHelper questHelper;
		try
		{
			questHelper = clazz.newInstance();
			questHelper.setQuest(quest);
		}
		catch (InstantiationException | IllegalAccessException ex)
		{
			throw new QuestInstantiationException(ex);
		}

		try
		{
			Module questModule = (Binder binder) ->
			{
				binder.bind(clazz).toInstance(questHelper);
				binder.install(questHelper);
			};
			Injector questInjector = RuneLite.getInjector().createChildInjector(questModule);
			questInjector.injectMembers(questHelper);
			questHelper.setInjector(questInjector);
		}
		catch (CreationException ex)
		{
			throw new QuestInstantiationException(ex);
		}

		log.debug("Loaded quest helper {}", clazz.getSimpleName());
		return questHelper;
	}
}
