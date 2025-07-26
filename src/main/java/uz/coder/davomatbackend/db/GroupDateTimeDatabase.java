package uz.coder.davomatbackend.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.coder.davomatbackend.db.model.GroupDateTimeDbModel;
import uz.coder.davomatbackend.db.model.GroupDbModel;

@Repository
public interface GroupDateTimeDatabase extends JpaRepository<GroupDateTimeDbModel, Long> {}
