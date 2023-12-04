package task;

import java.util.List;
import java.time.Instant;
import java.util.*;
public class Epic extends Task {
    public List<Integer> getNumberSub() {
        return numberSub;
    }

    private List<Integer> numberSub;
    private Instant endTime;

    public void setNumberSub(List<Integer> numberSub) {
        this.numberSub = numberSub;
    }

    public Epic(int id, String name, String description, TaskStatus status, List<Integer> numberSub, Instant startTime, long duration) {
        super(id, name, description, status,startTime,duration);
        this.numberSub = numberSub;
    }
    @Override
    public Instant getEndTime() {
        return endTime;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }
}
