package com.iafenvoy.uranus.client.render;

import com.iafenvoy.uranus.client.model.ITabulaModelAnimator;
import com.iafenvoy.uranus.client.model.TabulaModel;
import com.iafenvoy.uranus.client.model.tabula.TabulaModelContainer;

@Deprecated(forRemoval = true)
public class TabulaModelAccessor extends TabulaModel {
    public TabulaModelAccessor(TabulaModelContainer container, ITabulaModelAnimator tabulaAnimator) {
        super(container, tabulaAnimator);
    }

    public TabulaModelAccessor(TabulaModelContainer container) {
        super(container);
    }
}
