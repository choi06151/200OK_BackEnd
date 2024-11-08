package com._OK._OK.User;

public class UserMapper {

    public static UserDto mapToUserDto(User user){
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

    public static User mapToUser(UserDto userDto){
        return new User(
                userDto.getId(),
                userDto.getName(),
                userDto.getWater(),
                userDto.getFood(),
                userDto.isAlive(),
                userDto.getProbability(),
                userDto.getDay()
        );
    }


}
