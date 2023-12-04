package task;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Objects;
public class SubTask extends Task {
    public int getNumberEpic() {
        return numberEpic;
    }

    public void setNumberEpic(int numberEpic) {
        this.numberEpic = numberEpic;
    }

    private int numberEpic;
    public SubTask(int id, int numberEpic, String name, String description, TaskStatus status, Instant startTime, long duration) {
        super(id, name, description, status,startTime,duration);
        this.numberEpic = numberEpic;
    }
}
