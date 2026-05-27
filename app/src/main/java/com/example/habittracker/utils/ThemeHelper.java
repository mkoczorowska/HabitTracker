package com.example.habittracker.utils;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;

public class ThemeHelper {


    public static final int LIGHT_BG = Color.parseColor("#F5F5F5");
    public static final int LIGHT_SURFACE = Color.parseColor("#FFFFFF");
    public static final int LIGHT_BORDER = Color.parseColor("#D0D0D0");
    public static final int LIGHT_TEXT_PRIMARY = Color.parseColor("#1A1A1A");
    public static final int LIGHT_TEXT_SECONDARY = Color.parseColor("#6B6B6B");
    public static final int LIGHT_BUTTON_BG = Color.parseColor("#3D3D3D");
    public static final int LIGHT_BUTTON_TEXT = Color.parseColor("#FFFFFF");
    public static final int LIGHT_BUTTON_OUTLINE_TEXT = Color.parseColor("#1A1A1A");


    public static final int DARK_BG = Color.parseColor("#111111");
    public static final int DARK_SURFACE = Color.parseColor("#1E1E1E");
    public static final int DARK_BORDER = Color.parseColor("#3A3A3A");
    public static final int DARK_TEXT_PRIMARY = Color.parseColor("#F0F0F0");
    public static final int DARK_TEXT_SECONDARY = Color.parseColor("#9A9A9A");
    public static final int DARK_BUTTON_BG = Color.parseColor("#E0E0E0");
    public static final int DARK_BUTTON_TEXT = Color.parseColor("#111111");
    public static final int DARK_BUTTON_OUTLINE_TEXT = Color.parseColor("#F0F0F0");

    public static void apply(Activity activity, View rootView, boolean dark) {

        activity.getWindow().getDecorView().setBackgroundColor(dark ? DARK_BG : LIGHT_BG);
        applyToView(rootView, dark);
    }

    public static void applyToView(View view, boolean dark) {
        if (view == null) return;

        int bg = dark ? DARK_BG : LIGHT_BG;
        int surface = dark ? DARK_SURFACE : LIGHT_SURFACE;
        int textPrimary = dark ? DARK_TEXT_PRIMARY : LIGHT_TEXT_PRIMARY;
        int textSecondary = dark ? DARK_TEXT_SECONDARY : LIGHT_TEXT_SECONDARY;

        String tag = view.getTag() != null ? view.getTag().toString() : "";

        if (tag.equals("root") || tag.equals("scroll_root")) {
            view.setBackgroundColor(bg);
        } else if (tag.equals("card")) {
            view.setBackgroundColor(surface);
            // rysujemy border
            android.graphics.drawable.GradientDrawable gd = new android.graphics.drawable.GradientDrawable();
            gd.setColor(surface);
            gd.setCornerRadius(dpToPx(view.getContext(), 16));
            gd.setStroke(dpToPx(view.getContext(), 1), dark ? DARK_BORDER : LIGHT_BORDER);
            view.setBackground(gd);
        } else if (tag.equals("bottom_nav")) {
            int navBg = dark ? Color.parseColor("#1E1E1E") : Color.parseColor("#FFFFFF");
            android.graphics.drawable.GradientDrawable gd = new android.graphics.drawable.GradientDrawable();
            gd.setColor(navBg);
            gd.setCornerRadii(new float[]{dpToPx(view.getContext(), 20), dpToPx(view.getContext(), 20),
                    dpToPx(view.getContext(), 20), dpToPx(view.getContext(), 20), 0, 0, 0, 0});
            gd.setStroke(dpToPx(view.getContext(), 1), dark ? DARK_BORDER : LIGHT_BORDER);
            view.setBackground(gd);
        } else if (tag.equals("btn_primary")) {
            android.graphics.drawable.GradientDrawable gd = new android.graphics.drawable.GradientDrawable();
            gd.setColor(dark ? DARK_BUTTON_BG : LIGHT_BUTTON_BG);
            gd.setCornerRadius(dpToPx(view.getContext(), 12));
            view.setBackground(gd);
            if (view instanceof Button) {
                ((Button) view).setTextColor(dark ? DARK_BUTTON_TEXT : LIGHT_BUTTON_TEXT);
            }
        } else if (tag.equals("btn_outline")) {
            android.graphics.drawable.GradientDrawable gd = new android.graphics.drawable.GradientDrawable();
            gd.setColor(Color.TRANSPARENT);
            gd.setCornerRadius(dpToPx(view.getContext(), 12));
            gd.setStroke(dpToPx(view.getContext(), 2), dark ? DARK_TEXT_PRIMARY : LIGHT_TEXT_PRIMARY);
            view.setBackground(gd);
            if (view instanceof Button) {
                ((Button) view).setTextColor(dark ? DARK_BUTTON_OUTLINE_TEXT : LIGHT_BUTTON_OUTLINE_TEXT);
            }
        } else if (tag.equals("input")) {
            android.graphics.drawable.GradientDrawable gd = new android.graphics.drawable.GradientDrawable();
            gd.setColor(dark ? Color.parseColor("#2A2A2A") : Color.parseColor("#F5F5F5"));
            gd.setCornerRadius(dpToPx(view.getContext(), 10));
            gd.setStroke(dpToPx(view.getContext(), 1), dark ? DARK_BORDER : LIGHT_BORDER);
            view.setBackground(gd);
            if (view instanceof EditText) {
                ((EditText) view).setTextColor(textPrimary);
                ((EditText) view).setHintTextColor(dark ? Color.parseColor("#666666") : Color.parseColor("#AAAAAA"));
            }
        } else if (tag.equals("divider")) {
            view.setBackgroundColor(dark ? DARK_BORDER : LIGHT_BORDER);
        }


        if (view instanceof TextView && !(view instanceof Button)) {
            String tvTag = tag;
            if (tvTag.equals("text_primary") || tvTag.isEmpty()) {
                ((TextView) view).setTextColor(textPrimary);
            } else if (tvTag.equals("text_secondary") || tvTag.equals("label")) {
                ((TextView) view).setTextColor(textSecondary);
            } else if (tvTag.equals("text_title")) {
                ((TextView) view).setTextColor(textPrimary);
            }
        }

        if (view instanceof Switch) {
            ((Switch) view).setThumbTintList(android.content.res.ColorStateList.valueOf(
                    dark ? Color.parseColor("#E0E0E0") : Color.parseColor("#5A5A5A")));
            ((Switch) view).setTrackTintList(android.content.res.ColorStateList.valueOf(
                    dark ? Color.parseColor("#444444") : Color.parseColor("#CCCCCC")));
        }


        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            for (int i = 0; i < group.getChildCount(); i++) {
                applyToView(group.getChildAt(i), dark);
            }
        }
    }

    private static int dpToPx(android.content.Context context, int dp) {
        return (int) (dp * context.getResources().getDisplayMetrics().density);
    }
}