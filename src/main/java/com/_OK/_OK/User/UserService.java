package com._OK._OK.User;

import com._OK._OK.Story.StoryDto;

public interface UserService {
    UserDto createUser(UserDto userDto);
    boolean isAlive(StoryDto storyDto);
}
