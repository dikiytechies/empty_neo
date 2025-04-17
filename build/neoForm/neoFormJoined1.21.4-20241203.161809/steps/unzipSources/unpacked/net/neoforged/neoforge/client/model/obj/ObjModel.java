/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.neoforged.neoforge.client.model.obj;

import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.mojang.math.Transformation;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import joptsimple.internal.Strings;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.block.model.TextureSlots;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.phys.Vec2;
import net.neoforged.neoforge.client.RenderTypeGroup;
import net.neoforged.neoforge.client.model.AbstractUnbakedModel;
import net.neoforged.neoforge.client.model.NeoForgeModelProperties;
import net.neoforged.neoforge.client.model.StandardModelParameters;
import net.neoforged.neoforge.client.model.pipeline.QuadBakingVertexConsumer;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import org.joml.Vector4f;

/**
 * A model loaded from an OBJ file.
 * <p>
 * Supports positions, texture coordinates, normals and colors. The {@link ObjMaterialLibrary material library}
 * has support for numerous features, including support for {@link ResourceLocation} textures (non-standard).
 */
public class ObjModel extends AbstractUnbakedModel {
    private static final Vector4f COLOR_WHITE = new Vector4f(1, 1, 1, 1);
    private static final Vec2[] DEFAULT_COORDS = {
            new Vec2(0, 0),
            new Vec2(0, 1),
            new Vec2(1, 1),
            new Vec2(1, 0),
    };

    private final Multimap<String, ModelGroup> parts = MultimapBuilder.linkedHashKeys().arrayListValues().build();

    private final List<Vector3f> positions = Lists.newArrayList();
    private final List<Vec2> texCoords = Lists.newArrayList();
    private final List<Vector3f> normals = Lists.newArrayList();
    private final List<Vector4f> colors = Lists.newArrayList();

    public final boolean automaticCulling;
    public final boolean shadeQuads;
    public final boolean flipV;
    public final boolean emissiveAmbient;
    @Nullable
    public final String mtlOverride;

    public final ResourceLocation modelLocation;

    private ObjModel(ModelSettings settings) {
        super(settings.parameters);
        this.modelLocation = settings.modelLocation;
        this.automaticCulling = settings.automaticCulling;
        this.shadeQuads = settings.shadeQuads;
        this.flipV = settings.flipV;
        this.emissiveAmbient = settings.emissiveAmbient;
        this.mtlOverride = settings.mtlOverride;
    }

    public static ObjModel parse(ObjTokenizer tokenizer, ModelSettings settings) throws IOException {
        var modelLocation = settings.modelLocation;
        var materialLibraryOverrideLocation = settings.mtlOverride;
        var model = new ObjModel(settings);

        // for relative references to material libraries
        String modelDomain = modelLocation.getNamespace();
        String modelPath = modelLocation.getPath();
        int lastSlash = modelPath.lastIndexOf('/');
        if (lastSlash >= 0)
            modelPath = modelPath.substring(0, lastSlash + 1); // include the '/'
        else
            modelPath = "";

        ObjMaterialLibrary mtllib = ObjMaterialLibrary.EMPTY;
        ObjMaterialLibrary.Material currentMat = null;
        String currentSmoothingGroup = null;
        ModelGroup currentGroup = null;
        ModelObject currentObject = null;
        ModelMesh currentMesh = null;

        boolean objAboveGroup = false;

        if (materialLibraryOverrideLocation != null) {
            String lib = materialLibraryOverrideLocation;
            if (lib.contains(":"))
                mtllib = ObjLoader.INSTANCE.loadMaterialLibrary(ResourceLocation.parse(lib));
            else
                mtllib = ObjLoader.INSTANCE.loadMaterialLibrary(ResourceLocation.fromNamespaceAndPath(modelDomain, modelPath + lib));
        }

        String[] line;
        while ((line = tokenizer.readAndSplitLine(true)) != null) {
            switch (line[0]) {
                case "mtllib": // Loads material library
                {
                    if (materialLibraryOverrideLocation != null)
                        break;

                    String lib = line[1];
                    if (lib.contains(":"))
                        mtllib = ObjLoader.INSTANCE.loadMaterialLibrary(ResourceLocation.parse(lib));
                    else
                        mtllib = ObjLoader.INSTANCE.loadMaterialLibrary(ResourceLocation.fromNamespaceAndPath(modelDomain, modelPath + lib));
                    break;
                }

                case "usemtl": // Sets the current material (starts new mesh)
                {
                    String mat = Strings.join(Arrays.copyOfRange(line, 1, line.length), " ");
                    ObjMaterialLibrary.Material newMat = mtllib.getMaterial(mat);
                    if (!Objects.equals(newMat, currentMat)) {
                        currentMat = newMat;
                        if (currentMesh != null && currentMesh.mat == null && currentMesh.faces.size() == 0) {
                            currentMesh.mat = currentMat;
                        } else {
                            // Start new mesh
                            currentMesh = null;
                        }
                    }
                    break;
                }

                case "v": // Vertex
                    model.positions.add(parseVector4To3(line));
                    break;
                case "vt": // Vertex texcoord
                    model.texCoords.add(parseVector2(line));
                    break;
                case "vn": // Vertex normal
                    model.normals.add(parseVector3(line));
                    break;
                case "vc": // Vertex color (non-standard)
                    model.colors.add(parseVector4(line));
                    break;

                case "f": // Face
                {
                    if (currentMesh == null) {
                        currentMesh = model.new ModelMesh(currentMat, currentSmoothingGroup);
                        if (currentObject != null) {
                            currentObject.meshes.add(currentMesh);
                        } else {
                            if (currentGroup == null) {
                                currentGroup = model.new ModelGroup("");
                                model.parts.put("", currentGroup);
                            }
                            currentGroup.meshes.add(currentMesh);
                        }
                    }

                    int[][] vertices = new int[line.length - 1][];
                    for (int i = 0; i < vertices.length; i++) {
                        String vertexData = line[i + 1];
                        String[] vertexParts = vertexData.split("/");
                        int[] vertex = Arrays.stream(vertexParts).mapToInt(num -> Strings.isNullOrEmpty(num) ? 0 : Integer.parseInt(num)).toArray();
                        if (vertex[0] < 0) vertex[0] = model.positions.size() + vertex[0];
                        else vertex[0]--;
                        if (vertex.length > 1) {
                            if (vertex[1] < 0) vertex[1] = model.texCoords.size() + vertex[1];
                            else vertex[1]--;
                            if (vertex.length > 2) {
                                if (vertex[2] < 0) vertex[2] = model.normals.size() + vertex[2];
                                else vertex[2]--;
                                if (vertex.length > 3) {
                                    if (vertex[3] < 0) vertex[3] = model.colors.size() + vertex[3];
                                    else vertex[3]--;
                                }
                            }
                        }
                        vertices[i] = vertex;
                    }

                    currentMesh.faces.add(vertices);

                    break;
                }

                case "s": // Smoothing group (starts new mesh)
                {
                    String smoothingGroup = "off".equals(line[1]) ? null : line[1];
                    if (!Objects.equals(currentSmoothingGroup, smoothingGroup)) {
                        currentSmoothingGroup = smoothingGroup;
                        if (currentMesh != null && currentMesh.smoothingGroup == null && currentMesh.faces.size() == 0) {
                            currentMesh.smoothingGroup = currentSmoothingGroup;
                        } else {
                            // Start new mesh
                            currentMesh = null;
                        }
                    }
                    break;
                }

                case "g": {
                    String name = line[1];
                    if (objAboveGroup) {
                        currentObject = model.new ModelObject(currentGroup.name() + "/" + name);
                        currentGroup.parts.put(name, currentObject);
                    } else {
                        currentGroup = model.new ModelGroup(name);
                        model.parts.put(name, currentGroup);
                        currentObject = null;
                    }
                    // Start new mesh
                    currentMesh = null;
                    break;
                }

                case "o": {
                    String name = line[1];
                    if (objAboveGroup || currentGroup == null) {
                        objAboveGroup = true;

                        currentGroup = model.new ModelGroup(name);
                        model.parts.put(name, currentGroup);
                        currentObject = null;
                    } else {
                        currentObject = model.new ModelObject(currentGroup.name() + "/" + name);
                        currentGroup.parts.put(name, currentObject);
                    }
                    // Start new mesh
                    currentMesh = null;
                    break;
                }
            }
        }
        return model;
    }

    private static Vector3f parseVector4To3(String[] line) {
        Vector4f vec4 = parseVector4(line);
        return new Vector3f(
                vec4.x() / vec4.w(),
                vec4.y() / vec4.w(),
                vec4.z() / vec4.w());
    }

    private static Vec2 parseVector2(String[] line) {
        return switch (line.length) {
            case 1 -> new Vec2(0, 0);
            case 2 -> new Vec2(Float.parseFloat(line[1]), 0);
            default -> new Vec2(Float.parseFloat(line[1]), Float.parseFloat(line[2]));
        };
    }

    private static Vector3f parseVector3(String[] line) {
        return switch (line.length) {
            case 1 -> new Vector3f();
            case 2 -> new Vector3f(Float.parseFloat(line[1]), 0, 0);
            case 3 -> new Vector3f(Float.parseFloat(line[1]), Float.parseFloat(line[2]), 0);
            default -> new Vector3f(Float.parseFloat(line[1]), Float.parseFloat(line[2]), Float.parseFloat(line[3]));
        };
    }

    static Vector4f parseVector4(String[] line) {
        return switch (line.length) {
            case 1 -> new Vector4f();
            case 2 -> new Vector4f(Float.parseFloat(line[1]), 0, 0, 1);
            case 3 -> new Vector4f(Float.parseFloat(line[1]), Float.parseFloat(line[2]), 0, 1);
            case 4 -> new Vector4f(Float.parseFloat(line[1]), Float.parseFloat(line[2]), Float.parseFloat(line[3]), 1);
            default -> new Vector4f(Float.parseFloat(line[1]), Float.parseFloat(line[2]), Float.parseFloat(line[3]), Float.parseFloat(line[4]));
        };
    }

    private Pair<BakedQuad, Direction> makeQuad(int[][] indices, int tintIndex, Vector4f colorTint, Vector4f ambientColor, TextureAtlasSprite texture, Transformation transform) {
        boolean needsNormalRecalculation = false;
        for (int[] ints : indices) {
            needsNormalRecalculation |= ints.length < 3;
        }
        Vector3f faceNormal = new Vector3f();
        if (needsNormalRecalculation) {
            Vector3f a = positions.get(indices[0][0]);
            Vector3f ab = positions.get(indices[1][0]);
            Vector3f ac = positions.get(indices[2][0]);
            Vector3f abs = new Vector3f(ab);
            abs.sub(a);
            Vector3f acs = new Vector3f(ac);
            acs.sub(a);
            abs.cross(acs);
            abs.normalize();
            faceNormal = abs;
        }

        var quadBaker = new QuadBakingVertexConsumer();

        quadBaker.setSprite(texture);
        quadBaker.setTintIndex(tintIndex);

        int uv2 = 0;
        if (emissiveAmbient) {
            int fakeLight = (int) ((ambientColor.x() + ambientColor.y() + ambientColor.z()) * 15 / 3.0f);
            uv2 = LightTexture.pack(fakeLight, fakeLight);
            quadBaker.setShade(fakeLight == 0 && shadeQuads);
        } else {
            quadBaker.setShade(shadeQuads);
        }

        boolean hasTransform = !transform.isIdentity();
        // The incoming transform is referenced on the center of the block, but our coords are referenced on the corner
        Transformation transformation = hasTransform ? transform.blockCenterToCorner() : transform;

        Vector4f[] pos = new Vector4f[4];
        Vector3f[] norm = new Vector3f[4];

        for (int i = 0; i < 4; i++) {
            int[] index = indices[Math.min(i, indices.length - 1)];
            Vector4f position = new Vector4f(positions.get(index[0]), 1);
            Vec2 texCoord = index.length >= 2 && texCoords.size() > 0 ? texCoords.get(index[1]) : DEFAULT_COORDS[i];
            Vector3f norm0 = !needsNormalRecalculation && index.length >= 3 && normals.size() > 0 ? normals.get(index[2]) : faceNormal;
            Vector3f normal = norm0;
            Vector4f color = index.length >= 4 && colors.size() > 0 ? colors.get(index[3]) : COLOR_WHITE;
            if (hasTransform) {
                normal = new Vector3f(norm0);
                transformation.transformPosition(position);
                transformation.transformNormal(normal);
            }
            Vector4f tintedColor = new Vector4f(
                    color.x() * colorTint.x(),
                    color.y() * colorTint.y(),
                    color.z() * colorTint.z(),
                    color.w() * colorTint.w());
            quadBaker.addVertex(position.x(), position.y(), position.z());
            quadBaker.setColor(tintedColor.x(), tintedColor.y(), tintedColor.z(), tintedColor.w());
            quadBaker.setUv(
                    texture.getU(texCoord.x),
                    texture.getV((flipV ? 1 - texCoord.y : texCoord.y)));
            quadBaker.setLight(uv2);
            quadBaker.setNormal(normal.x(), normal.y(), normal.z());
            if (i == 0) {
                quadBaker.setDirection(Direction.getApproximateNearest(normal.x(), normal.y(), normal.z()));
            }
            pos[i] = position;
            norm[i] = normal;
        }

        Direction cull = null;
        if (automaticCulling) {
            if (Mth.equal(pos[0].x(), 0) && // vertex.position.x
                    Mth.equal(pos[1].x(), 0) &&
                    Mth.equal(pos[2].x(), 0) &&
                    Mth.equal(pos[3].x(), 0) &&
                    norm[0].x() < 0) // vertex.normal.x
            {
                cull = Direction.WEST;
            } else if (Mth.equal(pos[0].x(), 1) && // vertex.position.x
                    Mth.equal(pos[1].x(), 1) &&
                    Mth.equal(pos[2].x(), 1) &&
                    Mth.equal(pos[3].x(), 1) &&
                    norm[0].x() > 0) // vertex.normal.x
            {
                cull = Direction.EAST;
            } else if (Mth.equal(pos[0].z(), 0) && // vertex.position.z
                    Mth.equal(pos[1].z(), 0) &&
                    Mth.equal(pos[2].z(), 0) &&
                    Mth.equal(pos[3].z(), 0) &&
                    norm[0].z() < 0) // vertex.normal.z
            {
                cull = Direction.NORTH; // can never remember
            } else if (Mth.equal(pos[0].z(), 1) && // vertex.position.z
                    Mth.equal(pos[1].z(), 1) &&
                    Mth.equal(pos[2].z(), 1) &&
                    Mth.equal(pos[3].z(), 1) &&
                    norm[0].z() > 0) // vertex.normal.z
            {
                cull = Direction.SOUTH;
            } else if (Mth.equal(pos[0].y(), 0) && // vertex.position.y
                    Mth.equal(pos[1].y(), 0) &&
                    Mth.equal(pos[2].y(), 0) &&
                    Mth.equal(pos[3].y(), 0) &&
                    norm[0].y() < 0) // vertex.normal.z
            {
                cull = Direction.DOWN; // can never remember
            } else if (Mth.equal(pos[0].y(), 1) && // vertex.position.y
                    Mth.equal(pos[1].y(), 1) &&
                    Mth.equal(pos[2].y(), 1) &&
                    Mth.equal(pos[3].y(), 1) &&
                    norm[0].y() > 0) // vertex.normal.y
            {
                cull = Direction.UP;
            }
        }

        return Pair.of(quadBaker.bakeQuad(), cull);
    }

    @Override
    public BakedModel bake(TextureSlots slots, ModelBaker baker, ModelState state, boolean useAmbientOcclusion, boolean usesBlockLight, ItemTransforms transforms, ContextMap additionalProperties) {
        Map<String, Boolean> partVisibility = additionalProperties.getOrDefault(NeoForgeModelProperties.PART_VISIBILITY, Map.of());
        var builder = new SimpleBakedModel.Builder(useAmbientOcclusion, usesBlockLight, true, transforms);
        builder.particle(baker.findSprite(slots, TextureSlot.PARTICLE.getId()));
        parts.values().stream().filter(part -> partVisibility.getOrDefault(part.name(), true))
                .forEach(part -> part.addQuads(builder, slots, baker, state, useAmbientOcclusion, usesBlockLight, transforms, additionalProperties));
        return builder.build(additionalProperties.getOrDefault(NeoForgeModelProperties.RENDER_TYPE, RenderTypeGroup.EMPTY));
    }

    public class ModelObject {
        public final String name;

        List<ModelMesh> meshes = Lists.newArrayList();

        ModelObject(String name) {
            this.name = name;
        }

        public String name() {
            return name;
        }

        public void addQuads(SimpleBakedModel.Builder builder, TextureSlots slots, ModelBaker baker, ModelState state, boolean useAmbientOcclusion, boolean usesBlockLight, ItemTransforms transforms, ContextMap additionalProperties) {
            for (ModelMesh mesh : meshes) {
                mesh.addQuads(builder, slots, baker, state, useAmbientOcclusion, usesBlockLight, transforms, additionalProperties);
            }
        }

        protected void addNamesRecursively(Set<String> names) {
            names.add(name());
        }
    }

    public class ModelGroup extends ModelObject {
        final Multimap<String, ModelObject> parts = MultimapBuilder.linkedHashKeys().arrayListValues().build();

        ModelGroup(String name) {
            super(name);
        }

        @Override
        public void addQuads(SimpleBakedModel.Builder builder, TextureSlots slots, ModelBaker baker, ModelState state, boolean useAmbientOcclusion, boolean usesBlockLight, ItemTransforms transforms, ContextMap additionalProperties) {
            super.addQuads(builder, slots, baker, state, useAmbientOcclusion, usesBlockLight, transforms, additionalProperties);

            Map<String, Boolean> partVisibility = additionalProperties.getOrDefault(NeoForgeModelProperties.PART_VISIBILITY, Map.of());
            parts.values().stream().filter(part -> partVisibility.getOrDefault("%s.%s".formatted(name(), part.name()), true))
                    .forEach(part -> part.addQuads(builder, slots, baker, state, useAmbientOcclusion, usesBlockLight, transforms, additionalProperties));
        }

        @Override
        protected void addNamesRecursively(Set<String> names) {
            super.addNamesRecursively(names);
            for (ModelObject object : parts.values())
                object.addNamesRecursively(names);
        }
    }

    private class ModelMesh {
        @Nullable
        public ObjMaterialLibrary.Material mat;
        @Nullable
        public String smoothingGroup;
        public final List<int[][]> faces = Lists.newArrayList();

        public ModelMesh(@Nullable ObjMaterialLibrary.Material currentMat, @Nullable String currentSmoothingGroup) {
            this.mat = currentMat;
            this.smoothingGroup = currentSmoothingGroup;
        }

        public void addQuads(SimpleBakedModel.Builder builder, TextureSlots slots, ModelBaker baker, ModelState state, boolean useAmbientOcclusion, boolean usesBlockLight, ItemTransforms transforms, ContextMap additionalProperties) {
            if (mat == null)
                return;
            TextureAtlasSprite texture = baker.findSprite(slots, mat.diffuseColorMap);
            int tintIndex = mat.diffuseTintIndex;
            Vector4f colorTint = mat.diffuseColor;

            var rootTransform = additionalProperties.getOrDefault(NeoForgeModelProperties.TRANSFORM, Transformation.identity());
            var transform = rootTransform.isIdentity() ? state.getRotation() : state.getRotation().compose(rootTransform);
            for (int[][] face : faces) {
                Pair<BakedQuad, Direction> quad = makeQuad(face, tintIndex, colorTint, mat.ambientColor, texture, transform);
                if (quad.getRight() == null)
                    builder.addUnculledFace(quad.getLeft());
                else
                    builder.addCulledFace(quad.getRight(), quad.getLeft());
            }
        }
    }

    public record ModelSettings(ResourceLocation modelLocation,
            boolean automaticCulling, boolean shadeQuads, boolean flipV,
            boolean emissiveAmbient, @Nullable String mtlOverride,
            StandardModelParameters parameters) {}
}
