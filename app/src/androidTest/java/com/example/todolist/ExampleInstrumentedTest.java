package com.example.todolist;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    private String stringToBeTyped;

    @Rule
    public ActivityTestRule<EmailPassowordActivity> activityRule
            = new ActivityTestRule<>(EmailPassowordActivity.class);

    @Test
    public void changeText_sameActivity() {
        onView(withId(R.id.et_email))
                .check(matches(isDisplayed()));
        onView(withId(R.id.et_password))
                .check(matches(isDisplayed()));
        onView(withId(R.id.btn_registration))
                .check(matches(isDisplayed()));
        onView(withId(R.id.btn_sign_in))
                .check(matches(isDisplayed()));
    }
}
