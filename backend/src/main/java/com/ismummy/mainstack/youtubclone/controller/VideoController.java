package com.ismummy.mainstack.youtubclone.controller;

import com.ismummy.mainstack.youtubclone.dto.CommentDto;
import com.ismummy.mainstack.youtubclone.dto.UploadVideoResponse;
import com.ismummy.mainstack.youtubclone.dto.VideosDto;
import com.ismummy.mainstack.youtubclone.model.Comment;
import com.ismummy.mainstack.youtubclone.service.VideosService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/videos")
@RequiredArgsConstructor
public class VideoController {

    private final VideosService videosService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UploadVideoResponse uploadVideo(@RequestParam("file") MultipartFile file) {
        return videosService.uploadVideo(file);
    }

    @PostMapping("/thumbnail")
    @ResponseStatus(HttpStatus.CREATED)
    public String uploadVideo(@RequestParam("file") MultipartFile file, @RequestParam("videoId") String videoId) {
        return videosService.uploadThumbnail(file, videoId);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public VideosDto editVideoMetadata(@RequestBody VideosDto videosDto) {
        return videosService.editVideo(videosDto);
    }

    @GetMapping("/{videoId}")
    @ResponseStatus(HttpStatus.OK)
    public VideosDto getVideoDetails(@PathVariable String videoId) {
        return videosService.getVideoDetails(videoId);
    }

    @PostMapping("/{videoId}/like")
    @ResponseStatus(HttpStatus.OK)
    public VideosDto likeVideo(@PathVariable String videoId) {
        return videosService.likeVideo(videoId);
    }

    @PostMapping("/{videoId}/dislike")
    @ResponseStatus(HttpStatus.OK)
    public VideosDto disLikeVideo(@PathVariable String videoId) {
        return videosService.disLikeVideo(videoId);
    }

    @PostMapping("/{videoId}/comment")
    @ResponseStatus(HttpStatus.OK)
    public void addComment(@PathVariable String videoId, @RequestBody CommentDto commentDto) {
        videosService.addComment(videoId, commentDto);
    }

    @GetMapping("/{videoId}/comment")
    @ResponseStatus(HttpStatus.OK)
    public List<CommentDto> getAllComments(@PathVariable String videoId) {
        return videosService.getAllComments(videoId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<VideosDto> getAllVideos(){
        return  videosService.getAllVideos();
    }
}
