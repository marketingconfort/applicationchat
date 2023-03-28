package com.tupinamba.springbootwebsocket.controller;

import com.tupinamba.springbootwebsocket.model.ChatMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
public class ChatController {

    @MessageMapping("/chat.register")
    @SendTo("/topic/public")
    public ChatMessage register(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
        return chatMessage;
    }

    @MessageMapping("/chat.send")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
        if(!validateMessage(chatMessage.getContent())) {
            return chatMessage;
        }
        return null;
    }

    public boolean validateMessage(String message) {
        // Vérification du numéro de téléphone
        Pattern phonePattern = Pattern.compile("(\\+\\d{1,3}[- ]?)?\\d{10}");
        Matcher phoneMatcher = phonePattern.matcher(message);
        if (phoneMatcher.find()) {
            return true;
        }

        // Vérification de l'e-mail
        Pattern emailPattern = Pattern.compile("\\b[\\w.%-]+@[-.\\w]+\\.[A-Za-z]{2,4}\\b");
        Matcher emailMatcher = emailPattern.matcher(message);
        if (emailMatcher.find()) {
            return true;
        }

        // Vérification du RIB
        Pattern ribPattern = Pattern.compile("\\b[FR]{2}[0-9]{2}\\s?\\d{5}\\s?\\w{5}\\s?\\d{11}\\b");
        Matcher ribMatcher = ribPattern.matcher(message);
        if (ribMatcher.find()) {
            return true;
        }

        // Si aucun des motifs ci-dessus n'est trouvé, le message ne contient pas de numéro de téléphone, d'e-mail ou de RIB valide
        return false;
    }
}