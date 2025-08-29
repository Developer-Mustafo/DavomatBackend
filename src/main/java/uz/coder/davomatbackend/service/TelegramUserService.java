package uz.coder.davomatbackend.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uz.coder.davomatbackend.db.*;
import uz.coder.davomatbackend.db.model.*;
import uz.coder.davomatbackend.model.TelegramUser;
import java.util.List;
import java.util.stream.Collectors;
import static uz.coder.davomatbackend.todo.Strings.*;

@Slf4j
@Service
public class TelegramUserService {
    private final TelegramUserDatabase database;
    private final UserDatabase userDatabase;
    @Autowired
    public TelegramUserService(TelegramUserDatabase database, UserDatabase userDatabase) {
        this.database = database;
        this.userDatabase = userDatabase;
    }
    public TelegramUser save(TelegramUser telegramUser){
        if (database.existsByTelegramUserId(telegramUser.getTelegramUserId())) {
            if (userDatabase.existsByPhoneNumber(telegramUser.getPhoneNumber())){
                TelegramUserDbModel telegramUserDbModel = database.findByTelegramUserId(telegramUser.getTelegramUserId());
                database.update(telegramUserDbModel.getUserId(), telegramUser.getFirstName(), telegramUser.getLastName(), telegramUser.getPhoneNumber());
                return new TelegramUser(telegramUserDbModel.getId(), telegramUserDbModel.getTelegramUserId(), telegramUserDbModel.getFirstName(), telegramUserDbModel.getLastName(), telegramUserDbModel.getPhoneNumber());
            }else {
                throw new IllegalArgumentException(THERE_IS_NO_SUCH_A_PERSON);
            }
        }else {
            UserDbModel user = userDatabase.findByPhoneNumber(telegramUser.getPhoneNumber());
            if (user == null){
                throw new IllegalArgumentException(THERE_IS_NO_SUCH_A_PERSON);
            }else {
                TelegramUserDbModel model = database.save(new TelegramUserDbModel(user.getId(), telegramUser.getTelegramUserId(), telegramUser.getFirstName(), telegramUser.getLastName(), telegramUser.getPhoneNumber()));
                telegramUser.setId(model.getId());
                telegramUser.setTelegramUserId(model.getTelegramUserId());
                telegramUser.setUserId(user.getId());
                return telegramUser;
            }
        }
    }
    public List<TelegramUser> findAll() {
        return database.findAll().stream().map(model->new TelegramUser(model.getId(), model.getTelegramUserId(), model.getFirstName(), model.getLastName(), model.getPhoneNumber())).collect(Collectors.toList());
    }
}