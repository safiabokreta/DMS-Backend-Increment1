package com.lab.comments.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "comments")
public class Comment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long docId;
    
    @Column(nullable = false, length = 500)
    private String content;
    
    // Default Constructor
    public Comment() {
    }
    
    // Parameterized Constructor
    public Comment(Long docId, String content) {
        this.docId = docId;
        this.content = content;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getDocId() {
        return docId;
    }
    
    public void setDocId(Long docId) {
        this.docId = docId;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
}