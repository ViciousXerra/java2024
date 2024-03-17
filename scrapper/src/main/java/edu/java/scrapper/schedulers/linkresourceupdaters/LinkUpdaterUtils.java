package edu.java.scrapper.schedulers.linkresourceupdaters;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class LinkUpdaterUtils {

    public final static String RESOURCE_SLICER_REGEX =
        "^(https|git)(://|@)([^/:]+)[/:]([^/:]+)[/:]([^/:]+)([/:]([^/:]+))*$";
    public final static Pattern RESOURCE_PATTERN = Pattern.compile(RESOURCE_SLICER_REGEX);
    public final static int DOMAIN_NAME_GROUP = 3;
    public final static int GITHUB_REPO_OWNER_GROUP = 4;
    public final static int GITHUB_REPO_NAME_GROUP = 5;
    public final static int STACKOVERFLOW_QUESTION_ID_GROUP = 5;

    private LinkUpdaterUtils() {
    }

    public static LinkUpdaterUtils.Domain resolveDomain(String url) {
        Matcher matcher = RESOURCE_PATTERN.matcher(url);
        if (matcher.find()) {
            return switch (matcher.group(DOMAIN_NAME_GROUP)) {
                case "github.com" -> LinkUpdaterUtils.Domain.GITHUB;
                case "stackoverflow.com" -> LinkUpdaterUtils.Domain.STACKOVERFLOW;
                default -> throw new IllegalArgumentException("Unsupported domain: %s".formatted(matcher.group(
                    DOMAIN_NAME_GROUP)));
            };
        } else {
            throw new IllegalArgumentException("Unable to recognize URL pattern");
        }
    }

    public enum Domain {
        GITHUB,
        STACKOVERFLOW
    }

    public enum Activity {
        NO_ACTIVITY,
        NEW_UPDATE,
        GITHUB_PUSH,
        GITHUB_BRANCH_CREATION
    }

}
