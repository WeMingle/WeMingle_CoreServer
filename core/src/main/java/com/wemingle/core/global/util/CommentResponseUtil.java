package com.wemingle.core.global.util;

import com.wemingle.core.domain.comment.entity.Reply;
import lombok.extern.slf4j.Slf4j;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class CommentResponseUtil<T> {
    private static final String REPLY_RETRIEVE_PATH = "/reply";
    private static final int NEXT_DATA_MARKER = 11;
    private static final int NEXT_URL_MARKER = 10;
    private final String serverIp;

    public CommentResponseUtil(String serverIp) {
        this.serverIp = serverIp;
    }

    public String createRepliesNextUrl(List<Reply> replies, Long commentPk){
        Long minReplyPk = replies.stream().map(Reply::getPk).min(Comparator.naturalOrder()).orElse(0L);
        String nextUrl = null;
        if (isExistNextUrl(replies)){
            nextUrl = serverIp + REPLY_RETRIEVE_PATH + createReplyParametersUrl(commentPk, minReplyPk);
        }

        return nextUrl;
    }

    private boolean isExistNextUrl(List<?> data){
        return data.size() == NEXT_URL_MARKER;
    }

    public boolean isExistNextData(List<?> data){
        return data.size() == NEXT_DATA_MARKER;
    }

    public List<T> removeLastDataIfExceedNextDataMarker(List<T> data){
        if (isExistNextData(data)){
            data.remove(NEXT_DATA_MARKER - 1);
        }

        return data;
    }

    private String createReplyParametersUrl(Long commentPk, Long minReplyPk) {
        HashMap<String, Object> parameters = new LinkedHashMap<>();
        parameters.put("nextIdx", minReplyPk - 1);
        parameters.put("commentPk", commentPk);

        return "?" + parameters.entrySet().stream()
                .map(parameter -> parameter.getKey() + "=" + parameter.getValue())
                .collect(Collectors.joining("&"));
    }
}