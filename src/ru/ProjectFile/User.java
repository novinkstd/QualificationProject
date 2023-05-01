package ru.ProjectFile;

import java.util.List;

public class User {
    final private String login;
    private final List<UserFunction> functionsList;

    public User(String  login, List<UserFunction> functionsList){
        this.login = login;
        this.functionsList = functionsList;
    }

    public String getLogin() {
        return login;
    }

    public List<UserFunction> getFunctionsList() {
        return functionsList;
    }

    public void printFunctionsList(){
        for (var func: functionsList) {
            System.out.println(func);
        }
    }

    @Override
    public String toString() {
        return login;
    }

}
