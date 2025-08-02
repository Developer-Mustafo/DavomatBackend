package uz.coder.davomatbackend.db;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.coder.davomatbackend.db.model.CourseDbModel;
import java.util.List;

@Repository
public interface CourseDatabase extends JpaRepository<CourseDbModel, Long> {
    @Transactional
    @Modifying
    @Query("update CourseDbModel c set c.title=:title, c.description=:description, c.userId=:userId where c.id=:id")
    void update(@Param("id") long id, @Param("title") String title, @Param("description") String description, @Param("userId") long userId);

    @Query("select c from CourseDbModel c where c.userId=:userId")
    List<CourseDbModel> findAllByUserId(@Param("userId") long userId);
}
