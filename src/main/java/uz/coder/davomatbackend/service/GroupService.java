package uz.coder.davomatbackend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uz.coder.davomatbackend.db.GroupDatabase;
import uz.coder.davomatbackend.db.model.GroupDbModel;
import uz.coder.davomatbackend.model.Group;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class GroupService {
    private final GroupDatabase database;

    @Autowired
    public GroupService(GroupDatabase database) {
        this.database = database;
    }
    public Group save(Group group) {
        GroupDbModel save = database.save(new GroupDbModel(group.getTitle(), group.getCourseId()));
        return new Group(save.getId(), save.getTitle(), save.getCourseId());
    }
    public Group edit(Group group) {
        database.update(group.getId(), group.getTitle(), group.getCourseId());
        GroupDbModel save = database.findById(group.getId()).orElse(null);
        assert save != null;
        return new Group(save.getId(), save.getTitle(), save.getCourseId());
    }
    public Group findById(long id) {
        GroupDbModel group = database.findById(id).orElse(null);
        assert group != null;
        return new Group(group.getId(), group.getTitle(), group.getCourseId());
    }
    public int deleteById(long id) {
        GroupDbModel group = database.findById(id).orElse(null);
        assert group != null;
        database.delete(group);
        return 1;
    }
    public List<Group> findAllGroupByCourseId(long courseId) {
        List<GroupDbModel> allByUserId = database.findAllByCourseId(courseId);
        return allByUserId.stream().map(item -> new Group(item.getId(), item.getTitle(), item.getCourseId())).collect(Collectors.toList());
    }
}