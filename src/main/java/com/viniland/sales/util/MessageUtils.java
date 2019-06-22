package com.viniland.sales.util;

import lombok.experimental.UtilityClass;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Message utilities
 */
@UtilityClass
public class MessageUtils {

    /**
     * Get message from bundle
     *
     * @param path
     *      Message bundle path
     * @param key
     *      Message key
     * @return
     */
    public static String getMessage(String path, String key) {
        ResourceBundle bundle = ResourceBundle.getBundle(path, Locale.ROOT);
        if (bundle != null && bundle.containsKey(key)) {
            return bundle.getString(key);
        } else {
            return key;
        }
    }

}
