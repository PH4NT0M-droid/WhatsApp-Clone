package com.whatsappclone.services;

import com.whatsappclone.models.GroupChat;
import com.whatsappclone.models.Message;
import com.whatsappclone.models.User;
import com.whatsappclone.repositories.GroupChatRepository;
import com.whatsappclone.repositories.MessageRepository;
import com.whatsappclone.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final GroupChatRepository groupChatRepository;

    // Directory where media files will be saved (configure in application.properties)
    @Value("${uploads.chat:C:/uploads/chat}")
    private String chatUploadPath;

    public MessageService(MessageRepository messageRepository, UserRepository userRepository, GroupChatRepository groupChatRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.groupChatRepository = groupChatRepository;
    }

    // Sends a one-to-one text message.
    public Message sendMessage(Long senderId, Long recipientId, String content) {
        Optional<User> senderOpt = userRepository.findById(senderId);
        Optional<User> recipientOpt = userRepository.findById(recipientId);
        if (senderOpt.isEmpty() || recipientOpt.isEmpty()) {
            throw new RuntimeException("Sender or recipient not found");
        }
        Message message = new Message();
        message.setSender(senderOpt.get());
        message.setRecipient(recipientOpt.get());
        message.setContent(content);
        message.setTimestamp(new Date());
        return messageRepository.save(message);
    }

    // Sends a one-to-one media message.
    public Message sendMediaMessage(Long senderId, Long recipientId, String content, MultipartFile mediaFile, String mediaType) {
        Optional<User> senderOpt = userRepository.findById(senderId);
        Optional<User> recipientOpt = userRepository.findById(recipientId);
        if (senderOpt.isEmpty() || recipientOpt.isEmpty()) {
            throw new RuntimeException("Sender or recipient not found");
        }
        Message message = new Message();
        message.setSender(senderOpt.get());
        message.setRecipient(recipientOpt.get());
        message.setContent(content);
        message.setTimestamp(new Date());

        if (mediaFile != null && !mediaFile.isEmpty()) {
            // Generate a unique filename
            String fileName = System.currentTimeMillis() + "_" + mediaFile.getOriginalFilename();
            File uploadDir = new File(chatUploadPath);
            if (!uploadDir.exists()) {
                if (!uploadDir.mkdirs()) {
                    throw new RuntimeException("Failed to create directory for chat media");
                }
            }
            File dest = new File(uploadDir, fileName);
            try {
                mediaFile.transferTo(dest);
                message.setMediaUrl("/uploads/chat/" + fileName);
                message.setMediaType(mediaType);
            } catch (IOException e) {
                throw new RuntimeException("Failed to save media file", e);
            }
        }
        return messageRepository.save(message);
    }


    // Helper method to upload media and return its URL.
    public String uploadMedia(MultipartFile file) {
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        File uploadDir = new File(chatUploadPath);
        if (!uploadDir.exists() && !uploadDir.mkdirs()) {
            throw new RuntimeException("Failed to create directory for chat media");
        }
        File dest = new File(uploadDir, fileName);
        try {
            file.transferTo(dest);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save media file", e);
        }
        // Return the relative URL where the file is accessible.
        // (Ensure your static resource mapping allows serving files from this directory.)
        return "/uploads/chat/" + fileName;
    }

    // Retrieves one-to-one chat history.
    public List<Message> getChatHistory(Long userId1, Long userId2) {
        Optional<User> user1Opt = userRepository.findById(userId1);
        Optional<User> user2Opt = userRepository.findById(userId2);
        if (user1Opt.isEmpty() || user2Opt.isEmpty()) {
            throw new RuntimeException("User not found");
        }
        User user1 = user1Opt.get();
        User user2 = user2Opt.get();
        return messageRepository.findBySenderAndRecipientOrSenderAndRecipientOrderByTimestampAsc(user1, user2, user2, user1);
    }

    // Retrieves the group chat history ordered by timestamp.
    public List<Message> getGroupChatHistory(Long groupId) {
        Optional<GroupChat> groupOpt = groupChatRepository.findById(groupId);
        if (groupOpt.isEmpty()) {
            throw new RuntimeException("Group not found");
        }
        // Ensure that the MessageRepository has a method:
        // List<Message> findByGroupChatOrderByTimestampAsc(GroupChat groupChat);
        return messageRepository.findByGroupChatOrderByTimestampAsc(groupOpt.get());
    }

    // Sends a group message. For group messages, no recipient is set – only the groupChat field.
    public Message sendGroupMessage(Long senderId, Long groupId, String content, String mediaUrl, String mediaType) {
        Optional<User> senderOpt = userRepository.findById(senderId);
        if (senderOpt.isEmpty()) {
            throw new RuntimeException("Sender not found");
        }
        Optional<GroupChat> groupOpt = groupChatRepository.findById(groupId);
        if (groupOpt.isEmpty()) {
            throw new RuntimeException("Group not found");
        }
        Message message = new Message();
        message.setSender(senderOpt.get());

        // Retrieve the group and force initialization of lazy properties to avoid JSON serialization issues.
        GroupChat group = groupOpt.get();
        group.getOwner().getEmail(); // Access a property to force lazy-loading

        message.setGroupChat(group);
        message.setContent(content);
        message.setTimestamp(new Date());

        // If a media URL and type are provided, set them.
        if (mediaUrl != null && !mediaUrl.trim().isEmpty() && mediaType != null && !mediaType.trim().isEmpty()) {
            message.setMediaUrl(mediaUrl);
            message.setMediaType(mediaType);
        }
        return messageRepository.save(message);
    }

}
