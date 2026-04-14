package com.kintoh.domain;

import java.util.Optional;

public interface Monitor<T extends Resource> {
    Optional<Event> check(T resource);
}
