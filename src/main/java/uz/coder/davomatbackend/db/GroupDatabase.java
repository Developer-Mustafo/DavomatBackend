package uz.coder.davomatbackend.db;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.coder.davomatbackend.db.model.GroupDbModel;

import java.util.List;

@Repository
public interface GroupDatabase extends JpaRepository<GroupDbModel, Long> {
    @Modifying
    @Transactional
    @Query("update GroupDbModel g set g.title=:title, g.courseId=:courseId where g.id=:id")
    void update(@Param("id") long id, @Param("title") String title, @Param("courseId") long courseId);

    @Query("select g from GroupDbModel g where g.courseId=:courseId")
    List<GroupDbModel> findAllByCourseId(@Param("courseId") long courseId);

    @Query("""
        select g from GroupDbModel g
        where g.courseId in :courseIds
    """)
    List<GroupDbModel> findGroupsByCourseIds(@Param("courseIds") List<Long> courseIds);
}
