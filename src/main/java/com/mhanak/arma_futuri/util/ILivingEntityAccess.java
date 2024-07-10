package com.mhanak.arma_futuri.util;

import net.minecraft.entity.Entity;

public interface ILivingEntityAccess {
    Entity getAttachedEntity();
    void setAttachedEntity(Entity attachedEntity);

    Entity getAttachmentEntity();
    void setAttachmentEntity(Entity attachmentEntity);

    void attachToEntity(Entity entity);
    void detachFromEntity();

    //TrackedData<Optional<Entity>> getAttachedEntityTrackedData();
    //TrackedData<Optional<Entity>> getAttachmentEntityTrackedData();
}
