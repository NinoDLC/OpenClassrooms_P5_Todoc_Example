package fr.delcey.todoc.utils;

import android.view.View;

import androidx.annotation.IdRes;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;

import org.hamcrest.Matcher;

public class ViewActions {

    public static ViewAction clickChildViewWithId(@IdRes int viewId) {
        return new ViewAction() {

            @Override
            public Matcher<View> getConstraints() {
                return null;
            }

            @Override
            public String getDescription() {
                return "Click on a child view with specified id : " + viewId;
            }

            @Override
            public void perform(UiController uiController, View view) {
                View v = view.findViewById(viewId);

                v.performClick();
            }
        };
    }
}
