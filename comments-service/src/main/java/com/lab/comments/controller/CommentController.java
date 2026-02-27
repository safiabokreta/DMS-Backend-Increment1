package com.lab.comments.controller;

import com.lab.comments.entity.Comment;
import com.lab.comments.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comments")
public class CommentController {
    
    @Autowired
    private CommentRepository commentRepository;
    
    // GET /comments/list/{docId} - Get all comments for a specific document
    @GetMapping("/list/{docId}")
    public ResponseEntity<List<Comment>> listCommentsByDocId(@PathVariable Long docId) {
        List<Comment> comments = commentRepository.findByDocId(docId);
        return ResponseEntity.ok(comments);
    }
    
    // POST /comments/add - Add a new comment
    @PostMapping("/add")
    public ResponseEntity<Comment> addComment(@RequestBody Comment comment) {
        Comment savedComment = commentRepository.save(comment);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedComment);
    }
    
    // Optional: GET all comments (for testing)
    @GetMapping("/all")
    public ResponseEntity<List<Comment>> getAllComments() {
        return ResponseEntity.ok(commentRepository.findAll());
    }
}