package uz.coder.davomatbackend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.coder.davomatbackend.model.Group;
import uz.coder.davomatbackend.model.Response;
import uz.coder.davomatbackend.service.GroupService;

import java.util.List;

@RestController
@RequestMapping("/api/group")
public class GroupController {
    private final GroupService service;

    @Autowired
    public GroupController(GroupService service) {
        this.service = service;
    }

    @PostMapping("/addGroup")
    public ResponseEntity<Response<Group>> addGroup(@RequestBody Group group) {
        try {
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new Response<>(200, service.save(group)));
        }catch (Exception ex){
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new Response<>(500, ex.getMessage()));
        }
    }
    @PutMapping("/editGroup")
    public ResponseEntity<Response<Group>> editGroup(@RequestBody Group group) {
        try {
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new Response<>(200, service.edit(group)));
        }catch (Exception ex){
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new Response<>(500, ex.getMessage()));
        }
    }
    @DeleteMapping("/deleteGroup/{id}")
    public ResponseEntity<Response<Integer>> deleteGroup(@PathVariable("id") long id) {
        try {
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new Response<>(200, service.deleteById(id)));
        } catch (Exception e) {
            return  ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new Response<>(500, e.getMessage()));
        }
    }
    @GetMapping("/{id}")
    public ResponseEntity<Response<Group>> findById(@PathVariable("id") long id) {
        try {
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new Response<>(200, service.findById(id)));
        }catch (Exception ex){
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new Response<>(500, ex.getMessage()));
        }
    }
    @GetMapping("/findByCourseId/{courseId}")
    public ResponseEntity<Response<List<Group>>> findByCourseId(@PathVariable("courseId") long courseId) {
        try {
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new Response<>(200, service.findAllGroupByCourseId(courseId)));
        }catch (Exception ex){
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new Response<>(500, ex.getMessage()));
        }
    }
}
