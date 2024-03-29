package com.wemingle.core.domain.nickname.service.nicknamecleaner;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public class NicknameCleaner implements BannedWords {

    private final List<Pattern> badWordPatterns = new ArrayList<>();

    public NicknameCleaner() {
        Set<String> bannedWordsSet = new HashSet<>(List.of(bannedWords));
        bannedWordsSet.forEach(bannedWord -> badWordPatterns.add(Pattern.compile(String.join(buildPatternText(),bannedWord.split("")))));
    }
    private String buildPatternText() {
        StringBuilder delimiterBuilder = new StringBuilder("[");
        for (String delimiter : delimiters) {
            delimiterBuilder.append(Pattern.quote(delimiter));
        }
        delimiterBuilder.append("]*");
        return delimiterBuilder.toString();
    }

    public boolean isAvailableNickname(String nickname) {
        return isCleanNickname(nickname) && isValidLengthNickname(nickname);
    }

    public boolean isCleanNickname(String nickname) {
        return badWordPatterns.stream().noneMatch(pattern -> pattern.matcher(nickname).find());
    }

    public boolean isValidLengthNickname(String nickname) {
        nickname = nickname.replace(" ", "");
        Pattern pattern = Pattern.compile(".{2,9}");
        return pattern.matcher(nickname).matches();
    }


}
