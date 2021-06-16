package fr.delcey.todoc.ui;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isPlatformPopup;
import static androidx.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withChild;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.isA;
import static fr.delcey.todoc.utils.ViewAssertions.hasDrawableRes;
import static fr.delcey.todoc.utils.ViewAssertions.hasRecyclerViewItemCount;
import static fr.delcey.todoc.utils.ViewAssertions.onRecyclerViewItem;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import fr.delcey.todoc.R;
import fr.delcey.todoc.ui.add.AddTaskViewStateItem;
import fr.delcey.todoc.utils.ClickChildViewWithId;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    private static final String FIRST_TASK_DESCRIPTION = "FIRST_TASK_DESCRIPTION";
    private static final String SECOND_TASK_DESCRIPTION = "SECOND_TASK_DESCRIPTION";
    private static final String THIRD_TASK_DESCRIPTION = "THIRD_TASK_DESCRIPTION";

    @Before
    public void setUp() {
        ActivityScenario.launch(MainActivity.class);
    }

    @Test
    public void add_3_tasks_then_sort_then_remove_one_then_sort_then_remove() throws InterruptedException {
        assertIsDisplayingEmptyState();

        addTask(Project.LUCIDIA, FIRST_TASK_DESCRIPTION);
        addTask(Project.CIRCUS, SECOND_TASK_DESCRIPTION);
        addTask(Project.TARTAMPION, THIRD_TASK_DESCRIPTION);

        // Assert 3 tasks are present
        onView(withId(R.id.task_fragment_recycler_view))
            .check(hasRecyclerViewItemCount(3))
            .check(
                onRecyclerViewItem(
                    0,
                    R.id.task_item_text_view_description,
                    withText(FIRST_TASK_DESCRIPTION)
                )
            )
            .check(
                onRecyclerViewItem(
                    1,
                    R.id.task_item_text_view_description,
                    withText(SECOND_TASK_DESCRIPTION)
                )
            )
            .check(
                onRecyclerViewItem(
                    2,
                    R.id.task_item_text_view_description,
                    withText(THIRD_TASK_DESCRIPTION)
                )
            );

        // Sort by project
        onView(withId(R.id.sort_task))
            .perform(click());

        // Assert 3 headers & 3 tasks are present
        onView(withId(R.id.task_fragment_recycler_view))
            .check(hasRecyclerViewItemCount(6))
            .check(
                onRecyclerViewItem(
                    0,
                    R.id.task_header_item_text_view_title,
                    withText(Project.TARTAMPION.nameStringRes)
                )
            )
            .check(
                onRecyclerViewItem(
                    1,
                    R.id.task_item_text_view_description,
                    withText(THIRD_TASK_DESCRIPTION)
                )
            )
            .check(
                onRecyclerViewItem(
                    2,
                    R.id.task_header_item_text_view_title,
                    withText(Project.LUCIDIA.nameStringRes)
                )
            )
            .check(
                onRecyclerViewItem(
                    3,
                    R.id.task_item_text_view_description,
                    withText(FIRST_TASK_DESCRIPTION)
                )
            )
            .check(
                onRecyclerViewItem(
                    4,
                    R.id.task_header_item_text_view_title,
                    withText(Project.CIRCUS.nameStringRes)
                )
            )
            .check(
                onRecyclerViewItem(
                    5,
                    R.id.task_item_text_view_description,
                    withText(SECOND_TASK_DESCRIPTION)
                )
            );

        // Delete 1 task (Lucidia)
        onView(withId(R.id.task_fragment_recycler_view))
            .perform(RecyclerViewActions.actionOnItemAtPosition(3, new ClickChildViewWithId(R.id.task_item_image_view_delete)));

        Thread.sleep(1_000);

        // Assert 2 headers & 2 tasks are present
        onView(withId(R.id.task_fragment_recycler_view))
            .check(hasRecyclerViewItemCount(4))
            .check(
                onRecyclerViewItem(
                    0,
                    R.id.task_header_item_text_view_title,
                    withText(Project.TARTAMPION.nameStringRes)
                )
            )
            .check(
                onRecyclerViewItem(
                    1,
                    R.id.task_item_text_view_description,
                    withText(THIRD_TASK_DESCRIPTION)
                )
            )
            .check(
                onRecyclerViewItem(
                    2,
                    R.id.task_header_item_text_view_title,
                    withText(Project.CIRCUS.nameStringRes)
                )
            )
            .check(
                onRecyclerViewItem(
                    3,
                    R.id.task_item_text_view_description,
                    withText(SECOND_TASK_DESCRIPTION)
                )
            );

        // Sort by task (chronological)
        onView(withId(R.id.sort_task))
            .perform(click());

        // Assert 2 tasks are present
        onView(withId(R.id.task_fragment_recycler_view))
            .check(hasRecyclerViewItemCount(2))
            .check(
                onRecyclerViewItem(
                    0,
                    R.id.task_item_text_view_description,
                    withText(SECOND_TASK_DESCRIPTION)
                )
            )
            .check(
                onRecyclerViewItem(
                    1,
                    R.id.task_item_text_view_description,
                    withText(THIRD_TASK_DESCRIPTION)
                )
            );

        // Delete 1 task (Circus)
        onView(withId(R.id.task_fragment_recycler_view))
            .perform(RecyclerViewActions.actionOnItemAtPosition(0, new ClickChildViewWithId(R.id.task_item_image_view_delete)));

        Thread.sleep(1_000);

        // Assert 1 task is present
        onView(withId(R.id.task_fragment_recycler_view))
            .check(hasRecyclerViewItemCount(1))
            .check(
                onRecyclerViewItem(
                    0,
                    R.id.task_item_text_view_description,
                    withText(THIRD_TASK_DESCRIPTION)
                )
            );

        // Sort by project
        onView(withId(R.id.sort_task))
            .perform(click());

        // Assert 1 header & 1 task are present
        onView(withId(R.id.task_fragment_recycler_view))
            .check(hasRecyclerViewItemCount(2))
            .check(
                onRecyclerViewItem(
                    0,
                    R.id.task_header_item_text_view_title,
                    withText(Project.TARTAMPION.nameStringRes)
                )
            )
            .check(
                onRecyclerViewItem(
                    1,
                    R.id.task_item_text_view_description,
                    withText(THIRD_TASK_DESCRIPTION)
                )
            );

        // Delete 1 task (Tartampion)
        onView(withId(R.id.task_fragment_recycler_view))
            .perform(RecyclerViewActions.actionOnItemAtPosition(1, new ClickChildViewWithId(R.id.task_item_image_view_delete)));

        Thread.sleep(1_000);

        // Back to empty state
        assertIsDisplayingEmptyState();

        // Sort by task (chronological)
        onView(withId(R.id.sort_task))
            .perform(click());

        // Still on empty state
        assertIsDisplayingEmptyState();
    }

    private void assertIsDisplayingEmptyState() {
        onView(
            allOf(
                withId(R.id.task_empty_state_item_text_view),
                isCompletelyDisplayed()
            )
        ).check(matches(withText(R.string.empty_tasks)));
        onView(
            allOf(
                withId(R.id.task_empty_state_item_image_view_check),
                isCompletelyDisplayed()
            )
        ).check(hasDrawableRes(R.drawable.ic_check_96, R.color.colorOnBackground));
        onView(
            allOf(
                withId(R.id.task_fragment_recycler_view),
                isCompletelyDisplayed()
            )
        ).check(hasRecyclerViewItemCount(1));
    }

    private void addTask(@NonNull Project project, @NonNull String taskDescription) throws InterruptedException {
        onView(withId(R.id.task_fragment_fab_add_task))
            .check(matches(withContentDescription(R.string.a11y_add_new_task)))
            .perform(click());

        onView(withId(R.id.create_task_auto_complete_text_view_projects)).perform(click());

        onData(isA(AddTaskViewStateItem.class))
            .inRoot(isPlatformPopup())
            .atPosition(project.spinnerIndex)
            .check(matches(withChild(withText(project.nameStringRes))))
            .perform(
                scrollTo(),
                click()
            );

        onView(withId(R.id.create_task_edit_text_description)).perform(replaceText(taskDescription));

        Espresso.closeSoftKeyboard();

        onView(withId(R.id.create_task_button_ok)).perform(click());

        Thread.sleep(2_000);
    }

    private enum Project {
        TARTAMPION(0, R.string.tartampion_project),
        LUCIDIA(1, R.string.lucidia_project),
        CIRCUS(2, R.string.circus_project);

        private final int spinnerIndex;
        private final int nameStringRes;

        Project(int spinnerIndex, @StringRes int nameStringRes) {
            this.spinnerIndex = spinnerIndex;
            this.nameStringRes = nameStringRes;
        }
    }
}