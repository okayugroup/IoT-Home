package com.okayugroup.IotHome;

import java.util.ArrayList;
import java.util.List;

public class Ref {
    public static class A {
        public A(String name) {
            this.name = name;
        }
        String name;
    }
    public static void main(String[] args) {
        List<A> list = new ArrayList<>();
        A original = new A("a");
        list.add(original);
        list.add(new A("b"));

        A copied = list.get(0);

        copied.name = "c";

        System.out.println(original.name);
        System.out.println(copied.name);

    }
}
