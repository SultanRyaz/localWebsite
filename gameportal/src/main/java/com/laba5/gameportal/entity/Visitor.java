package com.laba5.gameportal.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "visitors")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class Visitor {

    @Id
    private Long id;

    @Column(name = "count")
    private Long count;

}