package manager;
import task.Task;
import task.SubTask;
import task.Epic;
import java.util.List;

public interface TaskManager {
    //ОБЫЧНЫЕ
    //Добавление новой задачи
    void addNew(Task newTask);

    //обновление задачи
    void updateTask(Task task);

    //Удаление задач
    void delete();

    //Удаление задачи по номеру
    void deleteOne(int number);

    //Печать списка задач
    List<Task> getTasks();

    //Печать одной задачи по номеру
    Task getOneTask(int number);

    ///ПОДЗАДАЧИ
    //Добавление новой задачи
    void addNewSub(SubTask newSubTask);

    //Печать одной подзадачи
    List<SubTask> getSubs();

    //обновление подзадачи
    void updateSub(SubTask upSupTask);

    //Удаление подзадач
    void deleteSub();

    //Удаление задачи по номеру
    void deleteOneSub(int number);

    //Печать одной задачи по номеру
    SubTask getOneSub(int number);

    ////Большие задачи
    //Добавление новой задачи
    void addNewEp(Epic newEpic);

    List<Epic> getEps();

    Epic getOneEp(int number);

    //Удаление большой задачи по номеру
    void deleteOneEp(int number);

    //обновление большой задачи
    void updateEp(Epic newEp);

    //Удаление всех больших задач
    void deleteEp();

    List<Integer> getSubList(int number);
    List<Task> getHistory();
}
