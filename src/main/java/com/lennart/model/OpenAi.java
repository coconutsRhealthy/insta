package com.lennart.model;

import io.github.sashirestela.openai.SimpleOpenAI;
import io.github.sashirestela.openai.domain.chat.ChatRequest;
import io.github.sashirestela.openai.domain.chat.message.ChatMsgSystem;
import io.github.sashirestela.openai.domain.chat.message.ChatMsgUser;

import java.util.List;

public class OpenAi {

    public String identifyInstaProfileCountry(String chatmessage) {
        var openai = SimpleOpenAI.builder()
                .apiKey("secret")
                .build();

        var chatRequest = ChatRequest.builder()
                .model("gpt-3.5-turbo-1106")
                .messages(List.of(
                        new ChatMsgSystem("You should identify from which country an instagram account most likely is, " +
                                "given its recent captions, its recenty used hashtags, its recent location tags and its bio." +
                                " Pay specific attention to usage of non English language. This is an important tell where " +
                                "someone is from. Please answer just with the name of the most likely country, with the" +
                                " abbreviation of the country behind it (so Netherlands should be 'Netherlands, NL', " +
                                "Mexico 'Mexico, MX' etc). Nothing more."),
                        new ChatMsgUser(chatmessage)))
                .temperature(0.0)
                .maxTokens(300)
                .build();
        var futureChat = openai.chatCompletions().create(chatRequest);
        var chatResponse = futureChat.join();
        return chatResponse.firstContent();
    }

    public String identifyDiscountCodesFromCaptions(String chatmessage) {
        var openai = SimpleOpenAI.builder()
                .apiKey("secret")
                .build();

        var chatRequest = ChatRequest.builder()
                .model("gpt-3.5-turbo-1106")
                .messages(List.of(
                        new ChatMsgSystem("You will receive a bunch of instagram post captions and their urls. You should" +
                                "scan each caption and check if a discount code is being offered. The caption can be in any" +
                                "language. In case you identify a discount code, try to determine for which company it is being" +
                                "offered, and what is the discount percentage. The data you will receive will be structured as " +
                                "follows: **** caption: (the caption) url: (the url of the post) ****. Your reply should be" +
                                "a list of all discount codes spotted, with (if possible) the company and discount percentage " +
                                "and then the url."),
                        new ChatMsgUser(chatmessage)))
                .temperature(0.0)
                .maxTokens(4096)
                .build();
        var futureChat = openai.chatCompletions().create(chatRequest);
        var chatResponse = futureChat.join();
        return chatResponse.firstContent();
    }

    public String isTiktokProfileDutch(String chatmessage) {
        var openai = SimpleOpenAI.builder()
                .apiKey("secret")
                .build();

        var chatRequest = ChatRequest.builder()
                .model("gpt-3.5-turbo-1106")
                .messages(List.of(
                        new ChatMsgSystem("You will receive information of a Tiktok account, namely the username, bio " +
                                "and one or a few captions. Based on this you should try to establish where the Tiktok " +
                                "account is from. Please answer just with the name of the most likely country, with " +
                                "the abbreviation of the country behind it (so Netherlands should be 'Netherlands, NL', " +
                                "Mexico 'Mexico, MX' etc). Nothing more."),
                        new ChatMsgUser(chatmessage)))
                .temperature(0.0)
                .maxTokens(300)
                .build();
        var futureChat = openai.chatCompletions().create(chatRequest);
        var chatResponse = futureChat.join();
        return chatResponse.firstContent();
    }
}
