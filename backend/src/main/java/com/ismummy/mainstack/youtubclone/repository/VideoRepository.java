package com.ismummy.mainstack.youtubclone.repository;

import com.ismummy.mainstack.youtubclone.model.Video;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface VideoRepository  extends MongoRepository<Video, String> {
}
