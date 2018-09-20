CircularViewPager
=================
A Circular ViewPager for Android

CircularViewPager actively supports android versions 4.0.3 (Ice Cream Sandwich) and above.
That said, it works all the way down to 4.0.3 but is not actively tested or working perfectly.
- [x] Supports compileSdkVersion 28.0.2
- [x] Supports Gradle 4.4

Here is a short gif showing the functionality you get with this library:

![Demo Gif](https://github.com/sanyuzhang/CircularViewPager/raw/master/Sample/demo/demo.gif)


Goal
----
The goal of this project is to deliver a high performance circular viewpager.


Installing
---------------
**Cloning**
First of all you will have to clone the library.
```shell
git clone git@github.com:sanyuzhang/CircularViewPager.git
```

Now that you have the library you will have to import it into Android Studio.
In Android Studio navigate the menus like this.
```
File -> New -> Import Module -> CloneLocation/CircularViewPager/CircularViewPager
```
Remember to add this to the build.gradle configuration of your app
```
dependencies {
    ...
    compile project(':CircularViewPager')
}
```

In the following dialog navigate to CircularViewPager which you cloned to your computer in the previous steps and select the `build.gradle`.

**Maven**
Upcoming...

**Gradle**
Upcoming...

Getting Started
---------------
**Base usage**

Ok lets start with your activities or fragments xml file. It might look something like this.
```xml
<com.sanyuzhang.circular.viewpager.cvp.view.CircularTabLayout
    android:id="@+id/circular_tab"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:background="?attr/colorPrimary"
    app:scrollMillisecondsPerInch="70.0"
    app:tabIndicatorColor="@color/circular_tab_indicator_color"
    app:tabIndicatorHeight="@dimen/circular_tab_indicator_height"
    app:tabMarginEnd="@dimen/circular_tab_margin"
    app:tabMarginStart="@dimen/circular_tab_margin"
    app:tabSelectedTextColor="@color/circular_tab_selected_color"
    app:tabTextAppearance="@style/CircularTabTextAppearance"/>

<com.sanyuzhang.circular.viewpager.cvp.view.CircularViewPager
    android:id="@+id/circular_viewpager"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="?attr/colorPrimary"/>
```

Now in your activities `onCreate()` or your fragments `onCreateView()` you would want to do something like this
```java
CircularTabLayout tabLayout = (CircularTabLayout) findViewById(R.id.circular_tab);
CircularViewPager viewPager = (CircularViewPager) findViewById(R.id.circular_viewpager);
MyAdapter adapter = new MyAdapter(getFragmentManager());
viewPager.setFragmentAdapter(adapter, getFragmentManager());
tabLayout.setupWithViewPager(viewPager);
```

`MyAdapter` in the above example would look something like this, a list of fragments.
```java
class MyAdapter extends FragmentPagerAdapter {

    List<Fragment> mFragments = new ArrayList<>();

    public MyAdapter(FragmentManager fm) {
        super(fm);
        for (int position = 0; position < 5; position++) {
            mFragments.add(new SampleFragment());
        }
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return String.format("Page %d", position);
    }

}
```

That's it! Look through the API docs below to get know about things to customize and if you have any problems getting started please open an issue as it probably means the getting started guide need some improvement!

**Styling**

You can apply your own theme to `CircularTabLayout`. Say you define a style called `CircularTabTextAppearance` in values/styles.xml:
```xml
<resources>
    <style name="CircularTabTextAppearance" parent="@style/TextAppearance.Design.Tab">
        <item name="android:textSize">@dimen/circular_tab_textsize</item>
        <item name="android:textColor">@color/colorPrimaryDark</item>
    </style>
</resources>
```
Then add `CircularTabTextAppearance` to `CircularTabLayout`:
```xml
<com.sanyuzhang.circular.viewpager.cvp.view.CircularTabLayout
    android:id="@+id/circular_tab"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    app:tabTextAppearance="@style/CircularTabTextAppearance"/>
```

Upcoming...
