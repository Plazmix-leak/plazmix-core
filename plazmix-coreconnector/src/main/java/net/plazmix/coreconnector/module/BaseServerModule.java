package net.plazmix.coreconnector.module;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public abstract class BaseServerModule {

    protected final String name;
    protected final BaseModuleContainer container = new BaseModuleContainer(this);

    public void onValueRead(ModuleExecuteType executeType, String key, Object value) {
    }

}
