package ru.ProjectFile;

public class TestSolve {

    public static void main(String[] args) {
        String url = args[0];
        var testSystem = new TestSystem(url);
        testSystem.startSystem();
    }
}
