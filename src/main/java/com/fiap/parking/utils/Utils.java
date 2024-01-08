package com.fiap.parking.utils;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;

public class Utils {

    final static ResourceBundleMessageSource source = new ResourceBundleMessageSource();

    static {
        source.setDefaultEncoding("UTF-8");
        source.addBasenames("messages");
    }

    public static String getMessage(final String message) {
        return source.getMessage(message, null, LocaleContextHolder.getLocale());
    }
}
