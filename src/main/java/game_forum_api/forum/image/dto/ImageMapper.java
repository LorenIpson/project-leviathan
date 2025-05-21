package game_forum_api.forum.image.dto;

import game_forum_api.forum.image.model.CommentImages;
import game_forum_api.forum.image.model.PostImages;

public class ImageMapper {

    public static PostImageResponse toPostImageResponse(PostImages postImage) {
        return new PostImageResponse(
                postImage.getId(),
                postImage.getPost().getId(),
                postImage.getImageUrl(),
                postImage.getDeleteHash()
        );
    }

    public static CommentImageResponse toCommentImageResponse(CommentImages commentImage) {
        return new CommentImageResponse(
                commentImage.getId(),
                commentImage.getComment().getPost().getId(),
                commentImage.getImageUrl(),
                commentImage.getDeleteHash()
        );
    }

}
