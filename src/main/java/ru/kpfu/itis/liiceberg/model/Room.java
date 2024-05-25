package ru.kpfu.itis.liiceberg.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.Set;


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

    @Setter
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToMany
    @JsonIgnore
    @JoinTable(
            name = "user_room",
            joinColumns = @JoinColumn(name = "room_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id")
    )
    private Set<User> users;
}
