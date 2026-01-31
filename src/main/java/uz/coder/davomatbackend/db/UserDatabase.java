package uz.coder.davomatbackend.db;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.coder.davomatbackend.db.model.UserDbModel;
import uz.coder.davomatbackend.model.Balance;

import java.time.Instant;
import java.time.LocalDate;

@Repository
public interface UserDatabase extends JpaRepository<UserDbModel, Long> {
    @Modifying
    @Transactional
    @Query("update UserDbModel u set u.firstName=:firstName, u.lastName=:lastName, u.email=:email, u.password=:password, u.phoneNumber=:phoneNumber, u.role=:role, u.lastPasswordResetAt=:lastPasswordResetAt where u.id=:id")
    void update(@Param("id") long id, @Param("firstName") String firstName, @Param("lastName") String lastName, @Param("email") String email, @Param("password") String password, @Param("phoneNumber") String phoneNumber, @Param("role") String role, @Param("lastPasswordResetAt") Instant lastPasswordResetAt);

    @Query("select u from UserDbModel u where u.phoneNumber=:phoneNumber")
    UserDbModel findByPhoneNumber(@Param("phoneNumber") String phoneNumber);

    @Query("select u.firstName from UserDbModel u where u.id=:userId")
    String findFirstNameById(@Param("userId") long userId);

    @Query("select u.lastName from UserDbModel u where u.id=:userId")
    String findLastNameById(@Param("userId")  long userId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query("UPDATE UserDbModel u SET u.payedDate = :payedDate WHERE u.id = :id")
    int updateBalanceUser(@Param("payedDate") LocalDate payedDate, @Param("id") long id);

    @Query("select new uz.coder.davomatbackend.model.Balance(u.payedDate, u.id) from UserDbModel u where u.id=:id")
    Balance getUserBalanceById(@Param("id") long id);

    boolean existsByPhoneNumber(String phoneNumber);

    @Query("select u from UserDbModel u where u.email=:email")
    UserDbModel findByEmail(String email);
}
