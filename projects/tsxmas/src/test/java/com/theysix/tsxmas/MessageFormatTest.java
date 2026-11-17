package com.theysix.tsxmas;

import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MessageFormatTest {
    @Test void configuredGradientDoesNotParsePlayerTextAsMarkup() {
        var message=MessageFormat.format("<gradient:#B3000C:#E40010:#D8D8D8:#1FD537:#00B32C>[TheySix]</gradient> ","<red>literal</red>");
        assertEquals("[TheySix] <red>literal</red>",PlainTextComponentSerializer.plainText().serialize(message));
    }
    @Test void oldPlainPrefixesStillWork() {
        assertEquals("[TheySix] Hello",PlainTextComponentSerializer.plainText().serialize(MessageFormat.format("[TheySix] ","Hello")));
    }
    @Test void existingHexAndLegacyPrefixesRemainReadable() {
        assertEquals("SERVER Hello",PlainTextComponentSerializer.plainText().serialize(MessageFormat.format("&#B3000C&lSERVER ","Hello")));
    }
}
