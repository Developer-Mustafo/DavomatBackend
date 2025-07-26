package uz.coder.davomatbackend.model;

import lombok.*;
import org.antlr.v4.runtime.misc.Pair;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class GroupDateTime {
    private long id;
    private long groupId;
    private List<LocalDate> dates;
    private List<Pair<LocalTime, LocalTime>> timesStartAndEnd;

    public GroupDateTime(long groupId, List<LocalDate> dates, List<Pair<LocalTime, LocalTime>> timesStartAndEnd) {
        this.groupId = groupId;
        this.dates = dates;
        this.timesStartAndEnd = timesStartAndEnd;
    }
}
