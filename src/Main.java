import manager.FileBackedTasksManager;
import java.io.File;
public class Main {
    public static void main(String[] args) {

       FileBackedTasksManager tasksManager = FileBackedTasksManager.loadFromFile(new File("resources/file.csv"));
        System.out.println(tasksManager.getHistory());

    }
}
