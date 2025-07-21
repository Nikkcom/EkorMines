package me.ekorn.EkorMines.menu.domain;

import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;

public final class DefaultSlotPatterns {
    private DefaultSlotPatterns() {}

    public static final SlotPattern HEADER = (rows, page) ->
            IntStream.range(0, 9)
                    .boxed()
                    .toList();

    public static final SlotPattern FOOTER = (rows, page) -> {
        int start = (rows - 1) * 9;
        return IntStream.range(start, start + 9)
                .boxed()
                .toList();
    };

    public static final SlotPattern BODY = (rows, page) -> {
        if (rows <= 2) {
            return List.of();
        }

        return IntStream.range(1, rows - 1)
                .mapToObj(r ->
                        IntStream.range(r * 9, r * 9 + 9).boxed()
                )
                .flatMap(Function.identity())
                .toList();
    };

    public static final SlotPattern FULL = BODY
            .withOffset(0, 0)
            .withOffset(0, 0)
            .withOffset(0, 0);
}
