package com.theysix.tsxmas;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

final class MessageFormat {
    static Component format(String prefix,String body) {
        Component configured=prefix.contains("<")?MiniMessage.miniMessage().deserialize(prefix):
                LegacyComponentSerializer.builder().character('&').hexColors().build().deserialize(prefix);
        return configured
                .append(Component.text(body,TextColor.fromHexString("#D8D8D8")));
    }
}
