/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap$Builder
 */
package net.runelite.client.plugins.interfacestyles;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import net.runelite.client.game.SpriteOverride;
import net.runelite.client.plugins.interfacestyles.Skin;

enum HealthbarOverride implements SpriteOverride
{
    DEFAULT_FRONT_30PX(2176, "default_front_40px.png"),
    DEFAULT_FRONT_40PX(2431, "default_front_40px.png"),
    DEFAULT_FRONT_50PX(2178, "default_front_50px.png"),
    DEFAULT_FRONT_60PX(2180, "default_front_60px.png"),
    DEFAULT_FRONT_70PX(2971, "default_front_70px.png"),
    DEFAULT_FRONT_80PX(2182, "default_front_80px.png"),
    DEFAULT_FRONT_90PX(5609, "default_front_90px.png"),
    DEFAULT_FRONT_100PX(2184, "default_front_100px.png"),
    DEFAULT_FRONT_120PX(2186, "default_front_120px.png"),
    DEFAULT_FRONT_140PX(2188, "default_front_140px.png"),
    DEFAULT_FRONT_160PX(2190, "default_front_160px.png"),
    DEFAULT_BACK_30PX(2177, "default_back_40px.png"),
    DEFAULT_BACK_40PX(2432, "default_back_40px.png"),
    DEFAULT_BACK_50PX(2179, "default_back_50px.png"),
    DEFAULT_BACK_60PX(2181, "default_back_60px.png"),
    DEFAULT_BACK_70PX(2972, "default_back_70px.png"),
    DEFAULT_BACK_80PX(2183, "default_back_80px.png"),
    DEFAULT_BACK_90PX(5610, "default_back_90px.png"),
    DEFAULT_BACK_100PX(2185, "default_back_100px.png"),
    DEFAULT_BACK_120PX(2187, "default_back_120px.png"),
    DEFAULT_BACK_140PX(2189, "default_back_140px.png"),
    DEFAULT_BACK_160PX(2191, "default_back_160px.png"),
    CYAN_FRONT_30PX(2433, "cyan_front_40px.png"),
    CYAN_FRONT_40PX(2435, "cyan_front_40px.png"),
    CYAN_FRONT_50PX(2437, "cyan_front_50px.png"),
    CYAN_FRONT_60PX(2439, "cyan_front_60px.png"),
    CYAN_FRONT_70PX(2973, "cyan_front_70px.png"),
    CYAN_FRONT_80PX(2441, "cyan_front_80px.png"),
    CYAN_FRONT_90PX(5611, "cyan_front_90px.png"),
    CYAN_FRONT_100PX(2443, "cyan_front_100px.png"),
    CYAN_FRONT_120PX(2445, "cyan_front_120px.png"),
    CYAN_FRONT_140PX(2447, "cyan_front_140px.png"),
    CYAN_FRONT_160PX(2449, "cyan_front_160px.png"),
    CYAN_BACK_30PX(2434, "cyan_back_40px.png"),
    CYAN_BACK_40PX(2436, "cyan_back_40px.png"),
    CYAN_BACK_50PX(2438, "cyan_back_50px.png"),
    CYAN_BACK_60PX(2440, "cyan_back_60px.png"),
    CYAN_BACK_70PX(2974, "cyan_back_70px.png"),
    CYAN_BACK_80PX(2442, "cyan_back_80px.png"),
    CYAN_BACK_90PX(5612, "cyan_back_90px.png"),
    CYAN_BACK_100PX(2444, "cyan_back_100px.png"),
    CYAN_BACK_120PX(2446, "cyan_back_120px.png"),
    CYAN_BACK_140PX(2448, "cyan_back_140px.png"),
    CYAN_BACK_160PX(2450, "cyan_back_160px.png"),
    ORANGE_FRONT_30PX(2451, "orange_front_40px.png"),
    ORANGE_FRONT_40PX(2453, "orange_front_40px.png"),
    ORANGE_FRONT_50PX(2455, "orange_front_50px.png"),
    ORANGE_FRONT_60PX(2457, "orange_front_60px.png"),
    ORANGE_FRONT_70PX(2975, "orange_front_70px.png"),
    ORANGE_FRONT_80PX(2459, "orange_front_80px.png"),
    ORANGE_FRONT_90PX(5613, "orange_front_90px.png"),
    ORANGE_FRONT_100PX(2461, "orange_front_100px.png"),
    ORANGE_FRONT_120PX(2463, "orange_front_120px.png"),
    ORANGE_FRONT_140PX(2465, "orange_front_140px.png"),
    ORANGE_FRONT_160PX(2467, "orange_front_160px.png"),
    ORANGE_BACK_30PX(2452, "orange_back_40px.png"),
    ORANGE_BACK_40PX(2454, "orange_back_40px.png"),
    ORANGE_BACK_50PX(2456, "orange_back_50px.png"),
    ORANGE_BACK_60PX(2458, "orange_back_60px.png"),
    ORANGE_BACK_70PX(2976, "orange_back_70px.png"),
    ORANGE_BACK_80PX(2460, "orange_back_80px.png"),
    ORANGE_BACK_90PX(5614, "orange_back_90px.png"),
    ORANGE_BACK_100PX(2462, "orange_back_100px.png"),
    ORANGE_BACK_120PX(2464, "orange_back_120px.png"),
    ORANGE_BACK_140PX(2466, "orange_back_140px.png"),
    ORANGE_BACK_160PX(2468, "orange_back_160px.png"),
    YELLOW_FRONT_30PX(2469, "yellow_front_40px.png"),
    YELLOW_FRONT_40PX(2471, "yellow_front_40px.png"),
    YELLOW_FRONT_50PX(2473, "yellow_front_50px.png"),
    YELLOW_FRONT_60PX(2475, "yellow_front_60px.png"),
    YELLOW_FRONT_70PX(2977, "yellow_front_70px.png"),
    YELLOW_FRONT_80PX(2477, "yellow_front_80px.png"),
    YELLOW_FRONT_90PX(5615, "yellow_front_90px.png"),
    YELLOW_FRONT_100PX(2479, "yellow_front_100px.png"),
    YELLOW_FRONT_120PX(2481, "yellow_front_120px.png"),
    YELLOW_FRONT_140PX(2483, "yellow_front_140px.png"),
    YELLOW_FRONT_160PX(2485, "yellow_front_160px.png"),
    YELLOW_BACK_30PX(2470, "yellow_back_40px.png"),
    YELLOW_BACK_40PX(2472, "yellow_back_40px.png"),
    YELLOW_BACK_50PX(2474, "yellow_back_50px.png"),
    YELLOW_BACK_60PX(2476, "yellow_back_60px.png"),
    YELLOW_BACK_70PX(2978, "yellow_back_70px.png"),
    YELLOW_BACK_80PX(2478, "yellow_back_80px.png"),
    YELLOW_BACK_90PX(5616, "yellow_back_90px.png"),
    YELLOW_BACK_100PX(2480, "yellow_back_100px.png"),
    YELLOW_BACK_120PX(2482, "yellow_back_120px.png"),
    YELLOW_BACK_140PX(2484, "yellow_back_140px.png"),
    YELLOW_BACK_160PX(2486, "yellow_back_160px.png"),
    PURPLE_FRONT_30PX(4708, "purple_front_40px.png"),
    PURPLE_FRONT_40PX(4710, "purple_front_40px.png"),
    PURPLE_FRONT_50PX(4712, "purple_front_50px.png"),
    PURPLE_FRONT_60PX(4714, "purple_front_60px.png"),
    PURPLE_FRONT_70PX(4716, "purple_front_70px.png"),
    PURPLE_FRONT_80PX(4718, "purple_front_80px.png"),
    PURPLE_FRONT_90PX(5617, "purple_front_90px.png"),
    PURPLE_FRONT_100PX(4720, "purple_front_100px.png"),
    PURPLE_FRONT_120PX(4722, "purple_front_120px.png"),
    PURPLE_FRONT_140PX(4724, "purple_front_140px.png"),
    PURPLE_FRONT_160PX(4726, "purple_front_160px.png"),
    PURPLE_BACK_30PX(4709, "purple_back_40px.png"),
    PURPLE_BACK_40PX(4711, "purple_back_40px.png"),
    PURPLE_BACK_50PX(4713, "purple_back_50px.png"),
    PURPLE_BACK_60PX(4715, "purple_back_60px.png"),
    PURPLE_BACK_70PX(4717, "purple_back_70px.png"),
    PURPLE_BACK_80PX(4719, "purple_back_80px.png"),
    PURPLE_BACK_90PX(5618, "purple_back_90px.png"),
    PURPLE_BACK_100PX(4721, "purple_back_100px.png"),
    PURPLE_BACK_120PX(4723, "purple_back_120px.png"),
    PURPLE_BACK_140PX(4725, "purple_back_140px.png"),
    PURPLE_BACK_160PX(4727, "purple_back_160px.png"),
    GREEN_FRONT_30PX(4771, "green_front_40px.png"),
    GREEN_FRONT_40PX(4773, "green_front_40px.png"),
    GREEN_FRONT_50PX(4775, "green_front_50px.png"),
    GREEN_FRONT_60PX(4777, "green_front_60px.png"),
    GREEN_FRONT_70PX(4779, "green_front_70px.png"),
    GREEN_FRONT_80PX(4781, "green_front_80px.png"),
    GREEN_FRONT_90PX(5619, "green_front_90px.png"),
    GREEN_FRONT_100PX(4783, "green_front_100px.png"),
    GREEN_FRONT_120PX(4785, "green_front_120px.png"),
    GREEN_FRONT_140PX(4787, "green_front_140px.png"),
    GREEN_FRONT_160PX(4789, "green_front_160px.png"),
    GREEN_BACK_30PX(4772, "green_back_40px.png"),
    GREEN_BACK_40PX(4774, "green_back_40px.png"),
    GREEN_BACK_50PX(4776, "green_back_50px.png"),
    GREEN_BACK_60PX(4778, "green_back_60px.png"),
    GREEN_BACK_70PX(4780, "green_back_70px.png"),
    GREEN_BACK_80PX(4782, "green_back_80px.png"),
    GREEN_BACK_90PX(5620, "green_back_90px.png"),
    GREEN_BACK_100PX(4784, "green_back_100px.png"),
    GREEN_BACK_120PX(4786, "green_back_120px.png"),
    GREEN_BACK_140PX(4788, "green_back_140px.png"),
    GREEN_BACK_160PX(4790, "green_back_160px.png"),
    BLUE_FRONT_50PX(2967, "blue_front_50px.png"),
    BLUE_BACK_50PX(2968, "blue_back_50px.png"),
    COX_GREEN(1415, "cox_green.png"),
    COX_BLUE(1416, "cox_blue.png"),
    COX_YELLOW(1417, "cox_yellow.png"),
    COX_RED(1418, "cox_red.png"),
    BLOOD_MOON_FRONT_120PX(5579, "blood_moon_front_120px.png"),
    BLOOD_MOON_BACK_120PX(5580, "blood_moon_back_120px.png"),
    BLUE_MOON_FRONT_120PX(5581, "blue_moon_front_120px.png"),
    BLUE_MOON_BACK_120PX(5582, "blue_moon_back_120px.png"),
    ECLIPSE_MOON_FRONT_120PX(5583, "eclipse_moon_front_120px.png"),
    ECLIPSE_MOON_BACK_120PX(5584, "eclipse_moon_back_120px.png"),
    BLOOD_MOON_FRONT_90PX(5644, "blood_moon_front_90px.png"),
    BLOOD_MOON_BACK_90PX(5645, "blood_moon_back_90px.png"),
    BLUE_MOON_FRONT_90PX(5646, "blue_moon_front_90px.png"),
    BLUE_MOON_BACK_90PX(5647, "blue_moon_back_90px.png"),
    ECLIPSE_MOON_FRONT_90PX(5648, "eclipse_moon_front_90px.png"),
    ECLIPSE_MOON_BACK_90PX(5649, "eclipse_moon_back_90px.png"),
    LAST_STAND_FRONT(5863, "last_stand_front.png"),
    LAST_STAND_BACK(5864, "last_stand_back.png");

    private final int spriteId;
    private final String fileName;
    private final int padding = 1;
    private static final Map<Integer, HealthbarOverride> MAP;

    static HealthbarOverride get(int spriteID) {
        return MAP.get(spriteID);
    }

    @Override
    public String getFileName() {
        return Skin.AROUND_2010.toString() + "/healthbar/" + this.fileName;
    }

    private HealthbarOverride(int spriteId, String fileName) {
        this.spriteId = spriteId;
        this.fileName = fileName;
    }

    @Override
    public int getSpriteId() {
        return this.spriteId;
    }

    public int getPadding() {
        return this.padding;
    }

    static {
        ImmutableMap.Builder builder = new ImmutableMap.Builder();
        for (HealthbarOverride override : HealthbarOverride.values()) {
            builder.put((Object)override.spriteId, (Object)override);
        }
        MAP = builder.build();
    }
}

