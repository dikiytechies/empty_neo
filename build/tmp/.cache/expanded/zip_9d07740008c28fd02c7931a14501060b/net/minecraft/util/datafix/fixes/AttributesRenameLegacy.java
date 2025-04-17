package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

public class AttributesRenameLegacy extends DataFix {
    private final String name;
    private final UnaryOperator<String> renames;

    public AttributesRenameLegacy(Schema p_364342_, String p_364033_, UnaryOperator<String> p_364098_) {
        super(p_364342_, false);
        this.name = p_364033_;
        this.renames = p_364098_;
    }

    @Override
    protected TypeRewriteRule makeRule() {
        Type<?> type = this.getInputSchema().getType(References.ITEM_STACK);
        OpticFinder<?> opticfinder = type.findField("tag");
        return TypeRewriteRule.seq(
            this.fixTypeEverywhereTyped(this.name + " (ItemStack)", type, p_362243_ -> p_362243_.updateTyped(opticfinder, this::fixItemStackTag)),
            this.fixTypeEverywhereTyped(this.name + " (Entity)", this.getInputSchema().getType(References.ENTITY), this::fixEntity),
            this.fixTypeEverywhereTyped(this.name + " (Player)", this.getInputSchema().getType(References.PLAYER), this::fixEntity)
        );
    }

    private Dynamic<?> fixName(Dynamic<?> p_363449_) {
        return DataFixUtils.orElse(p_363449_.asString().result().map(this.renames).map(p_363449_::createString), p_363449_);
    }

    private Typed<?> fixItemStackTag(Typed<?> p_360553_) {
        return p_360553_.update(
            DSL.remainderFinder(),
            p_362693_ -> p_362693_.update(
                    "AttributeModifiers",
                    p_364029_ -> DataFixUtils.orElse(
                            p_364029_.asStreamOpt()
                                .result()
                                .map(p_361493_ -> p_361493_.map(p_361431_ -> p_361431_.update("AttributeName", this::fixName)))
                                .map(p_364029_::createList),
                            p_364029_
                        )
                )
        );
    }

    private Typed<?> fixEntity(Typed<?> p_363266_) {
        return p_363266_.update(
            DSL.remainderFinder(),
            p_361860_ -> p_361860_.update(
                    "Attributes",
                    p_364503_ -> DataFixUtils.orElse(
                            p_364503_.asStreamOpt()
                                .result()
                                .map(p_365062_ -> p_365062_.map(p_363253_ -> p_363253_.update("Name", this::fixName)))
                                .map(p_364503_::createList),
                            p_364503_
                        )
                )
        );
    }
}
