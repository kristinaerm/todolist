package com.example.todolist;

import org.junit.Test;

import java.util.LinkedList;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void findNoCategoryId_isCorrect() {
        // arrange
        String noCategoryName = "NoCategory";
        LinkedList<Category> categoriesList = new LinkedList<>();
        categoriesList.add(new Category("1", "NotRight1", 1, "1"));
        categoriesList.add(new Category("2", noCategoryName, 1, "1"));
        categoriesList.add(new Category("3", "NotRight3", 1, "1"));
        // act
        String res = MainAndNavigation.findNoCategoryId(categoriesList, noCategoryName);
        // assert
        assertEquals("2", res);
    }
}