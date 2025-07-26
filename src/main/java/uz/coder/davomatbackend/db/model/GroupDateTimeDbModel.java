package uz.coder.davomatbackend.db.model;

import jakarta.persistence.*;
import lombok.*;
import org.antlr.v4.runtime.misc.Pair;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Entity
@Table(name = "group_date_time")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class GroupDateTimeDbModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "group_id", nullable = false)
    private long groupId;

    @ElementCollection
    @CollectionTable(name = "group_dates", joinColumns = @JoinColumn(name = "group_date_time_id"))
    @Column(name = "date")
    private List<LocalDate> dates;

    @ElementCollection
    @CollectionTable(name = "group_times", joinColumns = @JoinColumn(name = "group_date_time_id"))
    private List<TimeRange> timesStartAndEnd;

    public GroupDateTimeDbModel(long groupId, List<LocalDate> dates, List<TimeRange> timesStartAndEnd) {
        this.groupId = groupId;
        this.dates = dates;
        this.timesStartAndEnd = timesStartAndEnd;
    }
}
