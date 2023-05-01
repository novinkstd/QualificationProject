package ru.ProjectFile;

public interface BDUserFunc {
    default boolean execute() throws Exception{
        return false;
    }
}
