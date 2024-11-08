package com._OK._OK.User;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class UserDto {
    private Long id;
    private String name;
    private int water;
    private int food;
    private boolean alive = true;
    private int probability = 1;
    private int day = 1;
public static UserDto createUserDto(User user){
return new UserDto(
        user.getId(),
        user.getName(),
        user.getWater(),
        user.getFood(),
        user.isAlive(),
        user.getProbability(),
        user.getDay()
);
}
}
