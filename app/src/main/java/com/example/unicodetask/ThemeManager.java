package com.example.unicodetask;

import android.content.Context;

public class ThemeManager
{
    static enum THEMES {Dark, Light};
    private static THEMES currentTheme = THEMES.Dark; //The currently enabled theme

    static void setTheme(THEMES newTheme)
    {
        currentTheme = newTheme;
    }

    static void setActivityTheme(Context activity)
    {
        switch(currentTheme)
        {
            case Dark : activity.setTheme(R.style.AppThemeDark); break;
            case Light : activity.setTheme(R.style.AppThemeLight); break;
        }
    }

}
