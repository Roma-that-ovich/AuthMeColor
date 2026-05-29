package rto.plug.authMeColor;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.plugin.java.JavaPlugin;

public final class AuthMeColor extends JavaPlugin {

    private ProtocolManager protocolManager;

    @Override
    public void onEnable() {
        if (getServer().getPluginManager().getPlugin("AuthMe") == null) {
            getLogger().severe("AuthMe не найден! Отключаю AuthMeColor.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        protocolManager = ProtocolLibrary.getProtocolManager();
        registerChatInterceptor();

        getLogger().info("AuthMeColor запущен! Движок MiniMessage активирован.");
    }

    private void registerChatInterceptor() {
        protocolManager.addPacketListener(new PacketAdapter(this, ListenerPriority.NORMAL, PacketType.Play.Server.SYSTEM_CHAT) {
            @Override
            public void onPacketSending(PacketEvent event) {
                WrappedChatComponent chatComponent = event.getPacket().getChatComponents().readSafely(0);
                if (chatComponent != null) {
                    String json = chatComponent.getJson();

                    // Проверяем, есть ли в сыром JSON наши теги
                    if (json != null && (json.contains("<#") || json.contains("<gradient") || json.contains("<color"))) {
                        try {
                            // 1. Читаем кривой JSON от AuthMe и достаем из него чистый текст
                            Component original = GsonComponentSerializer.gson().deserialize(json);
                            String plainText = PlainTextComponentSerializer.plainText().serialize(original);

                            // 2. Пропускаем чистый текст через парсер MiniMessage
                            Component coloredComponent = MiniMessage.miniMessage().deserialize(plainText);

                            // 3. Упаковываем обратно в правильный современный JSON формат с цветами
                            String newJson = GsonComponentSerializer.gson().serialize(coloredComponent);

                            // 4. Заменяем пакет и отправляем игроку красивый текст
                            chatComponent.setJson(newJson);
                            event.getPacket().getChatComponents().write(0, chatComponent);
                        } catch (Exception e) {
                            // Если что-то пошло не так, сервер не упадет, а просто выведет оригинальное сообщение
                            getLogger().warning("Не удалось покрасить сообщение: " + e.getMessage());
                        }
                    }
                }
            }
        });
    }
}