package manager;

import task.Task;
import task.Epic;
import task.SubTask;
import task.TaskStatus;
import java.time.Instant;
import java.util.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    protected final Map<Integer, Epic> tableEpic= new HashMap<>();
    protected final Map<Integer, Task> tableTask = new HashMap<>();
    protected final Map<Integer, SubTask> tableSubTask = new HashMap<>();
    protected HistoryManager historyManager = null;
    private int numberTask = 0;//generatorId
    private final Comparator<Task> taskComparator = Comparator.comparing(Task::getStartTime);
    protected Set<Task> prioritizedTasks = new TreeSet<>(taskComparator);

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }



    //ОБЫЧНЫЕ
    //Добавление новой задачи
    @Override
  public void addNew(Task newTask) {
        numberTask += 1;
        newTask.setId(numberTask);
        addNewPrioritizedTask(newTask);

        tableTask.put(numberTask, newTask);
    }

    //обновление задачи
    @Override
    public void updateTask(Task task) {
        if (task!=null) {
            if (tableTask.containsKey(task.getId())) {
                addNewPrioritizedTask(task);
                tableTask.put(task.getId(), task);
            }
        }
    }


    //Удаление задач
    @Override
    public void delete() {
        tableTask.clear();
    }

    //Удаление задачи по номеру
    @Override
    public void deleteOne(int number) {
        tableTask.remove(number);
    }

    //Печать списка задач
    @Override
    public ArrayList<Task> getTasks() {
        int sizeTable = tableTask.size();
        if (sizeTable > 0) {
            return new ArrayList<>(tableTask.values());
        } else {
            return new ArrayList<>();
        }
    }

    //Печать одной задачи по номеру
    @Override
    public Task getOneTask(int number) {
        historyManager.add(tableTask.get(number));
        return tableTask.get(number);
    }

    ///ПОДЗАДАЧИ
    //Добавление новой задачи
    @Override
    public void addNewSub(SubTask newSubTask) {
        numberTask += 1;
        newSubTask.setId(numberTask);
        addNewPrioritizedTask(newSubTask);
        tableSubTask.put(numberTask, newSubTask);
        int numberEpic = newSubTask.getNumberEpic();
        Epic upEic = tableEpic.get(numberEpic);
        List<Integer> addingList = tableEpic.get(numberEpic).getNumberSub();
        addingList.add(numberTask);
        updateEp(upEic);
        updateTimeEpic(upEic);
    }

    //Печать одной подзадачи
    @Override
    public List<SubTask> getSubs() {
        int sizeTable = tableSubTask.size();
        if (sizeTable > 0) {
            return new ArrayList<>(tableSubTask.values());
        } else {
            return new ArrayList<>();
        }

    }

    //обновление подзадачи
    @Override
    public void updateSub(SubTask upSupTask) {
        if (upSupTask!=null) {

            int number = upSupTask.getId();
            if (tableSubTask.containsKey(number)) {
                int numberEpic = tableSubTask.get(number).getNumberEpic();
                addNewPrioritizedTask(upSupTask);
                tableSubTask.put(number, upSupTask);
                Epic upEpic = tableEpic.get(numberEpic);
                updateEp(upEpic);
                updateTimeEpic(upEpic);
            }
        }
    }
    public void updateTimeEpic(Epic epic) {
        List<Integer> numberSubtasks = epic.getNumberSub();
        List<SubTask> subtasks=new ArrayList<>();
        int iss=0;
        if (!numberSubtasks.isEmpty()) {
            for (int num : numberSubtasks) {
                subtasks.add(iss, getOneSub(num));
                iss = iss + 1;
            }
            Instant startTime = subtasks.get(0).getStartTime();
            Instant endTime = subtasks.get(0).getEndTime();

            for (SubTask subtask : subtasks) {
                if (subtask.getStartTime().isBefore(startTime)) startTime = subtask.getStartTime();
                if (subtask.getEndTime().isAfter(endTime)) endTime = subtask.getEndTime();
            }

            epic.setStartTime(startTime);
            epic.setEndTime(endTime);
            long duration = (endTime.toEpochMilli() - startTime.toEpochMilli());
            epic.setDuration(duration);
        }
    }
    //Удаление подзадач
    @Override
    public void deleteSub() {
        tableSubTask.clear();
        for (int i : tableEpic.keySet()) {
            Epic epic = tableEpic.get(i);
            epic.getNumberSub().clear();
            updateEp(epic);
        }
    }

    //Удаление задачи по номеру
    @Override
    public void deleteOneSub(int number) {
        if (tableSubTask.containsKey(number)) {
            int numberEp = tableSubTask.get(number).getNumberEpic();
            List<Integer> listSub = tableEpic.get(numberEp).getNumberSub();
            listSub.remove(number);
            tableSubTask.remove(number);

            Epic upEpic = tableEpic.get(numberEp);
            upEpic.setNumberSub(listSub);
            updateEp(upEpic);
            updateTimeEpic(upEpic);
        }
    }

    //Печать одной задачи по номеру
    @Override
    public SubTask getOneSub(int number) {
        historyManager.add(tableSubTask.get(number));
        return tableSubTask.get(number);
    }

    ////Большие задачи
    //Добавление новой задачи
    @Override
    public void addNewEp(Epic newEpic) {
        numberTask += 1;
        newEpic.setId(numberTask);
        TaskStatus status=calculateStatus(newEpic);
        newEpic.setStatus(status);
        tableEpic.put(numberTask, newEpic);
    }

    @Override
    public List <Epic> getEps() {
        int sizeTable = tableEpic.size();
        if (sizeTable > 0) {
            return new ArrayList<>(tableEpic.values());
        } else {
            return new ArrayList<>();        }
    }

    @Override
    public Epic getOneEp(int number) {
        historyManager.add(tableEpic.get(number));
        return tableEpic.get(number);
    }

    //Удаление большой задачи по номеру
    @Override
    public void deleteOneEp(int number) {
        if (!(tableEpic.get(number)==null)){
        List<Integer> deleteList = tableEpic.get(number).getNumberSub();
        for (int i : deleteList) {
            deleteOneSub(i);
        }
        tableEpic.remove(number);
    }
    }

    //обновление большой задачи
    @Override
    public void updateEp(Epic newEp) {
        if (newEp!=null){
        int numberEp = newEp.getId();
        if (tableEpic.containsKey(numberEp)) {
            TaskStatus status=calculateStatus(newEp);
            newEp.setStatus(status);
            tableEpic.put(numberEp, newEp);
            updateTimeEpic(newEp);
        }
    }
    }


    //Удаление всех больших задач
    @Override
    public void deleteEp() {
        tableEpic.clear();
        deleteSub();
    }

    @Override
    public List<Integer> getSubList(int number) {
        return tableEpic.get(number).getNumberSub();
    }

    private TaskStatus calculateStatus(Epic newEpic){
        TaskStatus status;
        if (newEpic.getNumberSub().isEmpty()) {
            status = TaskStatus.NEW;
        } else {
            int anew = 0;
            int adone = 0;

            for (int i : newEpic.getNumberSub()) {
                if (tableSubTask.get(i).getStatus() == TaskStatus.NEW) {
                    anew += 1;
                }
                if (tableSubTask.get(i).getStatus() == TaskStatus.DONE) {
                    adone += 1;
                }
            }
            if (newEpic.getNumberSub().size() == anew) {
                status = TaskStatus.NEW;
            } else if (newEpic.getNumberSub().size() == adone) {
                status = TaskStatus.DONE;
            } else {
                status = TaskStatus.INPROGRESS;
            }
        }
       return status;
    }

    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    private void addNewPrioritizedTask(Task task) {
        validateTaskPriority(task);
        prioritizedTasks.add(task);
    }



    private void validateTaskPriority(Task task) {
        boolean taskHasIntersections=false;
        List<Task> tasks = prioritizedTasks.stream().collect(Collectors.toList());
        getPrioritizedTasks();
        int nomerPeres=0;
        int sizeTimeNull = 0;
        if (!tasks.isEmpty()) {
            for (Task taskSave : tasks) {
                if (taskSave.getStartTime() != null && taskSave.getEndTime() != null) {
                    if (task.getStartTime().isBefore(taskSave.getStartTime())
                        && task.getEndTime().isBefore(taskSave.getStartTime())) {
                        taskHasIntersections= true;
                        nomerPeres=taskSave.getId();
                    } else if (task.getStartTime().isAfter(taskSave.getEndTime())
                          && task.getEndTime().isAfter(taskSave.getEndTime())) {
                         taskHasIntersections=true;
                         nomerPeres=taskSave.getId();
                    }
                }
            }
        } else {
            taskHasIntersections=false;
        }
        if (taskHasIntersections) {
            throw new ManagerValidateException(
                    "Задачи №" + task.getId() + " и №" + nomerPeres + "пересекаются");
        }
    }


    private List<Task> getPrioritizedTasks() {
        return prioritizedTasks.stream().collect(Collectors.toList());
    }

}

