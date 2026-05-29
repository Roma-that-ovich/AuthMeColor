package rto.plug.authMeColor;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSystemChatMessage;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.plugin.java.JavaPlugin;

public final class AuthMeColor extends JavaPlugin {

    @Override
    public void onLoad() {
        // Инициализируем API PacketEvents до запуска плагинов
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
        PacketEvents.getAPI().load();
    }

    @Override
    public void onEnable() {
        if (getServer().getPluginManager().getPlugin("AuthMe") == null) {
            getLogger().severe("AuthMe не найден! Отключаю AuthMeColor.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Запускаем PacketEvents и регистрируем нашего слушателя
        PacketEvents.getAPI().init();
        PacketEvents.getAPI().getEventManager().registerListener(new ChatPacketInterceptor());

        getLogger().info("AuthMeColor успешно запущен на базе PacketEvents!");
    }

    @Override
    public void onDisable() {
        PacketEvents.getAPI().terminate();
    }

    // Внутренний класс для перехвата пакетов
    private class ChatPacketInterceptor extends PacketListenerAbstract {
        @Override
        public void onPacketSend(PacketSendEvent event) {
            // Ловим пакет системного чата
            if (event.getPacketType() == PacketType.Play.Server.SYSTEM_CHAT_MESSAGE) {
                // Создаем удобную обертку для пакета
                WrapperPlayServerSystemChatMessage chatPacket = new WrapperPlayServerSystemChatMessage(event);

                // Получаем сообщение сразу в виде компонента Kyori!
                Component originalMessage = chatPacket.getMessage();
                if (originalMessage == null) return;

                // Переводим его в обычный текст, чтобы найти теги
                String plainText = PlainTextComponentSerializer.plainText().serialize(originalMessage);

                // Если находим нужные теги MiniMessage, перекрашиваем
                if (plainText.contains("<#") || plainText.contains("<gradient") || plainText.contains("<color")) {
                    try {
                        Component coloredMessage = MiniMessage.miniMessage().deserialize(plainText);
                        // Отправляем красивый компонент обратно в пакет
                        chatPacket.setMessage(coloredMessage);
                    } catch (Exception e) {
                        getLogger().warning("Ошибка рендера цвета: " + e.getMessage());
                    }
                }
            }
        }
    }
}
