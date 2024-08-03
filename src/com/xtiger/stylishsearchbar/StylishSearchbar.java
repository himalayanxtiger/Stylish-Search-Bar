package com.xtiger.stylishsearchbar;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.appinventor.components.annotations.DesignerProperty;
import com.google.appinventor.components.annotations.PropertyCategory;
import com.google.appinventor.components.annotations.SimpleEvent;
import com.google.appinventor.components.annotations.SimpleFunction;
import com.google.appinventor.components.annotations.SimpleProperty;
import com.google.appinventor.components.common.PropertyTypeConstants;
import com.google.appinventor.components.runtime.AndroidNonvisibleComponent;
import com.google.appinventor.components.runtime.AndroidViewComponent;
import com.google.appinventor.components.runtime.Component;
import com.google.appinventor.components.runtime.ComponentContainer;
import com.google.appinventor.components.runtime.EventDispatcher;
import com.google.appinventor.components.runtime.util.YailList;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class StylishSearchbar extends AndroidNonvisibleComponent implements Component {

    private Context context;
    private EditText searchBar;
    private ImageView searchIcon;
    private List<String> originalList = new ArrayList<>();
    private Typeface customTypeface;
    private int searchBarTextColor = Color.BLACK;
    private int searchBarHintColor = Color.GRAY;
    private int searchBarBackgroundColor = Color.parseColor("#F0F0F0");

    public StylishSearchbar(ComponentContainer container) {
        super(container.$form());
        context = container.$context();
    }

    @SimpleFunction(description = "Initialize the stylish searchbar in a HorizontalArrangement")
    public void Initialize(AndroidViewComponent arrangement) {
        if (!(arrangement.getView() instanceof ViewGroup)) {
            return;
        }
        ViewGroup parentView = (ViewGroup) arrangement.getView();

        // Create a LinearLayout to hold the search bar and icon
        LinearLayout searchLayout = new LinearLayout(context);
        searchLayout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(16, 16, 16, 16);
        searchLayout.setLayoutParams(layoutParams);

        // Create and style the search bar
        searchBar = new EditText(context);
        LinearLayout.LayoutParams searchBarParams = new LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1f
        );
        searchBar.setLayoutParams(searchBarParams);
        searchBar.setHint("Search...");
        searchBar.setBackgroundColor(Color.TRANSPARENT);
        searchBar.setPadding(32, 16, 32, 16);
        searchBar.setTextColor(searchBarTextColor);
        searchBar.setHintTextColor(searchBarHintColor);
        if (customTypeface != null) {
            searchBar.setTypeface(customTypeface);
        }

        // Create a drawable for the search layout background
        GradientDrawable searchBackground = new GradientDrawable();
        searchBackground.setShape(GradientDrawable.RECTANGLE);
        searchBackground.setColor(searchBarBackgroundColor);
        searchBackground.setCornerRadius(60);
        searchLayout.setBackground(searchBackground);

        // Add search icon
        searchIcon = new ImageView(context);
        searchIcon.setImageResource(android.R.drawable.ic_menu_search);
        LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        iconParams.gravity = Gravity.CENTER_VERTICAL;
        iconParams.setMargins(0, 0, 16, 0);
        searchIcon.setLayoutParams(iconParams);

        // Add views to layout
        searchLayout.addView(searchBar);
        searchLayout.addView(searchIcon);
        parentView.addView(searchLayout);

        // Add TextWatcher to the search bar
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                filterList(s.toString());
            }
        });
    }

    @SimpleFunction(description = "Set the list of items to be searched")
    public void SetItems(YailList items) {
        originalList.clear();
        for (Object item : items.toArray()) {
            if (item instanceof String) {
                originalList.add((String) item);
            }
        }
    }

    private void filterList(String text) {
        List<String> filteredList = new ArrayList<>();
        for (String item : originalList) {
            if (item.toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }
        FilteredResults(YailList.makeList(filteredList));
    }

    @SimpleEvent(description = "Event raised when the search results are filtered")
    public void FilteredResults(YailList results) {
        EventDispatcher.dispatchEvent(this, "FilteredResults", results);
    }

    @SimpleProperty(description = "Set the custom font for the search bar")
    @DesignerProperty(editorType = PropertyTypeConstants.PROPERTY_TYPE_ASSET, defaultValue = "")
    public void FontTypeFace(String typefacePath) {
        try {
            if (form instanceof com.google.appinventor.components.runtime.ReplForm) {
                String packageName = form.getPackageName();
                typefacePath = android.os.Build.VERSION.SDK_INT > 28
                        ? "/storage/emulated/0/Android/data/" + packageName + "/files/assets/" + typefacePath
                        : "/storage/emulated/0/AppInventor/assets/" + typefacePath;
                customTypeface = Typeface.createFromFile(new File(typefacePath));
            } else {
                customTypeface = Typeface.createFromAsset(form.getAssets(), typefacePath);
            }
            if (searchBar != null) {
                searchBar.setTypeface(customTypeface);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SimpleProperty(description = "Set the search bar text color")
    @DesignerProperty(editorType = PropertyTypeConstants.PROPERTY_TYPE_COLOR, defaultValue = Component.DEFAULT_VALUE_COLOR_BLACK)
    public void SearchBarTextColor(int color) {
        searchBarTextColor = color;
        if (searchBar != null) {
            searchBar.setTextColor(color);
        }
    }

    @SimpleProperty(description = "Set the search bar hint color")
    @DesignerProperty(editorType = PropertyTypeConstants.PROPERTY_TYPE_COLOR, defaultValue = Component.DEFAULT_VALUE_COLOR_GRAY)
    public void SearchBarHintColor(int color) {
        searchBarHintColor = color;
        if (searchBar != null) {
            searchBar.setHintTextColor(color);
        }
    }

    @SimpleProperty(description = "Set the search bar background color")
    public void SearchBarBackgroundColor(int color) {
        searchBarBackgroundColor = color;
        if (searchBar != null && searchBar.getParent() instanceof View) {
            View parentView = (View) searchBar.getParent();
            GradientDrawable background = (GradientDrawable) parentView.getBackground();
            background.setColor(color);
        }
    }

    @SimpleProperty(description = "Set the search icon visibility")
    @DesignerProperty(editorType = PropertyTypeConstants.PROPERTY_TYPE_BOOLEAN, defaultValue = "True")
    public void SearchIconVisible(boolean visible) {
        if (searchIcon != null) {
            searchIcon.setVisibility(visible ? View.VISIBLE : View.GONE);
        }
    }
}