package com._OK._OK.User;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity

public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String name;
    @Column
    private int water;
    @Column
    private int food;
    @Column
    private int hp = 10;
    @Column(nullable = false)
    private boolean alive = true;
    @Column
    private int probability = 1;
    @Column
    private int day;
    public void setWater(int water) {
        if (water < 0) this.water = 0;

    }
    public void setFood(int food){
        if (food<0)this.food = 0;
    }

}
