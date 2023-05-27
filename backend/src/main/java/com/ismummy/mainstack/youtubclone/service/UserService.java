package com.ismummy.mainstack.youtubclone.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ismummy.mainstack.youtubclone.dto.UserInfoDto;
import com.ismummy.mainstack.youtubclone.model.User;
import com.ismummy.mainstack.youtubclone.model.Video;
import com.ismummy.mainstack.youtubclone.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Value("${autho0.userinfoEndpoint}")
    private String userInfoEndpoint;

    public String registerUser(String tokenValue) {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(userInfoEndpoint))
                .setHeader("Authorization", String.format("Bearer %s", tokenValue))
                .build();

        HttpClient httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .build();

        try {
            HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            UserInfoDto userInfoDto = objectMapper.readValue(httpResponse.body(), UserInfoDto.class);

            Optional<User> existingUser = userRepository.findBySub(userInfoDto.getSub());
            if (existingUser.isPresent()) {
                return existingUser.get().getId();
            } else {

                User user = new User();
                user.setFirstname(userInfoDto.getGivenName());
                user.setLastname(userInfoDto.getFamilyName());
                user.setFullname(userInfoDto.getName());
                user.setEmailAddress(userInfoDto.getEmail());
                user.setSub(userInfoDto.getSub());

                return userRepository.save(user).getId();
            }

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Exception occurred while registring user", e);
        }
    }

    public User getCurrentUser() {
        String sub = ((Jwt) (SecurityContextHolder.getContext().getAuthentication().getPrincipal())).getClaim("sub");

        return userRepository.findBySub(sub)
                .orElseThrow(() -> new IllegalArgumentException("Cannot find user with sub - " + sub));
    }

    public void addToLikedVideos(String videoId) {
        User currentUser = getCurrentUser();
        currentUser.addToLikedVideos(videoId);

        userRepository.save(currentUser);
    }

    public boolean ifLikedVideo(String videoId) {
        return getCurrentUser().getLikedVideos()
                .stream()
                .anyMatch(likedVideo -> likedVideo.equals(videoId));
    }

    public boolean ifDisLikedVideo(String videoId) {
        return getCurrentUser().getDisLikedVideos()
                .stream()
                .anyMatch(disLikedVideo -> disLikedVideo.equals(videoId));
    }

    public void removeFromLikedVideo(String videoId) {
        User currentUser = getCurrentUser();
        currentUser.removeFromLikedVideos(videoId);

        userRepository.save(currentUser);
    }

    public void removeFromDisLikeVideo(String videoId) {
        User currentUser = getCurrentUser();
        currentUser.removeFromDisLikedVideos(videoId);

        userRepository.save(currentUser);
    }

    public void addToDisLikeVideos(String videoId) {
        User currentUser = getCurrentUser();
        currentUser.addToDisLikedVideos(videoId);

        userRepository.save(currentUser);
    }

    public void addVideoToHistory(String videoId) {
        User currentUser = getCurrentUser();

        currentUser.addToVideoHistory(videoId);

        userRepository.save(currentUser);
    }

    public void subscribeUser(String userId) {
        User currentUser = getCurrentUser();
        currentUser.addToSubscribeToUsers(userId);

        User targetUser = getUserById(userId);
        targetUser.addToSubscribers(currentUser.getId());

        userRepository.save(currentUser);
        userRepository.save(targetUser);
    }

    public void unSubscribeUser(String userId) {
        User currentUser = getCurrentUser();
        currentUser.removeFromSubscribeToUsers(userId);

        User targetUser = getUserById(userId);
        targetUser.removeFromSubscribers(currentUser.getId());

        userRepository.save(currentUser);
        userRepository.save(targetUser);
    }

    public User getUserById(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User with the id not found!"));
    }

    public Set<String> userHistory(String userId) {
        User user = getUserById(userId);

        return user.getVideoHistory();
    }
}
