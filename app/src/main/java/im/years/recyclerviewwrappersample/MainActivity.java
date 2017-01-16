package im.years.recyclerviewwrappersample;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import im.years.recyclerviewwrapper.EasyListFragment;

public class MainActivity extends AppCompatActivity {

    TabLayout tabs;
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabs = (TabLayout) findViewById(R.id.tabs);
        viewPager = (ViewPager) findViewById(R.id.pager);

        viewPager.setAdapter(new HomePagerAdapter(getSupportFragmentManager()));
        tabs.setupWithViewPager(viewPager);
    }

    class HomePagerAdapter extends FragmentPagerAdapter {

        public HomePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            switch (position) {
                case 0:
                    fragment = new TestChildOfHelperFragment();
                    break;
                case 1:
                    fragment = new TestBriefListFragment();
                    break;
                case 2:
                    fragment = new TestFragment();
                    break;
            }

            return fragment;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            String title = "";
            switch (position) {
                case 0:
                    title = "Simple";
                    break;
                case 1:
                    title = "Brief";
                    break;
                case 2:
                    title = "Easy";
                    break;
            }

            return title;
        }

        @Override
        public int getCount() {
            return 3;
        }
    }
}
