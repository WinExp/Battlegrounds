package com.github.winexp.battlegrounds.client.toast.vote;

import com.github.winexp.battlegrounds.client.toast.SimpleToast;
import com.github.winexp.battlegrounds.util.time.Duration;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public enum ToastType implements SimpleToast.Type {
    OPENED(Duration.ofSeconds(5)), CLOSED(Duration.ofSeconds(5));

    private final Duration displayDuration;

    ToastType(Duration displayDuration) {
        this.displayDuration = displayDuration;
    }

    @Override
    public Duration getDisplayDuration() {
        return this.displayDuration;
    }
}
