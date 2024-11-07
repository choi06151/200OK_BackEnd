package com._OK._OK.Story;

public class StoryMapper {
    public static StoryDto mapToStoryDto(Story story){
        return new StoryDto(
                story.getId(),
                story.getUser(),
                "",
                "",
                "",
                "",
                "",
                null,
                0,
                0,
                story.getBeforeContent()
        );
    }

    public static Story mapToStory(StoryDto storyDto){
        return new Story(
                storyDto.getId(),
                storyDto.getUser(),
                storyDto.getBeforeContent()
        );
    }
}
