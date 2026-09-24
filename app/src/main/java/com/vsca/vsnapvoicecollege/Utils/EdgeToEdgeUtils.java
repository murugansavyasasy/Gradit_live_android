package com.vsca.vsnapvoicecollege.Utils;


import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;

import androidx.activity.ComponentActivity;
import androidx.activity.EdgeToEdge;
import androidx.activity.SystemBarStyle;
import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.vsca.vsnapvoicecollege.R;

public class EdgeToEdgeUtils {

    @ColorInt
    public static int getPriorityColor(android.content.Context context, @Nullable String priority) {
        int colorRes;
        if ("p1".equals(priority)) {
            colorRes = R.color.clr_principal;
        } else if ("p2".equals(priority) || "p3".equals(priority) || "p6".equals(priority)) {
            colorRes = R.color.clr_teachingstaff;
        } else if ("p4".equals(priority)) {
            colorRes = R.color.clr_receiver;
        } else if ("p5".equals(priority)) {
            colorRes = R.color.clr_parent;
        } else if ("p7".equals(priority)) {
            colorRes = R.color.cle_lightorang;
        } else {
            colorRes = R.color.black;
        }
        return ContextCompat.getColor(context, colorRes);
    }

    /** Overload that takes a priority string and resolves the colour itself. */
    public static void setupEdgeToEdge(
            ComponentActivity activity,
            @Nullable View rootView,
            @Nullable View statusBarBgView,
            @Nullable String priority,
            @Nullable Boolean lightIcons
    ) {
        int color = getPriorityColor(activity, priority);
        setupEdgeToEdge(activity, rootView, statusBarBgView, color, lightIcons);
    }

    /** Main function. Call after setContentView(). Screens WITHOUT an ActionBar. */
    public static void setupEdgeToEdge(
            ComponentActivity activity,
            @Nullable View rootView,
            @Nullable View statusBarBgView,
            @ColorInt int statusBarColor,
            @Nullable Boolean lightIcons   // true = dark icons, false = white icons, null = auto from colour
    ) {
        if (rootView == null || statusBarBgView == null) return;

        EdgeToEdge.enable(
                activity,
                SystemBarStyle.dark(Color.TRANSPARENT),
                SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
        );

        statusBarBgView.setBackgroundColor(statusBarColor);

        boolean useLightIcons = (lightIcons != null)
                ? lightIcons
                : ColorUtils.calculateLuminance(statusBarColor) > 0.5;

        Window window = activity.getWindow();
        WindowCompat.getInsetsController(window, window.getDecorView())
                .setAppearanceLightStatusBars(useLightIcons);

        ViewCompat.setOnApplyWindowInsetsListener(rootView, (v, insets) -> {
            Insets bars = insets.getInsets(
                    WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.displayCutout()
            );
            Insets ime = insets.getInsets(WindowInsetsCompat.Type.ime());

            v.setPadding(
                    bars.left,
                    v.getPaddingTop(),
                    bars.right,
                    Math.max(bars.bottom, ime.bottom)
            );

            ViewGroup.LayoutParams lp = statusBarBgView.getLayoutParams();
            lp.height = bars.top;
            statusBarBgView.setLayoutParams(lp);

            return WindowInsetsCompat.CONSUMED;
        });
    }

    /** Overload that takes a priority string and resolves the colour itself. Screens WITH an ActionBar. */
    public static void setupEdgeToEdgeWithActionBar(
            ComponentActivity activity,
            @Nullable View rootView,
            @Nullable String priority,
            @Nullable Boolean lightIcons
    ) {
        int color = getPriorityColor(activity, priority);
        setupEdgeToEdgeWithActionBar(activity, rootView, color, lightIcons);
    }

    /** Call after setContentView() + your ActionBar setup call. Screens WITH an ActionBar. */
    public static void setupEdgeToEdgeWithActionBar(
            ComponentActivity activity,
            @Nullable View rootView,
            @ColorInt int barColor,
            @Nullable Boolean lightIcons   // true = dark icons, false = white icons, null = auto from colour
    ) {
        Window window = activity.getWindow();

        EdgeToEdge.enable(
                activity,
                SystemBarStyle.dark(Color.TRANSPARENT),
                SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
        );

        // The strip behind the status bar is the window background
        window.setBackgroundDrawable(new ColorDrawable(barColor));

        boolean useLightIcons = (lightIcons != null)
                ? lightIcons
                : ColorUtils.calculateLuminance(barColor) > 0.5;

        WindowCompat.getInsetsController(window, window.getDecorView())
                .setAppearanceLightStatusBars(useLightIcons);

        // The ActionBar container handles the top inset, so only left/right/bottom here
        if (rootView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(rootView, (v, insets) -> {
                Insets bars = insets.getInsets(
                        WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.displayCutout()
                );
                Insets ime = insets.getInsets(WindowInsetsCompat.Type.ime());
                v.setPadding(bars.left, v.getPaddingTop(), bars.right, Math.max(bars.bottom, ime.bottom));
                return WindowInsetsCompat.CONSUMED;
            });
        }
    }

    /** Pushes contentView down if the ActionBar overlaps it. Adds nothing when there's no overlap. */
    public static void fixActionBarOverlap(AppCompatActivity activity, @Nullable View contentView) {
        if (contentView == null) return;

        ViewTreeObserver.OnGlobalLayoutListener listener = new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                View actionBarContainer = activity.getWindow().getDecorView()
                        .findViewById(androidx.appcompat.R.id.action_bar_container);
                if (actionBarContainer == null || actionBarContainer.getVisibility() != View.VISIBLE) {
                    return;
                }

                int[] abLoc = new int[2];
                int[] contentLoc = new int[2];
                actionBarContainer.getLocationOnScreen(abLoc);
                contentView.getLocationOnScreen(contentLoc);

                int actionBarBottom = abLoc[1] + actionBarContainer.getHeight();
                int overlap = actionBarBottom - contentLoc[1];

                ViewGroup.LayoutParams lpRaw = contentView.getLayoutParams();
                if (!(lpRaw instanceof ViewGroup.MarginLayoutParams)) return;
                ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) lpRaw;

                if (overlap > 0 && actionBarContainer.getHeight() > 0) {
                    lp.topMargin += overlap;
                    contentView.setLayoutParams(lp);
                }
            }
        };
        contentView.getViewTreeObserver().addOnGlobalLayoutListener(listener);
    }
}