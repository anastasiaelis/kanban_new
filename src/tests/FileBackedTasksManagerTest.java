package tests;

import manager.FileBackedTasksManager;
import manager.InMemoryTaskManager;
import manager.Managers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.TaskStatus;
import task.Epic;
import task.Task;
import tests.TaskManagerTest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FileBackedTasksManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    public static final Path path = Path.of("data.test.csv");
    File file = new File(String.valueOf(path));
    @BeforeEach
    public void beforeEach() {
        manager = new FileBackedTasksManager(Managers.getDefaultHistory(), file);
    }

    @AfterEach
    public void afterEach() {
        try {
            Files.delete(path);
        } catch (IOException exception) {
            System.out.println(exception.getMessage());
        }
    }

    @Test
    public void shouldCorrectlySaveAndLoad() {
        Task task = new Task(1,"Description", "Title", TaskStatus.NEW, Instant.now(), 0);
        manager.addNew(task);
        Epic epic = new Epic(1,"Description", "Title", TaskStatus.NEW, new ArrayList<>(),Instant.now(), 0);
        manager.addNewEp(epic);
        FileBackedTasksManager fileManager = new FileBackedTasksManager(Managers.getDefaultHistory(),file);
        fileManager.loadFromFile(file);
        assertEquals(List.of(task), manager.getTasks());
        assertEquals(List.of(epic), manager.getEps());
    }

    @Test
    public void shouldSaveAndLoadEmptyTasksEpicsSubtasks() {
        FileBackedTasksManager fileManager = new FileBackedTasksManager(Managers.getDefaultHistory(),file);
        fileManager.save();
        fileManager.loadFromFile(file);
        assertEquals(Collections.EMPTY_LIST, manager.getTasks());
        assertEquals(Collections.EMPTY_LIST, manager.getEps());
        assertEquals(Collections.EMPTY_LIST, manager.getSubs());
    }

    @Test
    public void shouldSaveAndLoadEmptyHistory() {
        FileBackedTasksManager fileManager = new FileBackedTasksManager(Managers.getDefaultHistory(), file);
        fileManager.save();
        fileManager.loadFromFile(file);
        assertEquals(Collections.EMPTY_LIST, manager.getHistory());
    }
}