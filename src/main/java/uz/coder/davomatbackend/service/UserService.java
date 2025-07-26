package uz.coder.davomatbackend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uz.coder.davomatbackend.db.UserDatabase;
import uz.coder.davomatbackend.db.model.UserDbModel;
import uz.coder.davomatbackend.model.User;

@Service
public class UserService {
    private final UserDatabase database;

    @Autowired
    public UserService(UserDatabase database) {
        this.database = database;
    }
    public User save(User user) {
        UserDbModel save = database.save(new UserDbModel(user.getFirstName(), user.getLastName(), user.getEmail(), user.getPassword(), user.getPhoneNumber(), user.getRole()));
        return new User(save.getId(), save.getFirstName(), save.getLastName(), save.getEmail(), save.getPassword(), save.getPhoneNumber(), save.getRole());
    }
    public User edit(User user) {
        database.update(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail(), user.getPassword(), user.getPhoneNumber(), user.getRole());
        UserDbModel save = database.findById(user.getId()).orElse(null);
        assert save != null;
        return new User(save.getId(), save.getFirstName(), save.getLastName(), save.getEmail(), save.getPassword(), save.getPhoneNumber(), save.getRole());
    }
    public User findById(long id) {
        UserDbModel user = database.findById(id).orElse(null);
        assert user != null;
        return new User(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail(), user.getPassword(), user.getPhoneNumber(), user.getRole());
    }
    public int deleteById(long id) {
        UserDbModel user = database.findById(id).orElse(null);
        assert user != null;
        database.delete(user);
        return 1;
    }
    public User login(String email, String password) {
        try {
            UserDbModel model = database.findByEmailAndPassword(email, password);
            assert model != null;
            return new User(model.getId(), model.getFirstName(), model.getLastName(), model.getEmail(), model.getPassword(), model.getPhoneNumber(), model.getRole());
        }catch (Exception e){
            return null;
        }
    }
    public User findByPhoneNumber(String phoneNumber) {
        try {
            UserDbModel model = database.findByPhoneNumber(phoneNumber);
            assert model != null;
            return new User(model.getId(), model.getFirstName(), model.getLastName(), model.getEmail(), model.getPassword(), model.getPhoneNumber(), model.getRole());
        }catch (Exception e){
            return null;
        }
    }
    /***
    * ROLES of the users***/
    private static final String ROLE_STUDENT = "ROLE_STUDENT";
    private static final String ROLE_TEACHER = "ROLE_TEACHER";
    private static final String ROLE_ADMIN = "ROLE_ADMIN";
}