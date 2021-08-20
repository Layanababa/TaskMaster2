package com.example.taskmaster;


import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;


@RunWith(AndroidJUnit4.class)
public class MainTest {
    @Rule
    public ActivityTestRule<MainActivity> mainActivityActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void usernameTestChanged(){

        onView(withId(R.id.username)).perform(typeText("Layana"), closeSoftKeyboard());
        onView(withId(R.id.setting)).perform(click());

        onView(withId(R.id.homeusername)).check(matches(withText("Layana")));

    }

    @Test
    public void goToSecondActivity(){
        onView(withId(R.id.button_second)).perform(click());
        onView(withId(R.id.textview_third)).check(matches(withText("All Tasks")));
    }
}
