package fr.delcey.todoc.utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.ViewAssertion;
import androidx.test.espresso.matcher.ViewMatchers;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@SuppressWarnings("Convert2Lambda")
public class ViewAssertions {

    public static ViewAssertion hasDrawableRes(
        @DrawableRes int expectedDrawableId,
        @Nullable @ColorRes Integer tintColorRes
    ) {
        return matches(new TypeSafeMatcher<View>() {
            private String resourceName;

            @Override
            protected boolean matchesSafely(View target) {
                Resources resources = target.getContext().getResources();
                resourceName = resources.getResourceEntryName(expectedDrawableId);

                if (!(target instanceof ImageView)) {
                    return false;
                }

                ImageView imageView = (ImageView) target;

                if (expectedDrawableId == -1) {
                    return imageView.getDrawable() == null;
                }

                Drawable expectedDrawable = resources.getDrawable(expectedDrawableId, null);

                if (expectedDrawable == null) {
                    return false;
                }

                if (tintColorRes != null) {
                    expectedDrawable.setTint(resources.getColor(tintColorRes, null));
                }

                Bitmap bitmap = getBitmap(imageView.getDrawable());
                Bitmap otherBitmap = getBitmap(expectedDrawable);

                return bitmap.sameAs(otherBitmap);
            }

            private Bitmap getBitmap(@NonNull Drawable drawable) {
                Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                drawable.draw(canvas);
                return bitmap;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("with drawable from resource id: ");
                description.appendValue(expectedDrawableId);
                if (resourceName != null) {
                    description.appendText("[");
                    description.appendText(resourceName);
                    description.appendText("]");
                }
            }
        });
    }

    public static ViewAssertion onRecyclerViewItem(@IntRange(from = 0) int position, @IdRes int viewId, @NonNull Matcher<View> matcher) {
        return new ViewAssertion() {
            @Override
            public void check(View view, @Nullable NoMatchingViewException noViewFoundException) {
                if (noViewFoundException != null) {
                    throw noViewFoundException;
                }

                if (!(view instanceof RecyclerView)) {
                    throw new IllegalStateException("The asserted view is not RecyclerView");
                }

                RecyclerView recyclerView = (RecyclerView) view;
                RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(position);

                if (viewHolder == null) {
                    String additionalInfo;

                    if (recyclerView.getAdapter() == null) {
                        additionalInfo = ", because no adapter is set for the RecyclerView";
                    } else {
                        additionalInfo = ", but there is " + recyclerView.getAdapter().getItemCount() + " items in adapter";
                    }

                    throw new IllegalStateException(
                        "No ViewHolder found for adapter position : " + position + additionalInfo
                    );
                }

                View childView = viewHolder.itemView.findViewById(viewId);

                if (childView == null) {
                    throw new IllegalStateException("No view found with id : " + recyclerView.getResources().getResourceEntryName(viewId));
                }

                ViewMatchers.assertThat(childView, matcher);
            }
        };
    }

    public static ViewAssertion hasRecyclerViewItemCount(int itemCount) {
        return new ViewAssertion() {

            @Override
            public void check(View view, NoMatchingViewException noViewFoundException) {
                if (noViewFoundException != null) {
                    throw noViewFoundException;
                }

                RecyclerView recyclerView = (RecyclerView) view;
                if (recyclerView.getAdapter() != null) {
                    assertThat(recyclerView.getAdapter().getItemCount(), is(itemCount));
                }
            }
        };
    }
}
