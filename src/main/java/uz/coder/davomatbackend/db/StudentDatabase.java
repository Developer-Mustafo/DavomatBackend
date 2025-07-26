package uz.coder.davomatbackend.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.coder.davomatbackend.db.model.StudentDbModel;
import uz.coder.davomatbackend.db.model.TimeRange;

@Repository
public interface StudentDatabase extends JpaRepository<StudentDbModel, Long> {}
