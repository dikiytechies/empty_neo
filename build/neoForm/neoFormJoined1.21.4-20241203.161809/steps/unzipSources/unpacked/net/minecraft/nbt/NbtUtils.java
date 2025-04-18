package net.minecraft.nbt;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import org.slf4j.Logger;

public final class NbtUtils {
    private static final Comparator<ListTag> YXZ_LISTTAG_INT_COMPARATOR = Comparator.<ListTag>comparingInt(p_178074_ -> p_178074_.getInt(1))
        .thenComparingInt(p_178070_ -> p_178070_.getInt(0))
        .thenComparingInt(p_178066_ -> p_178066_.getInt(2));
    private static final Comparator<ListTag> YXZ_LISTTAG_DOUBLE_COMPARATOR = Comparator.<ListTag>comparingDouble(p_178060_ -> p_178060_.getDouble(1))
        .thenComparingDouble(p_178056_ -> p_178056_.getDouble(0))
        .thenComparingDouble(p_178042_ -> p_178042_.getDouble(2));
    public static final String SNBT_DATA_TAG = "data";
    private static final char PROPERTIES_START = '{';
    private static final char PROPERTIES_END = '}';
    private static final String ELEMENT_SEPARATOR = ",";
    private static final char KEY_VALUE_SEPARATOR = ':';
    private static final Splitter COMMA_SPLITTER = Splitter.on(",");
    private static final Splitter COLON_SPLITTER = Splitter.on(':').limit(2);
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int INDENT = 2;
    private static final int NOT_FOUND = -1;

    private NbtUtils() {
    }

    @VisibleForTesting
    public static boolean compareNbt(@Nullable Tag p_129236_, @Nullable Tag p_129237_, boolean p_129238_) {
        if (p_129236_ == p_129237_) {
            return true;
        } else if (p_129236_ == null) {
            return true;
        } else if (p_129237_ == null) {
            return false;
        } else if (!p_129236_.getClass().equals(p_129237_.getClass())) {
            return false;
        } else if (p_129236_ instanceof CompoundTag compoundtag) {
            CompoundTag compoundtag1 = (CompoundTag)p_129237_;
            if (compoundtag1.size() < compoundtag.size()) {
                return false;
            } else {
                for (String s : compoundtag.getAllKeys()) {
                    Tag tag2 = compoundtag.get(s);
                    if (!compareNbt(tag2, compoundtag1.get(s), p_129238_)) {
                        return false;
                    }
                }

                return true;
            }
        } else {
            if (p_129236_ instanceof ListTag listtag && p_129238_) {
                ListTag listtag1 = (ListTag)p_129237_;
                if (listtag.isEmpty()) {
                    return listtag1.isEmpty();
                }

                if (listtag1.size() < listtag.size()) {
                    return false;
                }

                for (Tag tag : listtag) {
                    boolean flag = false;

                    for (Tag tag1 : listtag1) {
                        if (compareNbt(tag, tag1, p_129238_)) {
                            flag = true;
                            break;
                        }
                    }

                    if (!flag) {
                        return false;
                    }
                }

                return true;
            }

            return p_129236_.equals(p_129237_);
        }
    }

    public static IntArrayTag createUUID(UUID p_129227_) {
        return new IntArrayTag(UUIDUtil.uuidToIntArray(p_129227_));
    }

    public static UUID loadUUID(Tag p_129234_) {
        if (p_129234_.getType() != IntArrayTag.TYPE) {
            throw new IllegalArgumentException(
                "Expected UUID-Tag to be of type " + IntArrayTag.TYPE.getName() + ", but found " + p_129234_.getType().getName() + "."
            );
        } else {
            int[] aint = ((IntArrayTag)p_129234_).getAsIntArray();
            if (aint.length != 4) {
                throw new IllegalArgumentException("Expected UUID-Array to be of length 4, but found " + aint.length + ".");
            } else {
                return UUIDUtil.uuidFromIntArray(aint);
            }
        }
    }

    public static Optional<BlockPos> readBlockPos(CompoundTag p_129240_, String p_326507_) {
        int[] aint = p_129240_.getIntArray(p_326507_);
        return aint.length == 3 ? Optional.of(new BlockPos(aint[0], aint[1], aint[2])) : Optional.empty();
    }

    public static Tag writeBlockPos(BlockPos p_129225_) {
        return new IntArrayTag(new int[]{p_129225_.getX(), p_129225_.getY(), p_129225_.getZ()});
    }

    public static BlockState readBlockState(HolderGetter<Block> p_256363_, CompoundTag p_250775_) {
        if (!p_250775_.contains("Name", 8)) {
            return Blocks.AIR.defaultBlockState();
        } else {
            ResourceLocation resourcelocation = ResourceLocation.parse(p_250775_.getString("Name"));
            Optional<? extends Holder<Block>> optional = p_256363_.get(ResourceKey.create(Registries.BLOCK, resourcelocation));
            if (optional.isEmpty()) {
                return Blocks.AIR.defaultBlockState();
            } else {
                Block block = optional.get().value();
                BlockState blockstate = block.defaultBlockState();
                if (p_250775_.contains("Properties", 10)) {
                    CompoundTag compoundtag = p_250775_.getCompound("Properties");
                    StateDefinition<Block, BlockState> statedefinition = block.getStateDefinition();

                    for (String s : compoundtag.getAllKeys()) {
                        Property<?> property = statedefinition.getProperty(s);
                        if (property != null) {
                            blockstate = setValueHelper(blockstate, property, s, compoundtag, p_250775_);
                        }
                    }
                }

                return blockstate;
            }
        }
    }

    private static <S extends StateHolder<?, S>, T extends Comparable<T>> S setValueHelper(
        S p_129205_, Property<T> p_129206_, String p_129207_, CompoundTag p_129208_, CompoundTag p_129209_
    ) {
        Optional<T> optional = p_129206_.getValue(p_129208_.getString(p_129207_));
        if (optional.isPresent()) {
            return p_129205_.setValue(p_129206_, optional.get());
        } else {
            LOGGER.warn("Unable to read property: {} with value: {} for blockstate: {}", p_129207_, p_129208_.getString(p_129207_), p_129209_);
            return p_129205_;
        }
    }

    public static CompoundTag writeBlockState(BlockState p_129203_) {
        CompoundTag compoundtag = new CompoundTag();
        compoundtag.putString("Name", BuiltInRegistries.BLOCK.getKey(p_129203_.getBlock()).toString());
        Map<Property<?>, Comparable<?>> map = p_129203_.getValues();
        if (!map.isEmpty()) {
            CompoundTag compoundtag1 = new CompoundTag();

            for (Entry<Property<?>, Comparable<?>> entry : map.entrySet()) {
                Property<?> property = entry.getKey();
                compoundtag1.putString(property.getName(), getName(property, entry.getValue()));
            }

            compoundtag.put("Properties", compoundtag1);
        }

        return compoundtag;
    }

    public static CompoundTag writeFluidState(FluidState p_178023_) {
        CompoundTag compoundtag = new CompoundTag();
        compoundtag.putString("Name", BuiltInRegistries.FLUID.getKey(p_178023_.getType()).toString());
        Map<Property<?>, Comparable<?>> map = p_178023_.getValues();
        if (!map.isEmpty()) {
            CompoundTag compoundtag1 = new CompoundTag();

            for (Entry<Property<?>, Comparable<?>> entry : map.entrySet()) {
                Property<?> property = entry.getKey();
                compoundtag1.putString(property.getName(), getName(property, entry.getValue()));
            }

            compoundtag.put("Properties", compoundtag1);
        }

        return compoundtag;
    }

    private static <T extends Comparable<T>> String getName(Property<T> p_129211_, Comparable<?> p_129212_) {
        return p_129211_.getName((T)p_129212_);
    }

    public static String prettyPrint(Tag p_178058_) {
        return prettyPrint(p_178058_, false);
    }

    public static String prettyPrint(Tag p_178051_, boolean p_178052_) {
        return prettyPrint(new StringBuilder(), p_178051_, 0, p_178052_).toString();
    }

    public static StringBuilder prettyPrint(StringBuilder p_178027_, Tag p_178028_, int p_178029_, boolean p_178030_) {
        switch (p_178028_.getId()) {
            case 0:
                break;
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 8:
                p_178027_.append(p_178028_);
                break;
            case 7:
                ByteArrayTag bytearraytag = (ByteArrayTag)p_178028_;
                byte[] abyte = bytearraytag.getAsByteArray();
                int k1 = abyte.length;
                indent(p_178029_, p_178027_).append("byte[").append(k1).append("] {\n");
                if (p_178030_) {
                    indent(p_178029_ + 1, p_178027_);

                    for (int i2 = 0; i2 < abyte.length; i2++) {
                        if (i2 != 0) {
                            p_178027_.append(',');
                        }

                        if (i2 % 16 == 0 && i2 / 16 > 0) {
                            p_178027_.append('\n');
                            if (i2 < abyte.length) {
                                indent(p_178029_ + 1, p_178027_);
                            }
                        } else if (i2 != 0) {
                            p_178027_.append(' ');
                        }

                        p_178027_.append(String.format(Locale.ROOT, "0x%02X", abyte[i2] & 255));
                    }
                } else {
                    indent(p_178029_ + 1, p_178027_).append(" // Skipped, supply withBinaryBlobs true");
                }

                p_178027_.append('\n');
                indent(p_178029_, p_178027_).append('}');
                break;
            case 9:
                ListTag listtag = (ListTag)p_178028_;
                int k = listtag.size();
                int j1 = listtag.getElementType();
                String s1 = j1 == 0 ? "undefined" : TagTypes.getType(j1).getPrettyName();
                indent(p_178029_, p_178027_).append("list<").append(s1).append(">[").append(k).append("] [");
                if (k != 0) {
                    p_178027_.append('\n');
                }

                for (int i3 = 0; i3 < k; i3++) {
                    if (i3 != 0) {
                        p_178027_.append(",\n");
                    }

                    indent(p_178029_ + 1, p_178027_);
                    prettyPrint(p_178027_, listtag.get(i3), p_178029_ + 1, p_178030_);
                }

                if (k != 0) {
                    p_178027_.append('\n');
                }

                indent(p_178029_, p_178027_).append(']');
                break;
            case 10:
                CompoundTag compoundtag = (CompoundTag)p_178028_;
                List<String> list = Lists.newArrayList(compoundtag.getAllKeys());
                Collections.sort(list);
                indent(p_178029_, p_178027_).append('{');
                if (p_178027_.length() - p_178027_.lastIndexOf("\n") > 2 * (p_178029_ + 1)) {
                    p_178027_.append('\n');
                    indent(p_178029_ + 1, p_178027_);
                }

                int i1 = list.stream().mapToInt(String::length).max().orElse(0);
                String s = Strings.repeat(" ", i1);

                for (int l2 = 0; l2 < list.size(); l2++) {
                    if (l2 != 0) {
                        p_178027_.append(",\n");
                    }

                    String s2 = list.get(l2);
                    indent(p_178029_ + 1, p_178027_).append('"').append(s2).append('"').append(s, 0, s.length() - s2.length()).append(": ");
                    prettyPrint(p_178027_, compoundtag.get(s2), p_178029_ + 1, p_178030_);
                }

                if (!list.isEmpty()) {
                    p_178027_.append('\n');
                }

                indent(p_178029_, p_178027_).append('}');
                break;
            case 11:
                IntArrayTag intarraytag = (IntArrayTag)p_178028_;
                int[] aint = intarraytag.getAsIntArray();
                int l = 0;

                for (int k3 : aint) {
                    l = Math.max(l, String.format(Locale.ROOT, "%X", k3).length());
                }

                int l1 = aint.length;
                indent(p_178029_, p_178027_).append("int[").append(l1).append("] {\n");
                if (p_178030_) {
                    indent(p_178029_ + 1, p_178027_);

                    for (int k2 = 0; k2 < aint.length; k2++) {
                        if (k2 != 0) {
                            p_178027_.append(',');
                        }

                        if (k2 % 16 == 0 && k2 / 16 > 0) {
                            p_178027_.append('\n');
                            if (k2 < aint.length) {
                                indent(p_178029_ + 1, p_178027_);
                            }
                        } else if (k2 != 0) {
                            p_178027_.append(' ');
                        }

                        p_178027_.append(String.format(Locale.ROOT, "0x%0" + l + "X", aint[k2]));
                    }
                } else {
                    indent(p_178029_ + 1, p_178027_).append(" // Skipped, supply withBinaryBlobs true");
                }

                p_178027_.append('\n');
                indent(p_178029_, p_178027_).append('}');
                break;
            case 12:
                LongArrayTag longarraytag = (LongArrayTag)p_178028_;
                long[] along = longarraytag.getAsLongArray();
                long i = 0L;

                for (long j : along) {
                    i = Math.max(i, (long)String.format(Locale.ROOT, "%X", j).length());
                }

                long j2 = (long)along.length;
                indent(p_178029_, p_178027_).append("long[").append(j2).append("] {\n");
                if (p_178030_) {
                    indent(p_178029_ + 1, p_178027_);

                    for (int j3 = 0; j3 < along.length; j3++) {
                        if (j3 != 0) {
                            p_178027_.append(',');
                        }

                        if (j3 % 16 == 0 && j3 / 16 > 0) {
                            p_178027_.append('\n');
                            if (j3 < along.length) {
                                indent(p_178029_ + 1, p_178027_);
                            }
                        } else if (j3 != 0) {
                            p_178027_.append(' ');
                        }

                        p_178027_.append(String.format(Locale.ROOT, "0x%0" + i + "X", along[j3]));
                    }
                } else {
                    indent(p_178029_ + 1, p_178027_).append(" // Skipped, supply withBinaryBlobs true");
                }

                p_178027_.append('\n');
                indent(p_178029_, p_178027_).append('}');
                break;
            default:
                p_178027_.append("<UNKNOWN :(>");
        }

        return p_178027_;
    }

    private static StringBuilder indent(int p_178020_, StringBuilder p_178021_) {
        int i = p_178021_.lastIndexOf("\n") + 1;
        int j = p_178021_.length() - i;

        for (int k = 0; k < 2 * p_178020_ - j; k++) {
            p_178021_.append(' ');
        }

        return p_178021_;
    }

    public static Component toPrettyComponent(Tag p_178062_) {
        return new TextComponentTagVisitor("").visit(p_178062_);
    }

    public static String structureToSnbt(CompoundTag p_178064_) {
        return new SnbtPrinterTagVisitor().visit(packStructureTemplate(p_178064_));
    }

    public static CompoundTag snbtToStructure(String p_178025_) throws CommandSyntaxException {
        return unpackStructureTemplate(TagParser.parseTag(p_178025_));
    }

    @VisibleForTesting
    static CompoundTag packStructureTemplate(CompoundTag p_178068_) {
        boolean flag = p_178068_.contains("palettes", 9);
        ListTag listtag;
        if (flag) {
            listtag = p_178068_.getList("palettes", 9).getList(0);
        } else {
            listtag = p_178068_.getList("palette", 10);
        }

        ListTag listtag1 = listtag.stream()
            .map(CompoundTag.class::cast)
            .map(NbtUtils::packBlockState)
            .map(StringTag::valueOf)
            .collect(Collectors.toCollection(ListTag::new));
        p_178068_.put("palette", listtag1);
        if (flag) {
            ListTag listtag2 = new ListTag();
            ListTag listtag3 = p_178068_.getList("palettes", 9);
            listtag3.stream().map(ListTag.class::cast).forEach(p_178049_ -> {
                CompoundTag compoundtag = new CompoundTag();

                for (int i = 0; i < p_178049_.size(); i++) {
                    compoundtag.putString(listtag1.getString(i), packBlockState(p_178049_.getCompound(i)));
                }

                listtag2.add(compoundtag);
            });
            p_178068_.put("palettes", listtag2);
        }

        if (p_178068_.contains("entities", 9)) {
            ListTag listtag4 = p_178068_.getList("entities", 10);
            ListTag listtag6 = listtag4.stream()
                .map(CompoundTag.class::cast)
                .sorted(Comparator.comparing(p_178080_ -> p_178080_.getList("pos", 6), YXZ_LISTTAG_DOUBLE_COMPARATOR))
                .collect(Collectors.toCollection(ListTag::new));
            p_178068_.put("entities", listtag6);
        }

        ListTag listtag5 = p_178068_.getList("blocks", 10)
            .stream()
            .map(CompoundTag.class::cast)
            .sorted(Comparator.comparing(p_178078_ -> p_178078_.getList("pos", 3), YXZ_LISTTAG_INT_COMPARATOR))
            .peek(p_178045_ -> p_178045_.putString("state", listtag1.getString(p_178045_.getInt("state"))))
            .collect(Collectors.toCollection(ListTag::new));
        p_178068_.put("data", listtag5);
        p_178068_.remove("blocks");
        return p_178068_;
    }

    @VisibleForTesting
    static CompoundTag unpackStructureTemplate(CompoundTag p_178072_) {
        ListTag listtag = p_178072_.getList("palette", 8);
        Map<String, Tag> map = listtag.stream()
            .map(StringTag.class::cast)
            .map(StringTag::getAsString)
            .collect(ImmutableMap.toImmutableMap(Function.identity(), NbtUtils::unpackBlockState));
        if (p_178072_.contains("palettes", 9)) {
            p_178072_.put(
                "palettes",
                p_178072_.getList("palettes", 10)
                    .stream()
                    .map(CompoundTag.class::cast)
                    .map(
                        p_178033_ -> map.keySet()
                                .stream()
                                .map(p_178033_::getString)
                                .map(NbtUtils::unpackBlockState)
                                .collect(Collectors.toCollection(ListTag::new))
                    )
                    .collect(Collectors.toCollection(ListTag::new))
            );
            p_178072_.remove("palette");
        } else {
            p_178072_.put("palette", map.values().stream().collect(Collectors.toCollection(ListTag::new)));
        }

        if (p_178072_.contains("data", 9)) {
            Object2IntMap<String> object2intmap = new Object2IntOpenHashMap<>();
            object2intmap.defaultReturnValue(-1);

            for (int i = 0; i < listtag.size(); i++) {
                object2intmap.put(listtag.getString(i), i);
            }

            ListTag listtag1 = p_178072_.getList("data", 10);

            for (int j = 0; j < listtag1.size(); j++) {
                CompoundTag compoundtag = listtag1.getCompound(j);
                String s = compoundtag.getString("state");
                int k = object2intmap.getInt(s);
                if (k == -1) {
                    throw new IllegalStateException("Entry " + s + " missing from palette");
                }

                compoundtag.putInt("state", k);
            }

            p_178072_.put("blocks", listtag1);
            p_178072_.remove("data");
        }

        return p_178072_;
    }

    @VisibleForTesting
    static String packBlockState(CompoundTag p_178076_) {
        StringBuilder stringbuilder = new StringBuilder(p_178076_.getString("Name"));
        if (p_178076_.contains("Properties", 10)) {
            CompoundTag compoundtag = p_178076_.getCompound("Properties");
            String s = compoundtag.getAllKeys()
                .stream()
                .sorted()
                .map(p_178036_ -> p_178036_ + ":" + compoundtag.get(p_178036_).getAsString())
                .collect(Collectors.joining(","));
            stringbuilder.append('{').append(s).append('}');
        }

        return stringbuilder.toString();
    }

    @VisibleForTesting
    static CompoundTag unpackBlockState(String p_178054_) {
        CompoundTag compoundtag = new CompoundTag();
        int i = p_178054_.indexOf(123);
        String s;
        if (i >= 0) {
            s = p_178054_.substring(0, i);
            CompoundTag compoundtag1 = new CompoundTag();
            if (i + 2 <= p_178054_.length()) {
                String s1 = p_178054_.substring(i + 1, p_178054_.indexOf(125, i));
                COMMA_SPLITTER.split(s1).forEach(p_178040_ -> {
                    List<String> list = COLON_SPLITTER.splitToList(p_178040_);
                    if (list.size() == 2) {
                        compoundtag1.putString(list.get(0), list.get(1));
                    } else {
                        LOGGER.error("Something went wrong parsing: '{}' -- incorrect gamedata!", p_178054_);
                    }
                });
                compoundtag.put("Properties", compoundtag1);
            }
        } else {
            s = p_178054_;
        }

        compoundtag.putString("Name", s);
        return compoundtag;
    }

    public static CompoundTag addCurrentDataVersion(CompoundTag p_265050_) {
        int i = SharedConstants.getCurrentVersion().getDataVersion().getVersion();
        return addDataVersion(p_265050_, i);
    }

    public static CompoundTag addDataVersion(CompoundTag p_265534_, int p_265686_) {
        p_265534_.putInt("DataVersion", p_265686_);
        return p_265534_;
    }

    public static int getDataVersion(CompoundTag p_265397_, int p_265399_) {
        return p_265397_.contains("DataVersion", 99) ? p_265397_.getInt("DataVersion") : p_265399_;
    }
}
