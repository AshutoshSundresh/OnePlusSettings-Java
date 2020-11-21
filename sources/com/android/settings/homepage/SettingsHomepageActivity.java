package com.android.settings.homepage;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.android.settings.C0007R$dimen;
import com.android.settings.C0010R$id;
import com.android.settings.C0012R$layout;
import com.android.settings.C0013R$menu;
import com.android.settings.homepage.contextualcards.ContextualCardsFragment;
import com.android.settings.overlay.FeatureFactory;
import com.google.android.material.appbar.Appbar;
import com.oneplus.settings.BaseAppCompatActivity;
import com.oneplus.settings.utils.OPUtils;

public class SettingsHomepageActivity extends BaseAppCompatActivity {
    private Toolbar mToolbar;

    /* access modifiers changed from: protected */
    @Override // androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, com.oneplus.settings.BaseAppCompatActivity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(C0012R$layout.settings_homepage_container);
        findViewById(C0010R$id.settings_homepage_container);
        Appbar appbar = (Appbar) findViewById(C0010R$id.appbar);
        appbar.setTitle(getTitle());
        appbar.setDisplayHomeAsUpEnabled(false);
        Toolbar toolbar = (Toolbar) findViewById(C0010R$id.toolbar);
        this.mToolbar = toolbar;
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        OPUtils.setLightNavigationBar(getWindow(), OPUtils.getThemeMode(getContentResolver()));
        Log.d("SettingsHomepageActivity", "showFragment(new ContextualCardsFragment()");
        showFragment(new ContextualCardsFragment(), C0010R$id.contextual_cards_content);
        showFragment(new TopLevelSettings(), C0010R$id.main_content);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        this.mToolbar.getMenu().clear();
        getMenuInflater().inflate(C0013R$menu.op_search_settings, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() != C0010R$id.action_search) {
            return super.onOptionsItemSelected(menuItem);
        }
        startActivity(FeatureFactory.getFactory(this).getSearchFeatureProvider().buildSearchIntent(this, 1502));
        return true;
    }

    private void showFragment(Fragment fragment, int i) {
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        FragmentTransaction beginTransaction = supportFragmentManager.beginTransaction();
        Fragment findFragmentById = supportFragmentManager.findFragmentById(i);
        if (findFragmentById == null) {
            beginTransaction.add(i, fragment);
        } else {
            beginTransaction.show(findFragmentById);
        }
        beginTransaction.commit();
    }

    /* access modifiers changed from: package-private */
    public void setHomepageContainerPaddingTop() {
        View findViewById = findViewById(C0010R$id.homepage_container);
        findViewById.setPadding(0, getResources().getDimensionPixelSize(C0007R$dimen.search_bar_height) + (getResources().getDimensionPixelSize(C0007R$dimen.search_bar_margin) * 2), 0, 0);
        findViewById.setFocusableInTouchMode(true);
        findViewById.requestFocus();
    }
}
