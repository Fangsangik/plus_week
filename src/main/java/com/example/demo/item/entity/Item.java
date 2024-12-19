package com.example.demo.item.entity;

import com.example.demo.user.entity.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.DynamicInsert;


@Entity
@Getter
@DynamicInsert // sql에서 null인 필드는 insert하지 않는다.
// TODO: 6. Dynamic Insert
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    @ManyToOne
    @JoinColumn(name = "manager_id")
    private User manager;

    @Column(nullable = false, columnDefinition = "varchar(20) default 'PENDING'")
    private String status;

    public Item(String name, String description, User manager, User owner) {
        this.name = name;
        this.description = description;
        this.manager = manager;
        this.owner = owner;
    }

    @Builder
    public Item(String name, String description, User owner, User manager, String status) {
        this.name = name;
        this.description = description;
        this.owner = owner;
        this.manager = manager;
        this.status = status;
    }

    public Item() {}
}
