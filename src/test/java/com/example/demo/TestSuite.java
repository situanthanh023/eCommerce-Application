package com.example.demo;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({CartControllerTest.class, ItemControllerTest.class, OrderControllerTest.class, UserControllerTest.class})
public class TestSuite {

}