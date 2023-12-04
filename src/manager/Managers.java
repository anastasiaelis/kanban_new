package manager;

import java.io.File;

public  class Managers {
    public static  TaskManager getDefault(){
        return new FileBackedTasksManager(Managers.getDefaultHistory(),new  File("resources/file.csv"));

    }
    public static HistoryManager getDefaultHistory(){
        return new InMemoryHistoryManager();

    }
}