package muwanguzi.journalapp;

import android.test.suitebuilder.annotation.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class MainScreenTest {

    @Rule
    public ActivityTestRule<MainScreen> mActivityTestRule = new ActivityTestRule<>(MainScreen.class);

    @Test
    public void menuScreenTest() {
    }

}
