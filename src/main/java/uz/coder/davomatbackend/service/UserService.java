package uz.coder.davomatbackend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uz.coder.davomatbackend.db.UserDatabase;
import uz.coder.davomatbackend.db.model.UserDbModel;
import uz.coder.davomatbackend.model.Balance;
import uz.coder.davomatbackend.model.User;
import java.time.LocalDate;

import static uz.coder.davomatbackend.todo.Strings.*;

@Service
public class UserService {
    private final UserDatabase database;

    @Autowired
    public UserService(UserDatabase database) {
        this.database = database;
    }
    public User save(User user) {
        LocalDate now = LocalDate.now();
        LocalDate balance;
        if (user.getRole().equals(ROLE_STUDENT)) {
            balance = now.plusWeeks(1);
        }else {
            balance = null;
        }
        UserDbModel save = database.save(new UserDbModel(user.getFirstName(), user.getLastName(), user.getEmail(), user.getPassword(), user.getPhoneNumber(), user.getRole(), balance));
        return new User(save.getId(), save.getFirstName(), save.getLastName(), save.getEmail(), save.getPassword(), save.getPhoneNumber(), save.getRole(), save.getPayedDate());
    }
    public User edit(User user) {
        database.update(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail(), user.getPassword(), user.getPhoneNumber(), user.getRole());
        UserDbModel save = database.findById(user.getId()).orElseThrow(()->new IllegalArgumentException(THERE_IS_NO_SUCH_A_PERSON));
        assert save != null;
        return new User(save.getId(), save.getFirstName(), save.getLastName(), save.getEmail(), save.getPassword(), save.getPhoneNumber(), save.getRole(), save.getPayedDate());
    }
    public User findById(long id) {
        UserDbModel user = database.findById(id).orElseThrow(()->new IllegalArgumentException(THERE_IS_NO_SUCH_A_PERSON));
        assert user != null;
        return new User(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail(), user.getPassword(), user.getPhoneNumber(), user.getRole(), user.getPayedDate());
    }
    public int deleteById(long id) {
        if (database.existsById(id)){
            database.deleteById(id);
            return 1;
        }else {
            return 0;
        }
    }
    public User login(String email, String password) {
        try {
            UserDbModel model = database.findByEmailAndPassword(email, password);
            assert model != null;
            return new User(model.getId(), model.getFirstName(), model.getLastName(), model.getEmail(), model.getPassword(), model.getPhoneNumber(), model.getRole(), model.getPayedDate());
        }catch (Exception e){
            return null;
        }
    }
    public User findByPhoneNumber(String phoneNumber) {
        try {
            UserDbModel model = database.findByPhoneNumber(phoneNumber);
            assert model != null;
            return new User(model.getId(), model.getFirstName(), model.getLastName(), model.getEmail(), model.getPassword(), model.getPhoneNumber(), model.getRole(), model.getPayedDate());
        }catch (Exception e){
            return null;
        }
    }
    public User updateBalanceUser(Balance balance){
        long id = balance.getId();
        LocalDate payDate = balance.getDate();
        User user = findById(id);
        if (user != null) {
            if (user.getRole().equals(ROLE_STUDENT)){
                UserDbModel model = database.updateBalanceUser(payDate, id);
                assert model != null;
                return new User(model.getId(), model.getFirstName(), model.getLastName(), model.getEmail(), model.getPassword(), model.getPhoneNumber(), model.getRole(), model.getPayedDate());
            }else {
                throw new IllegalArgumentException(YOU_ARE_NOT_A_STUDENT);
            }
        }else {
            throw new IllegalArgumentException(THERE_IS_NO_SUCH_A_PERSON);
        }
    }
}