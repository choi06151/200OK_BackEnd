package com._OK._OK.Story;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StotyServiceImpl implements StroyService {
    @Autowired
    StoryRepository storyRepository;
    @Override
    public StoryDto saveStory(StoryDto storyDto) {
        Story story = StoryMapper.mapToStory(storyDto);
        Story savedStory = storyRepository.save(story);
        return StoryMapper.mapToStoryDto(savedStory);
    }
}
