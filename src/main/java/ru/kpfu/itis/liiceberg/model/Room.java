package ru.kpfu.itis.liiceberg.model;

import lombok.*;

import javax.persistence.*;


@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
@Entity
@Table(name = "rooms")
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 6)
    private String code;

    @Column
    private Long datetime;
    @Column(nullable = false)
    private Integer capacity;
    @Column
    private Integer category;
    @Column
    private String difficulty;

}
