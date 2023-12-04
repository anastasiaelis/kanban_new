package tests;

import manager.TaskManager;
import org.junit.jupiter.api.Test;
import task.TaskStatus;
import task.Task;
import task.Epic;
import task.SubTask;


import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {
    protected T manager;
    protected Task addNew() {
        return new Task(1,"Description", "Title", TaskStatus.NEW, Instant.now(), 0);
    }
    protected Epic addNewEp() {

        return new Epic(2,"Description", "Title",TaskStatus.NEW, new ArrayList<>(), Instant.now(), 0);
    }
    protected SubTask addNewSub(Epic epic) {
        return new SubTask(3, epic.getId(),"Description", "Title", TaskStatus.NEW,  Instant.now(), 0);
    }

    @Test
    public void shouldCreateTask() {
        Task task = addNew();
        manager.addNew(task);
        List<Task> tasks = manager.getTasks();
        assertNotNull(task.getStatus());
        assertEquals(TaskStatus.NEW, task.getStatus());
        assertEquals(List.of(task), tasks);
    }

    @Test
    public void shouldCreateEpic() {
        Epic epic = addNewEp();
        manager.addNewEp(epic);
        List<Epic> epics = manager.getEps();
        assertNotNull(epic.getStatus());
        assertEquals(TaskStatus.NEW, epic.getStatus());
        //////////////////////
        assertEquals(List.of(epic), epics);
    }

    @Test
    public void shouldCreateSubTask() {
        Epic epic = addNewEp();
        manager.addNewEp(epic);
        SubTask subtask = addNewSub(epic);
        manager.addNewSub(subtask);
        List<SubTask> subtasks = manager.getSubs();
        assertNotNull(subtask.getStatus());
        assertEquals(epic.getId(), subtask.getNumberEpic());
        assertEquals(TaskStatus.NEW, subtask.getStatus());
        assertEquals(List.of(subtask), subtasks);
        assertEquals(List.of(subtask.getId()), epic.getNumberSub());
    }   

    @Test
    public void shouldUpdateTaskStatusToInProgress() {
        Task task = addNew();
        manager.addNew(task);
        task.setStatus(TaskStatus.INPROGRESS);
        manager.updateTask(task);
        assertEquals(TaskStatus.INPROGRESS, manager.getOneTask(task.getId()).getStatus());
    }

    @Test
    public void shouldUpdateEpicStatusToInProgress() {
        Epic epic = addNewEp();
        manager.addNewEp(epic);
        epic.setStatus(TaskStatus.INPROGRESS);
        assertEquals(TaskStatus.INPROGRESS, manager.getOneEp(epic.getId()).getStatus());
    }

    @Test
    public void shouldUpdateSubTaskStatusToInProgress() {
        Epic epic = addNewEp();
        manager.addNewEp(epic);
        SubTask subtask = addNewSub(epic);
        manager.addNewSub(subtask);
        subtask.setStatus(TaskStatus.INPROGRESS);
        manager.updateSub(subtask);
        assertEquals(TaskStatus.INPROGRESS, manager.getOneSub(subtask.getId()).getStatus());
        assertEquals(TaskStatus.INPROGRESS, manager.getOneEp(epic.getId()).getStatus());
    }

    @Test
    public void shouldUpdateTaskStatusToInDone() {
        Task task = addNew();
        manager.addNew(task);
        task.setStatus(TaskStatus.DONE);
        manager.updateTask(task);
        assertEquals(TaskStatus.DONE, manager.getOneTask(task.getId()).getStatus());
    }

    @Test
    public void shouldUpdateEpicStatusToInDone() {
        Epic epic = addNewEp();
        manager.addNewEp(epic);
        epic.setStatus(TaskStatus.DONE);
        assertEquals(TaskStatus.DONE, manager.getOneEp(epic.getId()).getStatus());
    }

    @Test
    public void shouldUpdateSubTaskStatusToInDone() {
        Epic epic = addNewEp();
        manager.addNewEp(epic);
        SubTask subtask = addNewSub(epic);
        manager.addNewSub(subtask);
        subtask.setStatus(TaskStatus.DONE);
        manager.updateSub(subtask);
        assertEquals(TaskStatus.DONE, manager.getOneSub(subtask.getId()).getStatus());
        assertEquals(TaskStatus.DONE, manager.getOneEp(epic.getId()).getStatus());
    }

    @Test
    public void shouldNotUpdateTaskIfNull() {
        Task task = addNew();
        manager.addNew(task);
        manager.updateTask(null);
        assertEquals(task, manager.getOneTask(task.getId()));
    }

    @Test
    public void shouldNotUpdateEpicIfNull() {
        Epic epic = addNewEp();
        manager.addNewEp(epic);
        manager.updateEp(null);
        assertEquals(epic, manager.getOneEp(epic.getId()));
    }

    @Test
    public void shouldNotUpdateSubTaskIfNull() {
        Epic epic = addNewEp();
        manager.addNewEp(epic);
        SubTask subtask = addNewSub(epic);
        manager.addNewSub(subtask);
        manager.updateSub(null);
        assertEquals(subtask, manager.getOneSub(subtask.getId()));
    }

    @Test
    public void shouldDeleteAllTasks() {
        Task task = addNew();
        manager.addNew(task);
        manager.delete();
        assertEquals(Collections.EMPTY_LIST, manager.getTasks());
    }

    @Test
    public void shouldDeleteAllEpics() {
        Epic epic = addNewEp();
        manager.addNewEp(epic);
        manager.deleteEp();
        assertEquals(Collections.EMPTY_LIST, manager.getEps());
    }

    @Test
    public void shouldDeleteAllSubTasks() {
        Epic epic = addNewEp();
      manager.addNewEp(epic);
      SubTask subtask = addNewSub(epic);

        manager.addNewSub(subtask);
        manager.deleteSub();
        assertTrue(epic.getNumberSub().isEmpty());
        assertTrue(manager.getSubs().isEmpty());
    }

    @Test
    public void shouldDeleteAllSubTasksByEpic() {
        Epic epic = addNewEp();
        manager.addNewEp(epic);
        SubTask subtask = addNewSub(epic);
        manager.addNewSub(subtask);
        manager.deleteSub();
        assertTrue(epic.getNumberSub().isEmpty());
        assertTrue(manager.getSubs().isEmpty());
    }

    @Test
    public void shouldDeleteTaskById() {
        Task task = addNew();
        manager.addNew(task);
        manager.deleteOne(task.getId());
        assertEquals(Collections.EMPTY_LIST, manager.getTasks());
    }

    @Test
    public void shouldDeleteEpicById() {
        Epic epic = addNewEp();
        manager.addNewEp(epic);
        manager.deleteOneEp(epic.getId());
        assertEquals(Collections.EMPTY_LIST, manager.getEps());
    }

    @Test
    public void shouldNotDeleteTaskIfBadId() {
        Task task = addNew();
        manager.addNew(task);
        manager.deleteOne(999);
        assertEquals(List.of(task), manager.getTasks());
    }

    @Test
    public void shouldNotDeleteEpicIfBadId() {
        Epic epic = addNewEp();
        manager.addNewEp(epic);
        manager.deleteOneEp(999);
        assertEquals(List.of(epic), manager.getEps());
    }

    @Test
    public void shouldNotDeleteSubTaskIfBadId() {
        Epic epic = addNewEp();
        manager.addNewEp(epic);
        SubTask subtask = addNewSub(epic);
        manager.addNewSub(subtask);
        manager.deleteOneSub(999);
        assertEquals(List.of(subtask), manager.getSubs());
        assertEquals(List.of(subtask.getId()), manager.getOneEp(epic.getId()).getNumberSub());
    }

    @Test
    public void shouldDoNothingIfTaskHashMapIsEmpty(){
        manager.delete();
        manager.deleteOne(999);
        assertEquals(0, manager.getTasks().size());
    }

    @Test
    public void shouldDoNothingIfEpicHashMapIsEmpty(){
        manager.deleteEp();
        manager.deleteOneEp(999);
        assertTrue(manager.getEps().isEmpty());
    }

    @Test
    public void shouldDoNothingIfSubTaskHashMapIsEmpty(){
        manager.deleteEp();
        manager.deleteOneSub(999);
        assertEquals(0, manager.getSubs().size());
    }

    @Test
    void shouldReturnEmptyListWhenGetSubTaskByEpicIdIsEmpty() {
        Epic epic = addNewEp();
        manager.addNewEp(epic);
        List<Integer> subtasks = manager.getSubList(epic.getId());
        assertTrue(subtasks.isEmpty());
    }

    @Test
    public void shouldReturnEmptyListTasksIfNoTasks() {
        assertTrue(manager.getTasks().isEmpty());
    }

    @Test
    public void shouldReturnEmptyListEpicsIfNoEpics() {
        assertTrue(manager.getEps().isEmpty());
    }

    @Test
    public void shouldReturnEmptyListSubTasksIfNoSubTasks() {
        assertTrue(manager.getSubs().isEmpty());
    }

    @Test
    public void shouldReturnNullIfTaskDoesNotExist() {
        assertNull(manager.getOneTask(999));
    }

    @Test
    public void shouldReturnNullIfEpicDoesNotExist() {
        assertNull(manager.getOneEp(999));
    }

    @Test
    public void shouldReturnNullIfSubTaskDoesNotExist() {
        assertNull(manager.getOneSub(999));
    }

    @Test
    public void shouldReturnEmptyHistory() {
        assertEquals(Collections.EMPTY_LIST, manager.getHistory());
    }

    @Test
    public void shouldReturnEmptyHistoryIfTasksNotExist() {
        manager.getOneTask(999);
        manager.getOneSub(999);
        manager.getOneEp(999);
        assertTrue(manager.getHistory().isEmpty());
    }

    @Test
    public void shouldReturnHistoryWithTasks() {
        Epic epic = addNewEp();
        manager.addNewEp(epic);
        SubTask subtask = addNewSub(epic);
        manager.addNewSub(subtask);
        manager.getOneEp(epic.getId());
        manager.getOneSub(subtask.getId());
        List<Task> list = manager.getHistory();
        assertEquals(2, list.size());
        assertTrue(list.contains(subtask));
        assertTrue(list.contains(epic));
    }

}