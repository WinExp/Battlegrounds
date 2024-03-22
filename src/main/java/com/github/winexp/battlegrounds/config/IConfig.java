package com.github.winexp.battlegrounds.config;

import com.mojang.serialization.Codec;

public interface IConfig<T extends Record> {
    Codec<T> getCodec();
}
