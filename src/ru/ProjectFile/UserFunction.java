package ru.ProjectFile;

public class UserFunction{
    final private String functionName;
    final private int functionID;
    final private BDUserFunc func;

    public UserFunction(String functionName, BDUserFunc func, int functionID){
        this.functionName = functionName;
        this.func = func;
        this.functionID = functionID;
    }
    public boolean execute() throws Exception{
        return func != null && func.execute();
    }

    public int getFunctionID() {
        return functionID;
    }

    public String getFunctionName() {
        return functionName;
    }

    @Override
    public String toString() {
        return functionName;
    }
}
