package fr.delcey.todoc.data;

import fr.delcey.todoc.BuildConfig;

public class BuildConfigResolver {

    public boolean isDebug() {
        return BuildConfig.DEBUG;
    }

}
