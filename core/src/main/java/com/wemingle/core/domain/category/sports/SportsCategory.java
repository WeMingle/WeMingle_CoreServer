package com.wemingle.core.domain.category.sports;

import jakarta.persistence.*;

@Entity
public class SportsCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "sprots_name")
    private String sportsName;

}