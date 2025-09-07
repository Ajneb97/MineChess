package mc.ajneb97.utils;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

public class ParticleUtils {
    public static void spawnCircleParticle(Player player, String particleName, Location location, double radius) {
        int particles = 20;

        Location location1 = location.clone();
        for (int i = 0; i < particles; i++) {
            double angle, x, z;
            angle = 2 * Math.PI * i / particles;
            x = Math.cos(angle) * radius;
            z = Math.sin(angle) * radius;
            location1.add(x, 0, z);
            spawnParticle(player, particleName, location1, 0.01F,1,0,0,0);
            location1.subtract(x, 0, z);
        }
    }

    public static void spawnSquareParticle(Player player, String particleName, Location location, double radius) {
        int particlesPerSide = 7;

        Location particleLocation = location.clone();

        for (int i = 0; i < particlesPerSide; i++) {
            float ratio = (float) i / particlesPerSide;

            particleLocation.setX(location.getX() - radius + (2*radius * ratio));
            particleLocation.setZ(location.getZ() - radius);
            spawnParticle(player, particleName, particleLocation, 0.01F, 1, 0, 0, 0);

            particleLocation.setX(location.getX() + radius);
            particleLocation.setZ(location.getZ() - radius + (2*radius * ratio));
            spawnParticle(player, particleName, particleLocation, 0.01F, 1, 0, 0, 0);

            particleLocation.setX(location.getX() + radius - (2*radius * ratio));
            particleLocation.setZ(location.getZ() + radius);
            spawnParticle(player, particleName, particleLocation, 0.01F, 1, 0, 0, 0);

            particleLocation.setX(location.getX() - radius);
            particleLocation.setZ(location.getZ() + radius - (2*radius * ratio));
            spawnParticle(player, particleName, particleLocation, 0.01F, 1, 0, 0, 0);
        }
    }

    public static void spawnParticle(Player player,String particle,Location l,float speed,int amount,double offsetX,double offsetY,double offsetZ){
        try {
            if(particle.startsWith("REDSTONE;") || particle.startsWith("DUST;")) {
                String[] effectSeparated = particle.split(";");
                int red = Integer.parseInt(effectSeparated[1]);
                int green = Integer.parseInt(effectSeparated[2]);
                int blue = Integer.parseInt(effectSeparated[3]);

                Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(red,green,blue), 1);
                player.spawnParticle(Particle.valueOf(effectSeparated[0]),l,amount,offsetX,offsetY,offsetZ,speed,dustOptions);
            }else {
                player.spawnParticle(Particle.valueOf(particle),l,amount,offsetX,offsetY,offsetZ,speed);
            }
        }catch(Exception e) {

        }
    }
}
