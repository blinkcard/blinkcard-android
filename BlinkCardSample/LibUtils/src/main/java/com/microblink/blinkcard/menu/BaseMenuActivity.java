package com.microblink.blinkcard.menu;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.microblink.blinkcard.libutils.R;

import java.util.List;

public abstract class BaseMenuActivity extends AppCompatActivity {

    private List<MenuListItem> mListItems;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(getTitleText());

        mListItems = createMenuListItems();
        final ListView lv = findViewById(R.id.detectorList);
        ArrayAdapter<MenuListItem> listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mListItems);
        lv.setAdapter(listAdapter);
        lv.setOnItemClickListener((parent, view, position, id) -> mListItems.get(position - lv.getHeaderViewsCount()).getOnClickAction().run());
        if (this.findViewById(R.id.appBarLayout) != null) {
            ViewCompat.setOnApplyWindowInsetsListener(this.findViewById(R.id.appBarLayout), (v, windowInsets) -> {
                Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.statusBars() | WindowInsetsCompat.Type.displayCutout());
                ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
                mlp.topMargin = insets.top;
                v.setLayoutParams(mlp);
                return WindowInsetsCompat.CONSUMED;
            });
        }
    }

    protected abstract List<MenuListItem> createMenuListItems();

    protected abstract String getTitleText();

}
