package com._OK._OK.User;

import com._OK._OK.Story.StoryDto;

import java.util.List;

public interface UserService {
    UserDto createUser(UserDto userDto);
    boolean isAlive(StoryDto storyDto,User user);
    void setProbability(User user);
    UserDto editWater(Long userId, int dWater);
    UserDto editFood(Long userId, int dWater);
    List<User> getSortedUsers();
}
