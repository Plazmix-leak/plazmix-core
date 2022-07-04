package net.plazmix.core.api.module;

import lombok.NonNull;

public interface CoreModuleHandler {

    void handle(@NonNull  CoreModule coreModule);
}
