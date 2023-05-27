package com.ismummy.mainstack.youtubclone.service;

import com.ismummy.mainstack.youtubclone.dto.CommentDto;
import com.ismummy.mainstack.youtubclone.dto.UploadVideoResponse;
import com.ismummy.mainstack.youtubclone.dto.VideosDto;
import com.ismummy.mainstack.youtubclone.model.Comment;
import com.ismummy.mainstack.youtubclone.model.Video;
import com.ismummy.mainstack.youtubclone.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VideosService {

    private final S3Service s3Service;
    private final VideoRepository videoRepository;
    private final UserService userService;

    public UploadVideoResponse uploadVideo(MultipartFile file) {
        String videoUrl = s3Service.uploadFile(file);

        var video = new Video();
        video.setVideoUrl(videoUrl);

        Video savedVideo = videoRepository.save(video);

        return new UploadVideoResponse(savedVideo.getId(), savedVideo.getVideoUrl());
    }


    public VideosDto editVideo(VideosDto videosDto) {
        var video = getVideoById(videosDto.getId());

        video.setTitle(videosDto.getTitle());
        video.setDescription(videosDto.getDescription());
        video.setTags(videosDto.getTags());
        video.setThumbnailUrl(videosDto.getThumbnailUrl());
        video.setVideoStatus(videosDto.getVideoStatus());

        videoRepository.save(video);

        return videosDto;
    }

    Video getVideoById(String videoId) {
        return videoRepository.findById(videoId)
                .orElseThrow(() -> new IllegalArgumentException("Cannot find video by id - " + videoId));

    }

    public String uploadThumbnail(MultipartFile file, String videoId) {
        var video = getVideoById(videoId);

        String thumbnailUrl = s3Service.uploadFile(file);

        video.setThumbnailUrl(thumbnailUrl);

        videoRepository.save(video);

        return thumbnailUrl;
    }

    public VideosDto getVideoDetails(String videoId) {
        var video = getVideoById(videoId);

        increaseVideoCount(video);
        userService.addVideoToHistory(videoId);

        return mapToVideoDTO(video);
    }

    private void increaseVideoCount(Video video) {
        video.incrementViewCount();
        videoRepository.save(video);
    }

    public VideosDto likeVideo(String videoId) {
        var video = getVideoById(videoId);


        if (userService.ifLikedVideo(videoId)) {
            video.decrementLikes();
            userService.removeFromLikedVideo(videoId);
        } else if (userService.ifDisLikedVideo(videoId)) {
            video.decrementDislikes();
            userService.removeFromDisLikeVideo(videoId);
            video.incrementLikes();
            userService.addToLikedVideos(videoId);
        } else {
            video.incrementLikes();
            userService.addToLikedVideos(videoId);
        }

        videoRepository.save(video);

        return mapToVideoDTO(video);
    }

    public VideosDto disLikeVideo(String videoId) {
        var video = getVideoById(videoId);


        if (userService.ifDisLikedVideo(videoId)) {
            video.decrementDislikes();
            userService.removeFromDisLikeVideo(videoId);
        } else if (userService.ifLikedVideo(videoId)) {
            video.decrementLikes();
            userService.removeFromLikedVideo(videoId);
            video.incrementDislikes();
            userService.addToDisLikeVideos(videoId);
        } else {
            video.incrementDislikes();
            userService.addToDisLikeVideos(videoId);
        }

        videoRepository.save(video);

        return mapToVideoDTO(video);
    }

    private VideosDto mapToVideoDTO(Video video) {
        VideosDto videosDto = new VideosDto();
        videosDto.setVideoUrl(video.getVideoUrl());
        videosDto.setThumbnailUrl(video.getThumbnailUrl());
        videosDto.setId(video.getId());
        videosDto.setTitle(video.getTitle());
        videosDto.setDescription(video.getDescription());
        videosDto.setTags(video.getTags());
        videosDto.setVideoStatus(video.getVideoStatus());
        videosDto.setLikeCount(video.getLikes().get());
        videosDto.setDislikeCount(video.getDislikes().get());
        videosDto.setViewCount(video.getViewCount().get());
        return videosDto;
    }

    public void addComment(String videoId, CommentDto commentDto) {
        Video video = getVideoById(videoId);

        Comment comment = new Comment();
        comment.setText(commentDto.getCommentText());
        comment.setAuthorId(comment.getAuthorId());

        video.addComment(comment);

        videoRepository.save(video);
    }

    public List<CommentDto> getAllComments(String videoId) {
        Video video = getVideoById(videoId);

        List<Comment> commentList = video.getCommnetList();

        return commentList.stream().map(this::mapToCommentDto).toList();
    }

    private CommentDto mapToCommentDto(Comment comment) {
        CommentDto commentDto = new CommentDto();
        commentDto.setCommentText(comment.getText());
        commentDto.setAuthorId(comment.getAuthorId());

        return commentDto;
    }

    public List<VideosDto> getAllVideos() {
        return videoRepository.findAll().stream().map(this::mapToVideoDTO).toList();
    }
}
