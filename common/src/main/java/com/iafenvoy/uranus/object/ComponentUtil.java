package com.iafenvoy.uranus.object;

import com.google.common.collect.Lists;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.List;

@Environment(EnvType.CLIENT)
public class ComponentUtil {
    public static List<MutableText> splitText(MutableText arg_chatText, int chatWidth, TextRenderer fontRenderer) {
        int i = 0;
        MutableText ichatcomponent = Text.literal("");
        List<MutableText> list = Lists.newArrayList();
        List<MutableText> chatComponents = Lists.newArrayList(arg_chatText);

        for (int j = 0; j < chatComponents.size(); ++j) {
            MutableText currentChatComponent = chatComponents.get(j);
            String unformattedText = currentChatComponent.getContent().toString();
            boolean addToList = false;

            if (unformattedText.contains("\n")) {
                int k = unformattedText.indexOf(10);
                String s1 = unformattedText.substring(k + 1);
                unformattedText = unformattedText.substring(0, k + 1);
                MutableText chatcomponenttext = Text.literal(getColorCode(unformattedText) + s1);
                chatcomponenttext.setStyle(currentChatComponent.getStyle());
                chatComponents.add(j + 1, chatcomponenttext);
                addToList = true;
            }
            String textRemovedLastNewline = unformattedText.endsWith("\n") ? unformattedText.substring(0, unformattedText.length() - 1) : unformattedText;
            int textWidth = fontRenderer.getWidth(textRemovedLastNewline);
            MutableText newChatComponent = Text.literal(textRemovedLastNewline);
            newChatComponent.setStyle(currentChatComponent.getStyle());

            if (i + textWidth > chatWidth) {
                String s2 = fontRenderer.trimToWidth(unformattedText, chatWidth - i, false);
                String s3 = s2.length() < unformattedText.length() ? unformattedText.substring(s2.length()) : null;
                if (s3 != null) {
                    int l = s2.lastIndexOf(" ");
                    if (l >= 0 && fontRenderer.getWidth(unformattedText.substring(0, l)) > 0) {
                        s2 = unformattedText.substring(0, l);
                        s3 = unformattedText.substring(l);
                    } else if (i > 0 && !unformattedText.contains(" ")) {
                        s2 = "";
                        s3 = unformattedText;
                    }
                    MutableText chatcomponenttext2 = Text.literal(getColorCode(s2) + s3);
                    chatcomponenttext2.setStyle(currentChatComponent.getStyle());
                    chatComponents.add(j + 1, chatcomponenttext2);
                }
                textWidth = fontRenderer.getWidth(s2);
                newChatComponent = Text.literal(s2);
                newChatComponent.setStyle(currentChatComponent.getStyle());
                addToList = true;
            }
            if (i + textWidth <= chatWidth) {
                i += textWidth;
                ichatcomponent.append(newChatComponent);
            } else
                addToList = true;
            if (addToList) {
                list.add(ichatcomponent);
                i = 0;
                ichatcomponent = Text.literal("");
            }
        }
        list.add(ichatcomponent);
        return list;
    }

    public static String getColorCode(String s) {
        String color = "";
        StringBuilder format = new StringBuilder();
        char last = 0;
        for (char c : s.toCharArray()) {
            if (last == '§') {
                if (c == 'r' || ('0' <= c && c <= 'f')) {
                    color = "§" + c;
                    format = new StringBuilder();
                } else if ('k' <= c && c <= 'o') {
                    format.append("§").append(c);
                }
            }
            last = c;
        }
        return color + format;
    }
}

