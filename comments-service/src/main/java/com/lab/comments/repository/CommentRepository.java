package com.lab.comments.repository;

import com.lab.comments.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    // Custom query method to find comments by document ID
    List<Comment> findByDocId(Long docId);
}