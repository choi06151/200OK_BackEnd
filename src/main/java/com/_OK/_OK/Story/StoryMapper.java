package com._OK._OK.Story;

public class StoryMapper {
    public static StoryDto mapToStoryDto(Story story){
        return new StoryDto(
                story.getId(),
                story.getUser(),
                story.getContent(),
                story.getChoice1(),
                story.getChoice2(),
                story.getChoice3(),
                "",
                story.getImage(),
                0,
                0,
                story.getBeforeContent()
        );
    }

    public static Story mapToStory(StoryDto storyDto){
        return new Story(
                storyDto.getId(),
                storyDto.getUser(),
                storyDto.getBeforeContent(),
                storyDto.getContent(),
                storyDto.getChoice1(),
                storyDto.getChoice2(),
                storyDto.getChoice3(),
                storyDto.getImage()
        );
    }
}
