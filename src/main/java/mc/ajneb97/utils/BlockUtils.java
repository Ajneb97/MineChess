package mc.ajneb97.utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import mc.ajneb97.MineChess;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;

import java.lang.reflect.Field;
import java.net.URL;
import java.util.Base64;
import java.util.UUID;

public class BlockUtils {
    public static void setHeadTextureData(Block block, String texture, String owner){
        Skull skullBlock = (Skull) block.getState();
        if(owner != null){
            skullBlock.setOwner(owner);
            skullBlock.update();
            return;
        }

        ServerVersion serverVersion = MineChess.serverVersion;
        if(serverVersion.serverVersionGreaterEqualThan(serverVersion,ServerVersion.v1_20_R2)){
            PlayerProfile profile = Bukkit.createPlayerProfile(UUID.randomUUID(),"minechess");
            PlayerTextures textures = profile.getTextures();
            URL url;
            try {
                String decoded = new String(Base64.getDecoder().decode(texture));
                String decodedFormatted = decoded.replaceAll("\\s", "");
                JsonObject jsonObject = new Gson().fromJson(decodedFormatted, JsonObject.class);
                String urlText = jsonObject.get("textures").getAsJsonObject().get("SKIN")
                        .getAsJsonObject().get("url").getAsString();

                url = new URL(urlText);
            } catch (Exception error) {
                error.printStackTrace();
                return;
            }
            textures.setSkin(url);
            profile.setTextures(textures);
            skullBlock.setOwnerProfile(profile);
        }else{
            GameProfile profile = new GameProfile(UUID.randomUUID(), "");
            profile.getProperties().put("textures", new Property("textures", texture));
            try {
                Field profileField = skullBlock.getClass().getDeclaredField("profile");
                profileField.setAccessible(true);
                profileField.set(skullBlock, profile);
            } catch (IllegalArgumentException | NoSuchFieldException | SecurityException | IllegalAccessException error) {
                error.printStackTrace();
            }
        }

        skullBlock.update();
    }
}
