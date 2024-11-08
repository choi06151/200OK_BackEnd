package com._OK._OK.User;

import com._OK._OK.Story.StoryDto;

public interface UserService {
    UserDto createUser(UserDto userDto);
    boolean isAlive(StoryDto storyDto,User user);
    void setprobability(User user);
    UserDto editWater(Long userId, int dWater);
    UserDto editFood(Long userId, int dWater);
}
