package com.example.habittracker.utils;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

public class ThemeHelper {

    // ── Light theme ──────────────────────────────────────────────
    public static final int LIGHT_BG              = Color.parseColor("#F7F8FC");
    public static final int LIGHT_SURFACE         = Color.parseColor("#FFFFFF");
    public static final int LIGHT_BORDER          = Color.parseColor("#E2E5F0");
    public static final int LIGHT_TEXT_PRIMARY    = Color.parseColor("#0F1128");
    public static final int LIGHT_TEXT_SECONDARY  = Color.parseColor("#6B7196");
    // Przycisk primary w light: gradient indigo → biały tekst ✓
    public static final int LIGHT_BUTTON_BG       = Color.parseColor("#5C6BC0");
    public static final int LIGHT_BUTTON_TEXT     = Color.parseColor("#FFFFFF");
    public static final int LIGHT_BUTTON_OUTLINE_TEXT = Color.parseColor("#0F1128");

    // ── Dark theme ───────────────────────────────────────────────
    public static final int DARK_BG               = Color.parseColor("#0D0E1A");
    public static final int DARK_SURFACE          = Color.parseColor("#161828");
    public static final int DARK_BORDER           = Color.parseColor("#2A2D4A");
    public static final int DARK_TEXT_PRIMARY     = Color.parseColor("#EDF0FF");
    public static final int DARK_TEXT_SECONDARY   = Color.parseColor("#8C91B8");
    // NAPRAWIONE: przycisk primary w dark → jaśniejszy indigo, BIAŁY tekst
    public static final int DARK_BUTTON_BG        = Color.parseColor("#7986CB");
    public static final int DARK_BUTTON_TEXT      = Color.parseColor("#FFFFFF");
    public static final int DARK_BUTTON_OUTLINE_TEXT = Color.parseColor("#EDF0FF");

    public static void apply(Activity activity, View rootView, boolean dark) {
        applyToView(rootView, dark);
    }

    public static void applyToView(View view, boolean dark) {
        if (view == null) return;

        int bg            = dark ? DARK_BG            : LIGHT_BG;
        int surface       = dark ? DARK_SURFACE       : LIGHT_SURFACE;
        int textPrimary   = dark ? DARK_TEXT_PRIMARY   : LIGHT_TEXT_PRIMARY;
        int textSecondary = dark ? DARK_TEXT_SECONDARY : LIGHT_TEXT_SECONDARY;

        String tag = view.getTag() != null ? view.getTag().toString() : "";

        switch (tag) {
            case "root":
            case "scroll_root":
                view.setBackgroundColor(bg);
                break;

            case "card": {
                android.graphics.drawable.GradientDrawable gd = new android.graphics.drawable.GradientDrawable();
                gd.setColor(surface);
                gd.setCornerRadius(dpToPx(view.getContext(), 16));
                gd.setStroke(dpToPx(view.getContext(), 1), dark ? DARK_BORDER : LIGHT_BORDER);
                view.setBackground(gd);
                break;
            }

            case "bottom_nav": {
                int navBg = dark ? Color.parseColor("#161828") : Color.parseColor("#FFFFFF");
                android.graphics.drawable.GradientDrawable gd = new android.graphics.drawable.GradientDrawable();
                gd.setColor(navBg);
                float r = dpToPx(view.getContext(), 20);
                gd.setCornerRadii(new float[]{r, r, r, r, 0, 0, 0, 0});
                gd.setStroke(dpToPx(view.getContext(), 1), dark ? DARK_BORDER : LIGHT_BORDER);
                view.setBackground(gd);
                break;
            }

            case "btn_primary": {
                // NAPRAWIONE: indigo w light, jaśniejszy indigo w dark — oba z białym tekstem
                int btnBg   = dark ? DARK_BUTTON_BG   : LIGHT_BUTTON_BG;
                int btnText = dark ? DARK_BUTTON_TEXT  : LIGHT_BUTTON_TEXT;
                android.graphics.drawable.GradientDrawable gd = new android.graphics.drawable.GradientDrawable();
                gd.setColor(btnBg);
                gd.setCornerRadius(dpToPx(view.getContext(), 14));
                view.setBackground(gd);
                if (view instanceof Button) {
                    ((Button) view).setTextColor(btnText);
                }
                break;
            }

            case "btn_outline": {
                int outlineColor = dark ? DARK_TEXT_PRIMARY : LIGHT_TEXT_PRIMARY;
                int outlineText  = dark ? DARK_BUTTON_OUTLINE_TEXT : LIGHT_BUTTON_OUTLINE_TEXT;
                android.graphics.drawable.GradientDrawable gd = new android.graphics.drawable.GradientDrawable();
                gd.setColor(Color.TRANSPARENT);
                gd.setCornerRadius(dpToPx(view.getContext(), 14));
                gd.setStroke(dpToPx(view.getContext(), 2), outlineColor);
                view.setBackground(gd);
                if (view instanceof Button) {
                    ((Button) view).setTextColor(outlineText);
                }
                break;
            }

            case "btn_delete": {
                // Czerwony outline button — zawsze czytelny
                android.graphics.drawable.GradientDrawable gd = new android.graphics.drawable.GradientDrawable();
                gd.setColor(dark ? Color.parseColor("#2A0A0A") : Color.parseColor("#FFEBEE"));
                gd.setCornerRadius(dpToPx(view.getContext(), 14));
                gd.setStroke(dpToPx(view.getContext(), 2), Color.parseColor("#E53935"));
                view.setBackground(gd);
                if (view instanceof Button) {
                    ((Button) view).setTextColor(Color.parseColor("#E53935"));
                }
                break;
            }

            case "input": {
                int inputBg = dark ? Color.parseColor("#1E2035") : Color.parseColor("#F7F8FC");
                android.graphics.drawable.GradientDrawable gd = new android.graphics.drawable.GradientDrawable();
                gd.setColor(inputBg);
                gd.setCornerRadius(dpToPx(view.getContext(), 12));
                gd.setStroke(dpToPx(view.getContext(), 1), dark ? DARK_BORDER : LIGHT_BORDER);
                view.setBackground(gd);
                if (view instanceof EditText) {
                    ((EditText) view).setTextColor(textPrimary);
                    ((EditText) view).setHintTextColor(dark
                            ? Color.parseColor("#4A4E6A")
                            : Color.parseColor("#AAAAAA"));
                }
                break;
            }

            case "header":
                // Gradient header — nie nadpisujemy tła, ma bg_header_gradient
                break;

            case "divider":
                view.setBackgroundColor(dark ? DARK_BORDER : LIGHT_BORDER);
                break;
        }

        // Tekst (nie Button — Buttony obsługiwane przez tag btn_*)
        if (view instanceof TextView && !(view instanceof Button)) {
            if (tag.equals("text_primary") || tag.isEmpty()) {
                ((TextView) view).setTextColor(textPrimary);
            } else if (tag.equals("text_secondary") || tag.equals("label")) {
                ((TextView) view).setTextColor(textSecondary);
            } else if (tag.equals("text_title")) {
                ((TextView) view).setTextColor(textPrimary);
            }
            // tag "header" i inne specjalne — nie nadpisujemy koloru
        }

        // Switch
        if (view instanceof Switch) {
            int thumbColor = dark ? Color.parseColor("#7986CB") : Color.parseColor("#5C6BC0");
            int trackColor = dark ? Color.parseColor("#2A2D4A") : Color.parseColor("#C5CAE9");
            ((Switch) view).setThumbTintList(android.content.res.ColorStateList.valueOf(thumbColor));
            ((Switch) view).setTrackTintList(android.content.res.ColorStateList.valueOf(trackColor));
        }

        // Rekurencja na dzieci
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