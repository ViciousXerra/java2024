package edu.java.scrapper.schedulers.linkresourceupdaters;

import java.util.regex.Pattern;

public final class LinkUpdaterUtils {

    //^(https|git)(://|@)([^/:]+)[/:]([^/:]+)[/:]([^/:]+)([/:]([^/:]+))*/*$
    public final static String RESOURCE_SLICER_REGEX =
        "^(https|git)(://|@)([^/:]+)[/:]([^/:]+)[/:]([^/:]+)([/:]([^/:]+))*/*$";
    public final static Pattern RESOURCE_PATTERN = Pattern.compile(RESOURCE_SLICER_REGEX);
    public final static int DOMAIN_NAME_GROUP = 3;
    public final static int GITHUB_REPO_OWNER_GROUP = 4;
    public final static int GITHUB_REPO_NAME_GROUP = 5;
    public final static int STACKOVERFLOW_QUESTION_ID_GROUP = 5;

    private LinkUpdaterUtils() {
    }

    public enum Activity {
        NO_ACTIVITY,
        NEW_UPDATE
    }

}
