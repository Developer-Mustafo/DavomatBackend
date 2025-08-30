package uz.coder.davomatbackend.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.coder.davomatbackend.db.model.TelegramUserDbModel;

@Repository
public interface TelegramUserDatabase extends JpaRepository<TelegramUserDbModel, Long> {
    @Query("select t from TelegramUserDbModel t where t.telegramUserId=:telegramUserId")
    TelegramUserDbModel findByTelegramUserId(@Param("telegramUserId") long telegramUserId);

    boolean existsByTelegramUserId(long telegramUserId);
}
