package uz.coder.davomatbackend.db;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.coder.davomatbackend.db.model.TelegramUserDbModel;

@Repository
public interface TelegramUserDatabase extends JpaRepository<TelegramUserDbModel, Long> {
    @Modifying
    @Transactional
    @Query("update TelegramUserDbModel t set t.firstName=:firstName, t.lastName=:lastName, t.phoneNumber=:phoneNtmber where t.id=:id")
    void update(@Param("id") long id, @Param("firstName") String firstName, @Param("lastName") String lastName, @Param("phoneNumber") String phoneNumber);

    @Query("select t from TelegramUserDbModel t where t.telegramUserId=:telegramUserId")
    TelegramUserDbModel findByTelegramUserId(@Param("telegramUserId") long telegramUserId);

    boolean existsByTelegramUserId(long telegramUserId);
}
