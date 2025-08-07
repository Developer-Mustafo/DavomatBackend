package uz.coder.davomatbackend.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.coder.davomatbackend.db.model.AttendanceDbModel;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceDatabase extends JpaRepository<AttendanceDbModel, Long> {

    @Query("SELECT a FROM AttendanceDbModel a WHERE a.studentId = :studentId AND a.date = :date")
    Optional<AttendanceDbModel> findByStudentIdAndDate(@Param("studentId") Long studentId,
                                                       @Param("date") LocalDate date);

    @Query("SELECT a FROM AttendanceDbModel a WHERE a.date BETWEEN :start AND :end AND a.studentId IN :studentIds")
    List<AttendanceDbModel> findAllByDateBetweenAndStudentIdIn(@Param("start") LocalDate start,
                                                               @Param("end") LocalDate end,
                                                               @Param("studentIds") List<Long> studentIds);

    @Query("SELECT a FROM AttendanceDbModel a WHERE a.studentId = :studentId")
    List<AttendanceDbModel> findAllByStudentId(@Param("studentId") Long studentId);
}