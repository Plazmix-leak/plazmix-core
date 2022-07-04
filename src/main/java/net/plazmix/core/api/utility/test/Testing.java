package net.plazmix.core.api.utility.test;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.plazmix.core.api.utility.test.impl.AsynchronousTestingFactory;
import net.plazmix.core.api.utility.test.impl.ThreadTestingFactory;

@RequiredArgsConstructor
public enum Testing {

    ASYNCHRONOUS(
            new AsynchronousTestingFactory()
    ),

    THREAD(
            new ThreadTestingFactory()
    );


    @Getter
    private final TestingFactory factory;
}
