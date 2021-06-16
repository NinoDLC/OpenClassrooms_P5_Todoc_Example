package fr.delcey.todoc.utils;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

public class LiveDataTestUtils {
    public static <T> T getValueForTesting(@NonNull final LiveData<T> liveData) {
        liveData.observeForever(t -> {
        });

        return liveData.getValue();
    }
}
