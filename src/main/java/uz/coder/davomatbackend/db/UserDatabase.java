package uz.coder.davomatbackend.db;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.coder.davomatbackend.db.model.UserDbModel;

@Repository
public interface UserDatabase extends JpaRepository<UserDbModel, Long> {
    @Modifying
    @Transactional
    @Query("update UserDbModel u set u.firstName=:firstName, u.lastName=:lastName, u.email=:email, u.password=:password, u.phoneNumber=:phoneNumber, u.role=:role where u.id=:id")
    void update(@Param("id") long id, @Param("firstName") String firstName, @Param("lastName") String lastName, @Param("email") String email, @Param("password") String password, @Param("phoneNumber") String phoneNumber,@Param("role") String role);

    @Query("select u from UserDbModel u where u.email=:email and u.password=:password")
    UserDbModel findByEmailAndPassword(@Param("email") String email, @Param("password") String password);

    @Query("select u from UserDbModel u where u.phoneNumber=:phoneNumber")
    UserDbModel findByPhoneNumber(@Param("phoneNumber") String phoneNumber);
}
