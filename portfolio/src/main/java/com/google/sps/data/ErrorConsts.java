package com.google.sps.data;

// Stores error messages as constants.
// Prevents errors due to typos when writing a property name string.
public class ErrorConsts {
    public static String EMPTY_VIDEO_ID = "Video id cannot be empty.";
    public static String VIDEO_NOT_FOUND_IN_DB = "Video not found in database.";
    public static String UNABLE_TO_RETRIEVE = "Unable to retrieve video.";
    public static String NO_VIDEO_QUERY_MATCHES = "No such video found.";
    public static String SECURITY_EXCEPTION = "A security exception occurred.";
    public static String EMPTY_ANALYSIS_JSON = "The database does not contain analysis for this video.";
}