package edu.nju.se.teamnamecannotbeempty.backend.po;

import org.hibernate.search.annotations.Field;

import javax.persistence.*;

@Entity
@Table(name = "terms")
public class Term {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    @Field
    private String content;

    public Term() {
    }

    @Override
    public String toString() {
        return "Term{" +
                "id=" + id +
                ", content='" + content + '\'' +
                '}';
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
