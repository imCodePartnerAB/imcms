package com.imcode.imcms.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public abstract class SpaceAround implements Serializable {

    private static final long serialVersionUID = 3471777360335790593L;

    public SpaceAround(SpaceAround from) {
        setBottom(from.getBottom());
        setLeft(from.getLeft());
        setRight(from.getRight());
        setTop(from.getTop());
    }

    public abstract int getTop();

    public abstract void setTop(int topSpace);

    public abstract int getRight();

    public abstract void setRight(int rightSpace);

    public abstract int getBottom();

    public abstract void setBottom(int bottomSpace);

    public abstract int getLeft();

    public abstract void setLeft(int leftSpace);
}
